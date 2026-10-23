/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.function.Supplier;
import java.util.List;
import java.util.ArrayList;
import org.apache.geode.cache.CacheListener;
import com.vmware.gemfire.driver.spi.*;

import org.apache.geode.cache.Region;

import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverRegionLifecycleListener;
import com.vmware.gemfire.driver.spi.DriverCacheLoader;

final class GemFireRegion<K, V> implements DriverRegion<K, V> {

	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	final Region<K, V> region;

	GemFireRegion(Region<K, V> region) {
		this.region = region;
	}

	@Override public DriverRegionAttributes<K, V> getAttributes() { return invoke("getAttributes", () -> new GemFireRegionAttributes<>(this)); }
	@Override public DriverRegionAttributesMutator<K, V> getAttributesMutator() { return invoke("getAttributesMutator", () -> new GemFireRegionAttributesMutator<>(this)); }
	@Override public <SK, SV> DriverRegion<SK, SV> createClientSubregion(String name,
		com.vmware.gemfire.driver.spi.config.ClientRegionShortcut shortcut, RegionConfiguration<SK, SV> configuration) {
		return invoke("createClientSubregion", () -> {
			if (!((org.apache.geode.internal.cache.InternalCache) region.getCache()).isClient()) throw new IllegalStateException("Client subregion requires a client cache");
			var factory = ((org.apache.geode.cache.client.ClientCache) region.getCache()).<SK, SV>createClientRegionFactory(org.apache.geode.cache.client.ClientRegionShortcut.valueOf(shortcut.name()));
			new GemFireRegionConfiguration().apply(configuration, factory);
			return new GemFireRegion<>(factory.createSubregion(region, name));
		});
	}

	@Override public void addCacheListener(DriverCacheListener<K, V> listener) {
		invoke("addCacheListener", () -> { region.getAttributesMutator().addCacheListener(new GemFireCacheListenerFactory().adapt(listener)); return null; });
	}
	@Override public void removeCacheListener(DriverCacheListener<K, V> listener) {
		invoke("removeCacheListener", () -> { region.getAttributesMutator().removeCacheListener(new GemFireCacheListenerFactory().adapt(listener)); return null; });
	}
	@Override public void initCacheListeners(List<DriverCacheListener<K, V>> listeners) {
		invoke("initCacheListeners", () -> {
			List<CacheListener<K, V>> replacements = new ArrayList<>();
			for (DriverCacheListener<K, V> listener : listeners == null ? List.<DriverCacheListener<K, V>>of() : listeners) replacements.add(new GemFireCacheListenerFactory().adapt(listener));
			// Resolver callbacks are independent registrations, not application cache listeners.
			for (CacheListener<K, V> listener : region.getAttributes().getCacheListeners()) {
				if (listener instanceof GemFireRegionLifecycleListener<?, ?>) {
					// The lifecycle adapter closes its delegate only when the region lifetime ends.
					replacements.add(listener);
				}
			}
			@SuppressWarnings("unchecked") CacheListener<K, V>[] array = replacements.toArray(CacheListener[]::new);
			region.getAttributesMutator().initCacheListeners(array);
			return null;
		});
	}
	@Override public List<DriverCacheListener<K, V>> getCacheListeners() {
		return invoke("getCacheListeners", () -> {
			List<DriverCacheListener<K, V>> listeners = new ArrayList<>();
			for (CacheListener<K, V> listener : region.getAttributes().getCacheListeners()) {
				Object application = listener;
				if (application instanceof GemFireCacheListener<?, ?> adapter) {
					@SuppressWarnings("unchecked") DriverCacheListener<K, V> callback = (DriverCacheListener<K, V>) adapter.delegate;
					listeners.add(callback);
				}
				else if (!(listener instanceof GemFireRegionLifecycleListener)) throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheListener");
			}
			return List.copyOf(listeners);
		});
	}
	@Override public void setCacheWriter(DriverCacheWriter<K, V> writer) {
		invoke("setCacheWriter", () -> {
			var current = region.getAttributes().getCacheWriter();
			if (current instanceof GemFireCacheWriter<?, ?> adapter && adapter.delegate == writer) {
				region.getAttributesMutator().setCacheWriter(current);
			} else region.getAttributesMutator().setCacheWriter(writer == null ? null : new GemFireCacheWriter<>(writer));
			return null;
		});
	}
	@Override public DriverCacheWriter<K, V> getCacheWriter() {
		return invoke("getCacheWriter", () -> {
			var current = region.getAttributes().getCacheWriter();
			if (current == null) return null;
			if (current instanceof GemFireCacheWriter<K, V> adapter) return adapter.delegate;
			throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheWriter");
		});
	}
	@Override public DriverCacheLoader<K, V> getCacheLoader() {
		return invoke("getCacheLoader", () -> {
			var current = region.getAttributes().getCacheLoader();
			if (current == null) return null;
			if (current instanceof GemFireCacheLoader<K, V> adapter) return adapter.delegate();
			throw UnsupportedFeatureException.notIntroduced("Region.nativeCacheLoader");
		});
	}
	@Override public V put(K key, V value, Object argument) { return invoke("put", () -> region.put(key, value, argument)); }
	@Override public void invalidate(K key, Object argument) { invoke("invalidate", () -> { region.invalidate(key, argument); return null; }); }
	@Override public V destroy(K key, Object argument) { return invoke("destroy", () -> region.destroy(key, argument)); }
	@Override public void invalidateRegion(Object argument) { invoke("invalidateRegion", () -> { region.invalidateRegion(argument); return null; }); }
	@Override public void destroyRegion(Object argument) { invoke("destroyRegion", () -> { region.destroyRegion(argument); return null; }); }

	@Override
	public void addLifecycleListener(DriverRegionLifecycleListener listener) {
		invoke("addLifecycleListener", () -> {
			region.getAttributesMutator().addCacheListener(new GemFireRegionLifecycleListener<>(listener, region));
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
			throw exceptions.translate(operation, ex);
		}
	}
	@Override public void create(K key, V value) { invoke("create", () -> { region.create(key, value); return null; }); }
	@Override public void create(K key, V value, Object argument) { invoke("create", () -> { region.create(key, value, argument); return null; }); }
	@Override public V destroy(K key) { return invoke("destroy", () -> region.destroy(key)); }
	@Override public void invalidate(K key) { invoke("invalidate", () -> { region.invalidate(key); return null; }); }
	@Override public void destroyRegion() { invoke("destroyRegion", () -> { region.destroyRegion(); return null; }); }
	@Override public void invalidateRegion() { invoke("invalidateRegion", () -> { region.invalidateRegion(); return null; }); }
	@Override public void closeRegion() { invoke("close", () -> { region.close(); return null; }); }
	@Override public boolean containsValue(Object value) { return invoke("containsValue", () -> region.containsValue(value)); }
	@Override public boolean containsValueForKey(K key) { return invoke("containsValueForKey", () -> region.containsValueForKey(key)); }
	@Override public boolean isEmpty() { return invoke("isEmpty", () -> region.isEmpty()); }
	@Override public long sizeLong() { return invoke("sizeLong", () -> region.sizeLong()); }
	@Override public V replace(K key, V value) { return invoke("replace", () -> region.replace(key, value)); }
	@Override public boolean replace(K key, V oldValue, V newValue) { return invoke("replace", () -> region.replace(key, oldValue, newValue)); }
	@Override public boolean remove(K key, Object value) { return invoke("remove", () -> region.remove(key, value)); }
	@Override public V getOrDefault(K key, V defaultValue) { return invoke("getOrDefault", () -> region.getOrDefault(key, defaultValue)); }
	@Override public V compute(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> function) { return invoke("compute", () -> region.compute(key, function)); }
	@Override public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> function) { return invoke("computeIfAbsent", () -> region.computeIfAbsent(key, function)); }
	@Override public V computeIfPresent(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> function) { return invoke("computeIfPresent", () -> region.computeIfPresent(key, function)); }
	@Override public V merge(K key, V value, java.util.function.BiFunction<? super V, ? super V, ? extends V> function) { return invoke("merge", () -> region.merge(key, value, function)); }
	@Override public void forEach(java.util.function.BiConsumer<? super K, ? super V> action) { invoke("forEach", () -> { region.forEach(action); return null; }); }
	@Override public void replaceAll(java.util.function.BiFunction<? super K, ? super V, ? extends V> function) { invoke("replaceAll", () -> { region.replaceAll(function); return null; }); }
	@Override public void putAll(java.util.Map<? extends K, ? extends V> values) { invoke("putAll", () -> { region.putAll(values); return null; }); }
	@Override public void putAll(java.util.Map<? extends K, ? extends V> values, Object argument) { invoke("putAll", () -> { region.putAll(values, argument); return null; }); }
	@Override public java.util.Map<K, V> getAll(java.util.Collection<? extends K> keys) { return invoke("getAll", () -> region.getAll(keys)); }
	@Override public <T extends K> java.util.Map<T, V> getAll(java.util.Collection<T> keys, Object argument) { return invoke("getAll", () -> region.getAll(keys, argument)); }
	@Override public void removeAll(java.util.Collection<? extends K> keys) { invoke("removeAll", () -> { region.removeAll(keys); return null; }); }
	@Override public void removeAll(java.util.Collection<? extends K> keys, Object argument) { invoke("removeAll", () -> { region.removeAll(keys, argument); return null; }); }
	@Override public void localClear() { invoke("localClear", () -> { region.localClear(); return null; }); }
	@Override public void localDestroy(K key) { invoke("localDestroy", () -> { region.localDestroy(key); return null; }); }
	@Override public void localDestroy(K key, Object argument) { invoke("localDestroy", () -> { region.localDestroy(key, argument); return null; }); }
	@Override public void localInvalidate(K key) { invoke("localInvalidate", () -> { region.localInvalidate(key); return null; }); }
	@Override public void localInvalidate(K key, Object argument) { invoke("localInvalidate", () -> { region.localInvalidate(key, argument); return null; }); }
	@Override public void localDestroyRegion() { invoke("localDestroyRegion", () -> { region.localDestroyRegion(); return null; }); }
	@Override public void localDestroyRegion(Object argument) { invoke("localDestroyRegion", () -> { region.localDestroyRegion(argument); return null; }); }
	@Override public void localInvalidateRegion() { invoke("localInvalidateRegion", () -> { region.localInvalidateRegion(); return null; }); }
	@Override public void localInvalidateRegion(Object argument) { invoke("localInvalidateRegion", () -> { region.localInvalidateRegion(argument); return null; }); }
	@Override public String getFullPath() { return invoke("getFullPath", () -> region.getFullPath()); }
	@Override public DriverRegion<?, ?> getParentRegion() { return invoke("getParentRegion", () -> wrap(region.getParentRegion())); }
	@Override public DriverRegion<?, ?> getSubregion(String path) { return invoke("getSubregion", () -> wrap(region.getSubregion(path))); }
	@Override public java.util.Set<DriverRegion<?, ?>> subregions(boolean recursive) { return invoke("subregions", () -> new GemFireRegionViews().regions(region.subregions(recursive))); }
	@Override public DriverRegionEntry<K, V> getEntry(K key) { return invoke("getEntry", () -> entry(region.getEntry(key))); }
	@Override public java.util.Set<K> keySet() { return invoke("keySet", () -> new GemFireRegionViews().set(region.keySet(), java.util.function.Function.identity())); }
	@Override public java.util.Collection<V> values() { return invoke("values", () -> new GemFireRegionViews().collection(region.values(), java.util.function.Function.identity())); }
	@Override public java.util.Set<java.util.Map.Entry<K, V>> entrySet() { return invoke("entrySet", () -> new GemFireRegionViews().mappedSet(region.entrySet(), e -> new GemFireMapEntry<>(e))); }
	@Override public java.util.Set<DriverRegionEntry<?, ?>> entrySet(boolean recursive) { return invoke("entrySet", () -> new GemFireRegionViews().mappedSet(region.entrySet(recursive), e -> new GemFireRegionEntry<>(e))); }
	@Override public DriverRegionService getRegionService() { return invoke("getRegionService", () -> new GemFireRegionService(region.getRegionService())); }
	@Override public DriverConnection getCache() { return invoke("getCache", () -> new GemFireConnection(region.getCache())); }
	@Override public Object getUserAttribute() { return invoke("getUserAttribute", () -> region.getUserAttribute()); }
	@Override public void setUserAttribute(Object value) { invoke("setUserAttribute", () -> { region.setUserAttribute(value); return null; }); }
	@Override public DriverCacheStatistics getStatistics() { return invoke("getStatistics", () -> new GemFireCacheStatistics(region.getStatistics())); }
	@Override public RegionMetadata metadata() { return invoke("metadata", () -> new RegionMetadata(com.vmware.gemfire.driver.spi.config.DataPolicy.valueOf(region.getAttributes().getDataPolicy().toString()), region.getAttributes().getPoolName())); }
	@Override public boolean containsKeyOnServer(K key) { return invoke("containsKeyOnServer", () -> region.containsKeyOnServer(key)); }
	@Override public java.util.Set<K> keySetOnServer() { return invoke("keySetOnServer", () -> region.keySetOnServer()); }
	@Override public int sizeOnServer() { return invoke("sizeOnServer", () -> region.sizeOnServer()); }
	@Override public long sizeLongOnServer() { return invoke("sizeLongOnServer", () -> region.sizeLongOnServer()); }
	@Override public boolean isEmptyOnServer() { return invoke("isEmptyOnServer", () -> region.isEmptyOnServer()); }

	private DriverRegion<?, ?> wrap(Region<?, ?> value) { return value == null ? null : new GemFireRegion<>(value); }
	private DriverRegionEntry<K, V> entry(Region.Entry<K, V> value) { return value == null ? null : new GemFireRegionEntry<>(value); }
	@Override public boolean equals(Object other) { return other instanceof GemFireRegion<?, ?> handle && region.equals(handle.region); }
	@Override public int hashCode() { return region.hashCode(); }
}
