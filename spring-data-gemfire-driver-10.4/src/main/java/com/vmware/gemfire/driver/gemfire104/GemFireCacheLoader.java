/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.LoaderHelper;

import com.vmware.gemfire.driver.spi.DriverCacheLoader;
import com.vmware.gemfire.driver.spi.DriverFailureKind;
import com.vmware.gemfire.driver.spi.DriverOperationException;
import com.vmware.gemfire.driver.spi.GemFireDriverException;
import com.vmware.gemfire.driver.spi.DriverLoaderHelper;
import com.vmware.gemfire.driver.spi.DriverRegion;

/** The native callback boundary; no native helper or region escapes into application callbacks. */
final class GemFireCacheLoader<K, V> implements CacheLoader<K, V> {

	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	private final DriverCacheLoader<K, V> delegate;

	GemFireCacheLoader(DriverCacheLoader<K, V> delegate) { this.delegate = delegate; }

	DriverCacheLoader<K, V> delegate() { return delegate; }

	@Override public void init(java.util.Properties properties) {
		try { delegate.init(properties); }
		catch (RuntimeException failure) { throw exceptions.translate("Loader.init", failure); }
	}

	@Override public void initialize(org.apache.geode.cache.Cache cache, java.util.Properties properties) {
		try { delegate.initialize(new GemFireConnection(cache), properties); }
		catch (RuntimeException failure) { throw exceptions.translate("Loader.initialize", failure); }
	}

	@Override
	public V load(LoaderHelper<K, V> helper) {
		try {
			return delegate.load(new DriverLoaderHelper<>() {
				@Override public K getKey() { return helper.getKey(); }
				@Override public DriverRegion<K, V> getRegion() { return new GemFireRegion<>(helper.getRegion()); }
				@Override public Object getArgument() { return helper.getArgument(); }
			});
		}
		catch (GemFireDriverException failure) { throw failure; }
		catch (RuntimeException failure) {
			throw new DriverOperationException(DriverFailureKind.LOADER_FAILURE, "Cache loader failed", failure);
		}
	}

	@Override
	public void close() {
		try { delegate.close(); }
		catch (GemFireDriverException failure) { throw failure; }
		catch (RuntimeException failure) {
			throw new DriverOperationException(DriverFailureKind.LOADER_FAILURE, "Cache loader cleanup failed", failure);
		}
	}
}
