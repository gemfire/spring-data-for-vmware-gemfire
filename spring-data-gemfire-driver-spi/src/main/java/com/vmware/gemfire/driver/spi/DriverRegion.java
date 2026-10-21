/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A thin region handle. Values, failures and concurrency follow the underlying native region. */
public interface DriverRegion<K, V> {

	/** General callbacks are managed independently of resolver lifecycle registrations.
	 * Identity, order, replacement and cleanup follow native registration semantics.
	 * Null removes writers/loaders; null listeners are invalid. */
	default void addCacheListener(DriverCacheListener<K, V> listener) {
		throw UnsupportedFeatureException.notIntroduced("Region.addCacheListener");
	}

	default void removeCacheListener(DriverCacheListener<K, V> listener) {
		throw UnsupportedFeatureException.notIntroduced("Region.removeCacheListener");
	}

	/** Replace the general listener list. Retained instances are closed too, as in native init.
	 * A null or empty list removes all general listeners; resolver lifecycle registrations remain active.
	 * Duplicate list entries are retained. Registration adds no atomicity guarantee.
	 */
	default void initCacheListeners(java.util.List<DriverCacheListener<K, V>> listeners) {
		throw UnsupportedFeatureException.notIntroduced("Region.initCacheListeners");
	}

	default java.util.List<DriverCacheListener<K, V>> getCacheListeners() {
		throw UnsupportedFeatureException.notIntroduced("Region.getCacheListeners");
	}

	default void setCacheWriter(DriverCacheWriter<K, V> writer) {
		throw UnsupportedFeatureException.notIntroduced("Region.setCacheWriter");
	}

	default DriverCacheWriter<K, V> getCacheWriter() {
		throw UnsupportedFeatureException.notIntroduced("Region.getCacheWriter");
	}

	default DriverCacheLoader<K, V> getCacheLoader() {
		throw UnsupportedFeatureException.notIntroduced("Region.getCacheLoader");
	}

	default void invalidate(K key, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.invalidate");
	}

	default void invalidateRegion(Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.invalidateRegion");
	}

	default V destroy(K key, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.destroy");
	}

	default void destroyRegion(Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.destroyRegion");
	}

	default V put(K key, V value, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.putWithArgument");
	}

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
