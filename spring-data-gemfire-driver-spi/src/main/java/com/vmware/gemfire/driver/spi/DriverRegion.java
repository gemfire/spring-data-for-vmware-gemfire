/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A thin region handle. Values, failures and concurrency follow the underlying native region. */
public interface DriverRegion<K, V> {

	/**
	 * Registers a nonnull lifecycle listener. Re-registering the same listener instance on the
	 * same underlying region is a no-op, including through another handle. Distinct instances
	 * are independent registrations. The region owns registration cleanup; callers do not own
	 * the region through this callback. Notification and cleanup timing follow the driver.
	 */
	default void addLifecycleListener(DriverRegionLifecycleListener listener) {
		throw UnsupportedFeatureException.notIntroduced("Region.addLifecycleListener");
	}

	/** Install or replace a loader; null removes it. Lifecycle follows the native region. */
	default void setCacheLoader(DriverCacheLoader<K, V> loader) {
		throw UnsupportedFeatureException.notIntroduced("Region.setCacheLoader");
	}

	/** Get a value, passing the argument to a loader when the native engine invokes it. */
	default V get(K key, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.getWithArgument");
	}

	default boolean isDestroyed() {
		throw UnsupportedFeatureException.notIntroduced("Region.isDestroyed");
	}

	default void clear() {
		throw UnsupportedFeatureException.notIntroduced("Region.clear");
	}

	/** Delegates to the native region's put-if-absent operation and returns the previous value. */
	default V putIfAbsent(K key, V value) {
		throw UnsupportedFeatureException.notIntroduced("Region.putIfAbsent");
	}

	default String name() {
		throw UnsupportedFeatureException.notIntroduced("Region.name");
	}

	default V get(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.get");
	}

	default V put(K key, V value) {
		throw UnsupportedFeatureException.notIntroduced("Region.put");
	}

	default V remove(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.remove");
	}

	default boolean containsKey(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.containsKey");
	}

	default int size() {
		throw UnsupportedFeatureException.notIntroduced("Region.size");
	}
}
