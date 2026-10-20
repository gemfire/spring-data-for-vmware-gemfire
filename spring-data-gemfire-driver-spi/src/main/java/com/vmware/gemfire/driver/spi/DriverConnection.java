/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Set;

import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;
import com.vmware.gemfire.driver.spi.config.RegionShortcut;

/**
 * A cache connection owned by the caller, which must close it. Operations delegate to the underlying
 * cache without adding thread-safety guarantees. Default methods preserve compatibility with older
 * drivers and fail explicitly when a feature has not been implemented.
 */
public interface DriverConnection extends AutoCloseable {

	/**
	 * Creates a named pool on an open CLIENT connection. Requires a nonblank name and nonnull
	 * configuration. Duplicate creation fails; it never adopts an existing pool. Native registry
	 * visibility can span compatible connection handles. Failures use DriverOperationException.
	 */
	default DriverPool createPool(String name, PoolConfiguration configuration) {
		throw UnsupportedFeatureException.notIntroduced("Connection.createPool");
	}

	/** Named lookup on an open CLIENT connection with a nonblank name; null means absence only. */
	default DriverPool getPool(String name) {
		throw UnsupportedFeatureException.notIntroduced("Connection.getPool");
	}

	/** Returns the root regions currently defined in this cache. */
	default Set<DriverRegion<?, ?>> rootRegions() {
		throw UnsupportedFeatureException.notIntroduced("Connection.rootRegions");
	}

	default <K, V> DriverRegion<K, V> createRegion(String name, RegionShortcut shortcut) {
		throw UnsupportedFeatureException.notIntroduced("Connection.createRegion");
	}

	default <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut) {
		throw UnsupportedFeatureException.notIntroduced("Connection.createClientRegion");
	}

	/** Returns {@code null} when no region exists at this name or path. */
	default <K, V> DriverRegion<K, V> getRegion(String name) {
		throw UnsupportedFeatureException.notIntroduced("Connection.getRegion");
	}

	default boolean isClosed() {
		throw UnsupportedFeatureException.notIntroduced("Connection.isClosed");
	}

	@Override
	default void close() {
		throw UnsupportedFeatureException.notIntroduced("Connection.close");
	}
}
