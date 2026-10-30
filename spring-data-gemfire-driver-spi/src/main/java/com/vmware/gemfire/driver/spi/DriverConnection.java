/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Set;

import com.vmware.gemfire.driver.spi.config.ClientRegionShortcut;

/**
 * A cache connection owned by the caller, which must close it. Operations delegate to the underlying
 * cache without adding thread-safety guarantees. Default methods preserve compatibility with older
 * drivers and fail explicitly when a feature has not been implemented.
 */
public interface DriverConnection extends DriverRegionService {

	/**
	 * Detached, immutable snapshot of server endpoints currently observed as connected by the
	 * provider. This is not configured topology or an active health/authentication probe; endpoints
	 * can disappear immediately afterward. Native 10.4 observes all pools in its JVM registry,
	 * including through a retained closed-cache handle. No connection isolation is promised.
	 * The mock always returns empty because it has no network connections.
	 */
	default Set<PoolConfiguration.Endpoint> getCurrentServers() {
		throw UnsupportedFeatureException.notIntroduced("Connection.getCurrentServers");
	}

	/** Local runtime metadata; does not test whether a server can be reached. */
	default DriverClientRuntime getRuntime() {
		throw UnsupportedFeatureException.notIntroduced("Connection.getRuntime");
	}

	/** Cache cancellation criterion, deliberately separate from distributed-system cancellation. */
	default DriverCancellation getCancellation() {
		throw UnsupportedFeatureException.notIntroduced("Connection.getCancellation");
	}

	default DriverTransactionManager getTransactionManager() {
		throw UnsupportedFeatureException.notIntroduced("Connection.getTransactionManager");
	}

	default <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionConfiguration<K, V> configuration) {
		throw UnsupportedFeatureException.notIntroduced("Connection.createConfiguredClientRegion");
	}


	/** Client creation supports listeners. Native 10.4 cannot install a loader/writer before creation;
	 * use the region setters afterward, or receive an explicit unsupported failure here.
	 */
	default <K, V> DriverRegion<K, V> createClientRegion(String name, ClientRegionShortcut shortcut, RegionCallbacks<K, V> callbacks) {
		throw UnsupportedFeatureException.notIntroduced("Connection.createClientRegionWithCallbacks");
	}


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

	/**
	 * Returns the pool the cache selected as its default, or null when native selection finds none
	 * (several pools exist and none is compatible with an implicit default). Selection is lazy and
	 * is not name based: the result need not be called {@code DEFAULT} or {@code gemfirePool}.
	 * Requires an open CLIENT connection.
	 */
	default DriverPool getDefaultPool() {
		throw UnsupportedFeatureException.notIntroduced("Connection.getDefaultPool");
	}

	/** Returns the root regions currently defined in this cache. */
	default Set<DriverRegion<?, ?>> rootRegions() {
		throw UnsupportedFeatureException.notIntroduced("Connection.rootRegions");
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
