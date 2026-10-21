/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import com.vmware.gemfire.driver.spi.*;

/** Local-only event delivery. No remote, transaction or serialized-value events are synthesized. */
final class MockRegion<K, V> implements DriverRegion<K, V> {
	private final System.Logger logger = System.getLogger(getClass().getName());
	private final String name;
	private final MockConnection connection;
	private final Map<K, V> entries = new HashMap<>();
	private DriverCacheLoader<K, V> loader;
	private DriverCacheWriter<K, V> writer;
	private final List<DriverCacheListener<K, V>> listeners = new ArrayList<>();
	private final List<DriverRegionLifecycleListener> lifecycleListeners = new ArrayList<>();
	private boolean destroyed;

	MockRegion(String name, MockConnection connection) { this.name = name; this.connection = connection; }

	void created(RegionCallbacks<K, V> callbacks) {
		// Validate before installation, so unsupported registration cannot partially mutate a region.
		callbacks.listeners().forEach(this::checkListener);
		callbacks.listeners().forEach(this::addCacheListener);
		loader = callbacks.loader(); writer = callbacks.writer();
		var event = new RegionEvent(DriverOperation.REGION_CREATE, null);
		notifyListeners(listener -> listener.afterRegionCreate(event));
	}
	private void checkListener(DriverCacheListener<K, V> listener) {
		if (listener == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Listener must not be null", null);
		if (listener instanceof DriverRegionMembershipListener || listener instanceof DriverRegionRoleListener) {
			throw UnsupportedFeatureException.notIntroduced("Region.distributedListeners");
		}
	}
	@Override public synchronized void addCacheListener(DriverCacheListener<K, V> listener) {
		checkOpen(); checkListener(listener);
		if (listeners.stream().noneMatch(value -> value == listener)) listeners.add(listener);
	}
	@Override public synchronized void removeCacheListener(DriverCacheListener<K, V> listener) {
		checkOpen(); checkListener(listener);
		if (listeners.removeIf(value -> value == listener)) closeListener(listener);
	}
	@Override public synchronized void initCacheListeners(List<DriverCacheListener<K, V>> replacements) {
		checkOpen();
		if (replacements == null) replacements = List.of();
		replacements.forEach(this::checkListener);
		var previous = List.copyOf(listeners);
		listeners.clear();
		listeners.addAll(replacements);
		previous.forEach(this::closeListener);
	}
	@Override public synchronized List<DriverCacheListener<K, V>> getCacheListeners() { checkOpen(); return List.copyOf(listeners); }
	@Override public synchronized DriverCacheWriter<K, V> getCacheWriter() { checkOpen(); return writer; }
	@Override public synchronized DriverCacheLoader<K, V> getCacheLoader() { checkOpen(); return loader; }
	@Override public synchronized void setCacheWriter(DriverCacheWriter<K, V> replacement) {
		checkOpen();
		if (writer != replacement) {
			var previous = writer; writer = replacement;
			closeCallback(previous, DriverFailureKind.WRITER_VETO);
		}
	}
	@Override public synchronized void setCacheLoader(DriverCacheLoader<K, V> replacement) {
		checkOpen();
		if (loader != replacement) {
			var previous = loader; loader = replacement;
			closeCallback(previous, DriverFailureKind.LOADER_FAILURE);
		}
	}
	@Override public synchronized void addLifecycleListener(DriverRegionLifecycleListener listener) {
		checkOpen();
		if (listener == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Region lifecycle listener must not be null", null);
		if (lifecycleListeners.stream().noneMatch(current -> current == listener)) lifecycleListeners.add(listener);
	}
	@Override public synchronized boolean isDestroyed() { return destroyed || connection.isClosed(); }
	@Override public String name() { return name; }
	@Override public V get(K key) { return get(key, null); }
	@Override public synchronized V get(K key, Object argument) {
		checkKey(key);
		V value = entries.get(key);
		if (value != null || loader == null) return value;
		V loaded;
		try {
			loaded = loader.load(new DriverLoaderHelper<>() {
				@Override public K getKey() { return key; }
				@Override public DriverRegion<K, V> getRegion() { return MockRegion.this; }
				@Override public Object getArgument() { return argument; }
				@Override public V netSearch(boolean doNetLoad) { throw UnsupportedFeatureException.notIntroduced("LoaderHelper.netSearch"); }
			});
		} catch (GemFireDriverException failure) { throw failure; }
		catch (RuntimeException failure) { throw failure(DriverFailureKind.LOADER_FAILURE, "Cache loader failed", failure); }
		if (loaded != null) putValue(key, loaded, argument, entries.containsKey(key) ? DriverOperation.LOCAL_LOAD_UPDATE : DriverOperation.LOCAL_LOAD_CREATE);
		return loaded;
	}
	@Override public V put(K key, V value) { return put(key, value, null); }
	@Override public synchronized V put(K key, V value, Object argument) {
		checkKey(key); checkValue(value);
		return putValue(key, value, argument, entries.containsKey(key) ? DriverOperation.UPDATE : DriverOperation.CREATE);
	}
	private V putValue(K key, V value, Object argument, DriverOperation operation) {
		V old = entries.get(key);
		var event = new EntryEvent(operation, argument, key, old, value);
		before(w -> { if (operation.isCreate()) w.beforeCreate(event); else w.beforeUpdate(event); });
		entries.put(key, value);
		notifyListeners(l -> { if (operation.isCreate()) l.afterCreate(event); else l.afterUpdate(event); });
		return old;
	}
	@Override public synchronized V putIfAbsent(K key, V value) {
		checkKey(key); checkValue(value);
		V old = entries.get(key);
		if (old != null) return old;
		return putValue(key, value, null, entries.containsKey(key) ? DriverOperation.UPDATE : DriverOperation.PUT_IF_ABSENT);
	}
	@Override public synchronized V remove(K key) {
		checkKey(key);
		if (!entries.containsKey(key)) return null;
		return destroyEntry(key, null, DriverOperation.DESTROY);
	}
	@Override public synchronized V destroy(K key, Object argument) {
		checkExisting(key); return destroyEntry(key, argument, DriverOperation.DESTROY);
	}
	private V destroyEntry(K key, Object argument, DriverOperation operation) {
		V old = entries.get(key);
		var event = new EntryEvent(operation, argument, key, old, null);
		before(w -> w.beforeDestroy(event));
		entries.remove(key);
		notifyListeners(l -> l.afterDestroy(event));
		return old;
	}
	@Override public synchronized void invalidate(K key, Object argument) {
		checkExisting(key);
		V old = entries.put(key, null);
		var event = new EntryEvent(DriverOperation.INVALIDATE, argument, key, old, null);
		notifyListeners(l -> l.afterInvalidate(event));
	}
	@Override public synchronized void clear() {
		checkOpen();
		var event = new RegionEvent(DriverOperation.REGION_CLEAR, null);
		before(w -> w.beforeRegionClear(event));
		entries.clear();
		notifyListeners(l -> l.afterRegionClear(event));
	}
	@Override public synchronized void invalidateRegion(Object argument) {
		checkOpen();
		entries.replaceAll((k, v) -> null);
		var event = new RegionEvent(DriverOperation.REGION_INVALIDATE, argument);
		notifyListeners(l -> l.afterRegionInvalidate(event));
	}
	@Override public synchronized void destroyRegion(Object argument) {
		checkOpen();
		var event = new RegionEvent(DriverOperation.REGION_DESTROY, argument);
		before(w -> w.beforeRegionDestroy(event));
		connection.removeRegion(name, this);
		release(event);
	}
	@Override public synchronized boolean containsKey(K key) { checkKey(key); return entries.containsKey(key); }
	@Override public synchronized int size() { checkOpen(); return entries.size(); }

	synchronized void releaseEntries() {
		if (!destroyed) release(new RegionEvent(DriverOperation.CACHE_CLOSE, null));
	}
	private void release(RegionEvent event) {
		destroyed = true; entries.clear();
		notifyListeners(l -> l.afterRegionDestroy(event));
		for (var listener : List.copyOf(lifecycleListeners)) {
			suppress(() -> listener.afterRegionDestroy(this));
		}
		var previous = List.copyOf(listeners); listeners.clear();
		previous.forEach(this::closeListener);
		for (var listener : List.copyOf(lifecycleListeners)) suppress(listener::close);
		lifecycleListeners.clear();
		var oldLoader = loader; loader = null;
		var oldWriter = writer; writer = null;
		GemFireDriverException failure = null;
		try { closeCallback(oldLoader, DriverFailureKind.LOADER_FAILURE); }
		catch (GemFireDriverException ex) { failure = ex; }
		try { closeCallback(oldWriter, DriverFailureKind.WRITER_VETO); }
		catch (GemFireDriverException ex) {
			if (failure == null) failure = ex;
			else if (failure != ex) failure.addSuppressed(ex);
		}
		if (failure != null) throw failure;
	}
	private void notifyListeners(Consumer<DriverCacheListener<K, V>> callback) {
		for (var listener : List.copyOf(listeners)) suppress(() -> callback.accept(listener));
	}
	private void closeListener(DriverCacheListener<K, V> listener) { suppress(listener::close); }
	private void suppress(Runnable action) {
		try { action.run(); }
		catch (RuntimeException failure) { logger.log(System.Logger.Level.WARNING, "Region listener failed", failure); }
	}
	private void before(Consumer<DriverCacheWriter<K, V>> callback) {
		if (writer == null) return;
		try { callback.accept(writer); }
		catch (GemFireDriverException failure) { throw failure; }
		catch (RuntimeException failure) { throw failure(DriverFailureKind.WRITER_VETO, "Cache writer failed", failure); }
	}
	private void closeCallback(DriverCacheCallback callback, DriverFailureKind kind) {
		if (callback == null) return;
		try { callback.close(); }
		catch (GemFireDriverException failure) { throw failure; }
		catch (RuntimeException failure) { throw failure(kind, "Cache callback cleanup failed", failure); }
	}
	private void checkOpen() {
		connection.checkOpen();
		if (destroyed) throw failure(DriverFailureKind.REGION_UNAVAILABLE, "Region is destroyed", null);
	}
	private void checkKey(K key) {
		checkOpen();
		if (key == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Region key must not be null", null);
	}
	private void checkValue(V value) {
		if (value == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Region value must not be null", null);
	}
	private void checkExisting(K key) {
		checkKey(key);
		if (!entries.containsKey(key)) throw failure(DriverFailureKind.ENTRY_NOT_FOUND, "Entry not found", null);
	}
	private DriverOperationException failure(DriverFailureKind kind, String message, Throwable cause) {
		return new DriverOperationException(kind, message, cause);
	}
	private class Event implements DriverCacheEvent<K, V> {
		private final DriverOperation operation;
		private final Object argument;
		Event(DriverOperation operation, Object argument) { this.operation = operation; this.argument = argument; }
		@Override public DriverRegion<K, V> getRegion() { return MockRegion.this; }
		@Override public DriverOperation getOperation() { return operation; }
		@Override public Object getCallbackArgument() { return argument; }
		@Override public boolean isCallbackArgumentAvailable() { return true; }
		@Override public boolean isOriginRemote() { return false; }
		@Override public DriverMember getDistributedMember() { return connection.member(); }
	}
	private final class RegionEvent extends Event implements DriverRegionEvent<K, V> {
		RegionEvent(DriverOperation operation, Object argument) { super(operation, argument); }
		@Override public boolean isReinitializing() { return false; }
	}
	private final class EntryEvent extends Event implements DriverEntryEvent<K, V> {
		private final K key;
		private final V oldValue;
		private final V newValue;
		EntryEvent(DriverOperation operation, Object argument, K key, V oldValue, V newValue) {
			super(operation, argument); this.key = key; this.oldValue = oldValue; this.newValue = newValue;
		}
		@Override public K getKey() { return key; }
		@Override public V getOldValue() { return oldValue; }
		@Override public V getNewValue() { return newValue; }
		@Override public boolean isOldValueAvailable() { return true; }
		@Override public boolean hasClientOrigin() { return false; }
		@Override public DriverTransactionId getTransactionId() { return null; }
		@Override public DriverSerializedCacheValue<V> getSerializedOldValue() { return null; }
		@Override public DriverSerializedCacheValue<V> getSerializedNewValue() { return null; }
	}
}
