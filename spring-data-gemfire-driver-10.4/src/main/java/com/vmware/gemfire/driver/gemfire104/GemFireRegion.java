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

	private final Region<K, V> region;

	GemFireRegion(Region<K, V> region) {
		this.region = region;
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
				Object application = new GemFireCacheListenerFactory().applicationAdapter(listener);
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
}
