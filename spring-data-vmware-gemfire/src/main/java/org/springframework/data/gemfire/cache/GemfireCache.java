/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.cache;

import java.util.concurrent.Callable;

import com.vmware.gemfire.driver.spi.DriverRegion;

import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

/**
 * Spring Cache adapter backed by an injected Driver SPI region. The region and its connection are
 * owned by the caller. Null values are not cached, matching the native GemFire adapter's behavior.
 */
public class GemfireCache implements Cache {

	private final DriverRegion<Object, Object> region;

	@SuppressWarnings("unchecked")
	public GemfireCache(DriverRegion<?, ?> region) {
		Assert.notNull(region, "GemFire Region must not be null");
		this.region = (DriverRegion<Object, Object>) region;
	}

	/** Returns the SPI region handle; native engine types stay inside the driver. */
	@Override
	public DriverRegion<Object, Object> getNativeCache() { return region; }

	@Override
	public String getName() { return region.name(); }

	@Override
	public void clear() { region.clear(); }

	@Override
	public void evict(Object key) { region.remove(key); }

	@Override
	public ValueWrapper get(Object key) {
		Object value = region.get(key);
		return value != null ? new SimpleValueWrapper(value) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Class<T> type) {
		Object value = region.get(key);
		if (value != null && type != null && !type.isInstance(value)) {
			throw new IllegalStateException(String.format(
				"Cached value [%1$s] is not an instance of type [%2$s]", value, type.getName()));
		}
		return (T) value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T get(Object key, Callable<T> valueLoader) {
		T value = (T) get(key, Object.class);
		if (value == null) {
			synchronized (region) {
				value = (T) get(key, Object.class);
				if (value == null) {
					try {
						value = valueLoader.call();
						put(key, value);
					} catch (Exception ex) {
						throw new ValueRetrievalException(key, valueLoader, ex);
					}
				}
			}
		}
		return value;
	}

	@Override
	public void put(Object key, Object value) {
		if (value != null) region.put(key, value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		Object existing = region.putIfAbsent(key, value);
		return existing != null ? new SimpleValueWrapper(existing) : null;
	}
}
