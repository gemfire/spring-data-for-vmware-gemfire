/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.function.Supplier;

import org.apache.geode.cache.Region;

import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;
import com.vmware.gemfire.driver.spi.DriverCacheLoader;

final class GemFireRegion<K, V> implements DriverRegion<K, V> {

	private final Region<K, V> region;

	GemFireRegion(Region<K, V> region) {
		this.region = region;
	}

	@Override
	public void addLifecycleListener(DriverRegionLifecycleListener listener) {
		invoke("addLifecycleListener", () -> {
			region.getAttributesMutator().addCacheListener(new GemFireRegionLifecycleListener<>(listener));
			return null;
		});
	}

	@Override
	public boolean isDestroyed() { return invoke("isDestroyed", region::isDestroyed); }

	@Override
	public void clear() { invoke("clear", () -> { region.clear(); return null; }); }

	@Override
	public V putIfAbsent(K key, V value) { return invoke("putIfAbsent", () -> region.putIfAbsent(key, value)); }

	@Override
	public String name() { return invoke("name", region::getName); }

	@Override
	public V get(K key) { return invoke("get", () -> region.get(key)); }

	@Override
	public V get(K key, Object argument) { return invoke("get", () -> region.get(key, argument)); }

	@Override
	public void setCacheLoader(DriverCacheLoader<K, V> loader) {
		invoke("setCacheLoader", () -> {
			var current = region.getAttributes().getCacheLoader();
			// Preserve native same-callback identity semantics across separate SPI handles.
			if (current instanceof GemFireCacheLoader<?, ?> adapter && adapter.delegate() == loader) {
				region.getAttributesMutator().setCacheLoader(current);
				return null;
			}
			region.getAttributesMutator().setCacheLoader(loader == null ? null : new GemFireCacheLoader<>(loader));
			return null;
		});
	}

	@Override
	public V put(K key, V value) { return invoke("put", () -> region.put(key, value)); }

	@Override
	public V remove(K key) { return invoke("remove", () -> region.remove(key)); }

	@Override
	public boolean containsKey(K key) { return invoke("containsKey", () -> region.containsKey(key)); }

	@Override
	public int size() { return invoke("size", region::size); }

	private <T> T invoke(String operation, Supplier<T> action) {
		try {
			return action.get();
		}
		catch (RuntimeException ex) {
			// Do not embed keys, values or native exception messages in boundary diagnostics.
			throw new DriverOperationException("GemFire region operation failed: " + operation, ex);
		}
	}
}
