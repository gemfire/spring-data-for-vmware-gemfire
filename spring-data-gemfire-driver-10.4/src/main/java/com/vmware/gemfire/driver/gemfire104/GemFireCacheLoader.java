/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.LoaderHelper;

import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.DriverRegion;

/** The native callback boundary; no native helper or region escapes into application callbacks. */
final class GemFireCacheLoader<K, V> implements CacheLoader<K, V> {

	private final DriverCacheLoader<K, V> delegate;

	GemFireCacheLoader(DriverCacheLoader<K, V> delegate) { this.delegate = delegate; }

	DriverCacheLoader<K, V> delegate() { return delegate; }

	@Override
	public V load(LoaderHelper<K, V> helper) {
		return delegate.load(new DriverLoaderHelper<>() {
			@Override public K getKey() { return helper.getKey(); }
			@Override public DriverRegion<K, V> getRegion() { return new GemFireRegion<>(helper.getRegion()); }
			@Override public Object getArgument() { return helper.getArgument(); }
			@Override public V netSearch(boolean doNetLoad) {
				try {
					return helper.netSearch(doNetLoad);
				}
				catch (RuntimeException ex) {
					throw new DriverOperationException("GemFire loader operation failed: netSearch", ex);
				}
			}
		});
	}

	@Override
	public void close() { delegate.close(); }
}
