/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A thin region handle. Values, failures and concurrency follow the underlying native region. */
public interface DriverRegion<K, V> {
	default DriverRegionAttributes<K, V> getAttributes() {
		throw UnsupportedFeatureException.notIntroduced("Region.getAttributes");
	}
	default DriverRegionAttributesMutator<K, V> getAttributesMutator() {
		throw UnsupportedFeatureException.notIntroduced("Region.getAttributesMutator");
	}
	default <SK, SV> DriverRegion<SK, SV> createClientSubregion(String name,
		com.vmware.gemfire.driver.spi.config.ClientRegionShortcut shortcut, RegionConfiguration<SK, SV> configuration) {
		throw UnsupportedFeatureException.notIntroduced("Region.createClientSubregion");
	}


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

	/** Native region path separators. */
	String SEPARATOR = "/";
	char SEPARATOR_CHAR = '/';

	default void create(K key, V value) {
		throw UnsupportedFeatureException.notIntroduced("Region.create");
	}

	default void create(K key, V value, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.create");
	}

	default V destroy(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.destroy");
	}

	default void invalidate(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.invalidate");
	}

	default void destroyRegion() {
		throw UnsupportedFeatureException.notIntroduced("Region.destroyRegion");
	}

	default void invalidateRegion() {
		throw UnsupportedFeatureException.notIntroduced("Region.invalidateRegion");
	}

	/** Delegates native Region.close(). Named explicitly so Spring does not infer bean ownership. */
	default void closeRegion() {
		throw UnsupportedFeatureException.notIntroduced("Region.closeRegion");
	}

	default boolean containsValue(Object value) {
		throw UnsupportedFeatureException.notIntroduced("Region.containsValue");
	}

	default boolean containsValueForKey(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.containsValueForKey");
	}

	default boolean isEmpty() {
		throw UnsupportedFeatureException.notIntroduced("Region.isEmpty");
	}

	default long sizeLong() {
		throw UnsupportedFeatureException.notIntroduced("Region.sizeLong");
	}

	default V replace(K key, V value) {
		throw UnsupportedFeatureException.notIntroduced("Region.replace");
	}

	default boolean replace(K key, V oldValue, V newValue) {
		throw UnsupportedFeatureException.notIntroduced("Region.replace");
	}

	default boolean remove(K key, Object value) {
		throw UnsupportedFeatureException.notIntroduced("Region.remove");
	}

	default V getOrDefault(K key, V defaultValue) {
		throw UnsupportedFeatureException.notIntroduced("Region.getOrDefault");
	}

	default V compute(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> function) {
		throw UnsupportedFeatureException.notIntroduced("Region.compute");
	}

	default V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> function) {
		throw UnsupportedFeatureException.notIntroduced("Region.computeIfAbsent");
	}

	default V computeIfPresent(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> function) {
		throw UnsupportedFeatureException.notIntroduced("Region.computeIfPresent");
	}

	default V merge(K key, V value, java.util.function.BiFunction<? super V, ? super V, ? extends V> function) {
		throw UnsupportedFeatureException.notIntroduced("Region.merge");
	}

	default void forEach(java.util.function.BiConsumer<? super K, ? super V> action) {
		throw UnsupportedFeatureException.notIntroduced("Region.forEach");
	}

	default void replaceAll(java.util.function.BiFunction<? super K, ? super V, ? extends V> function) {
		throw UnsupportedFeatureException.notIntroduced("Region.replaceAll");
	}

	default void putAll(java.util.Map<? extends K, ? extends V> values) {
		throw UnsupportedFeatureException.notIntroduced("Region.putAll");
	}

	default void putAll(java.util.Map<? extends K, ? extends V> values, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.putAll");
	}

	default java.util.Map<K, V> getAll(java.util.Collection<? extends K> keys) {
		throw UnsupportedFeatureException.notIntroduced("Region.getAll");
	}

	default <T extends K> java.util.Map<T, V> getAll(java.util.Collection<T> keys, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.getAll");
	}

	default void removeAll(java.util.Collection<? extends K> keys) {
		throw UnsupportedFeatureException.notIntroduced("Region.removeAll");
	}

	default void removeAll(java.util.Collection<? extends K> keys, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.removeAll");
	}

	default void localClear() {
		throw UnsupportedFeatureException.notIntroduced("Region.localClear");
	}

	default void localDestroy(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.localDestroy");
	}

	default void localDestroy(K key, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.localDestroy");
	}

	default void localInvalidate(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.localInvalidate");
	}

	default void localInvalidate(K key, Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.localInvalidate");
	}

	default void localDestroyRegion() {
		throw UnsupportedFeatureException.notIntroduced("Region.localDestroyRegion");
	}

	default void localDestroyRegion(Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.localDestroyRegion");
	}

	default void localInvalidateRegion() {
		throw UnsupportedFeatureException.notIntroduced("Region.localInvalidateRegion");
	}

	default void localInvalidateRegion(Object argument) {
		throw UnsupportedFeatureException.notIntroduced("Region.localInvalidateRegion");
	}

	default String getFullPath() {
		throw UnsupportedFeatureException.notIntroduced("Region.getFullPath");
	}

	default DriverRegion<?, ?> getParentRegion() {
		throw UnsupportedFeatureException.notIntroduced("Region.getParentRegion");
	}

	default DriverRegion<?, ?> getSubregion(String path) {
		throw UnsupportedFeatureException.notIntroduced("Region.getSubregion");
	}

	default java.util.Set<DriverRegion<?, ?>> subregions(boolean recursive) {
		throw UnsupportedFeatureException.notIntroduced("Region.subregions");
	}

	default DriverRegionEntry<K, V> getEntry(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.getEntry");
	}

	default java.util.Set<K> keySet() {
		throw UnsupportedFeatureException.notIntroduced("Region.keySet");
	}

	default java.util.Collection<V> values() {
		throw UnsupportedFeatureException.notIntroduced("Region.values");
	}

	default java.util.Set<java.util.Map.Entry<K, V>> entrySet() {
		throw UnsupportedFeatureException.notIntroduced("Region.entrySet");
	}

	default java.util.Set<DriverRegionEntry<?, ?>> entrySet(boolean recursive) {
		throw UnsupportedFeatureException.notIntroduced("Region.entrySet");
	}

	default DriverRegionService getRegionService() {
		throw UnsupportedFeatureException.notIntroduced("Region.getRegionService");
	}

	default DriverConnection getCache() {
		throw UnsupportedFeatureException.notIntroduced("Region.getCache");
	}

	default Object getUserAttribute() {
		throw UnsupportedFeatureException.notIntroduced("Region.getUserAttribute");
	}

	default void setUserAttribute(Object value) {
		throw UnsupportedFeatureException.notIntroduced("Region.setUserAttribute");
	}

	default DriverCacheStatistics getStatistics() {
		throw UnsupportedFeatureException.notIntroduced("Region.getStatistics");
	}

	default RegionMetadata metadata() {
		throw UnsupportedFeatureException.notIntroduced("Region.metadata");
	}

	default boolean containsKeyOnServer(K key) {
		throw UnsupportedFeatureException.notIntroduced("Region.containsKeyOnServer");
	}

	default java.util.Set<K> keySetOnServer() {
		throw UnsupportedFeatureException.notIntroduced("Region.keySetOnServer");
	}

	default int sizeOnServer() {
		throw UnsupportedFeatureException.notIntroduced("Region.sizeOnServer");
	}

	default long sizeLongOnServer() {
		throw UnsupportedFeatureException.notIntroduced("Region.sizeLongOnServer");
	}

	default boolean isEmptyOnServer() {
		throw UnsupportedFeatureException.notIntroduced("Region.isEmptyOnServer");
	}

}
