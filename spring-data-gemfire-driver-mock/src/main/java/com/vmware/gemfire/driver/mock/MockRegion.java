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
	private final MockRegion<?, ?> parent;
	private final MockRegionAttributes<K, V> attributes;
	private final Map<K, V> entries = new HashMap<>();
	private DriverCacheLoader<K, V> loader;
	private DriverCacheWriter<K, V> writer;
	private final List<DriverCacheListener<K, V>> listeners = new ArrayList<>();
	private final List<DriverRegionLifecycleListener> lifecycleListeners = new ArrayList<>();
	private boolean destroyed;
	private Object userAttribute;
	private final Map<K, Object> entryAttributes = new HashMap<>();
	private final Map<K, Object> entryIdentities = new HashMap<>();

	MockRegion(String name, MockConnection connection, MockRegion<?, ?> parent, RegionConfiguration<K, V> config, boolean client) {
		this.name = name; this.connection = connection; this.parent = parent;
		this.attributes = new MockRegionAttributes<>(this, config, client);
	}
	@Override public DriverRegionAttributes<K, V> getAttributes() { return attributes; }
	@Override public DriverRegionAttributesMutator<K, V> getAttributesMutator() { checkOpen(); return new MockRegionAttributesMutator<>(this); }
	@Override public <SK, SV> DriverRegion<SK, SV> createSubregion(String name,
		com.vmware.gemfire.driver.spi.config.RegionShortcut shortcut, RegionConfiguration<SK, SV> config) {
		checkOpen(); return connection.createConfigured(name, shortcut, config, this);
	}
	@Override public <SK, SV> DriverRegion<SK, SV> createClientSubregion(String name,
		com.vmware.gemfire.driver.spi.config.ClientRegionShortcut shortcut, RegionConfiguration<SK, SV> config) {
		checkOpen(); return connection.createConfiguredClient(name, shortcut, config, this);
	}

	void created(RegionCallbacks<K, V> callbacks) {
		// Validate before installation, so unsupported registration cannot partially mutate a region.
		callbacks.listeners().forEach(this::checkListener);
		listeners.addAll(callbacks.listeners());
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
		checkKeyConstraint(key);
		if (value != null) checkValue(value);
		V old = entries.get(key);
		var event = new EntryEvent(operation, argument, key, old, value);
		before(w -> { if (operation.isCreate()) w.beforeCreate(event); else w.beforeUpdate(event); });
		entries.put(key, value);
		entryIdentities.computeIfAbsent(key, k -> new Object());
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
		if (!operation.isLocal()) before(w -> w.beforeDestroy(event));
		entries.remove(key); entryAttributes.remove(key); entryIdentities.remove(key);
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
		entries.clear(); entryAttributes.clear(); entryIdentities.clear();
		notifyListeners(l -> l.afterRegionClear(event));
	}
	@Override public synchronized void invalidateRegion(Object argument) {
		checkOpen();
		entries.replaceAll((k, v) -> null);
		for (var child : connection.subregions(this, false)) child.invalidateRegion(argument);
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
		if (!destroyed) releaseTree(DriverOperation.CACHE_CLOSE, null);
	}
	private void release(RegionEvent event) {
		GemFireDriverException failure = null;
		for (var child : connection.subregions(this, false)) {
			try { ((MockRegion<?, ?>) child).releaseTree(event.getOperation(), event.getCallbackArgument()); }
			catch (GemFireDriverException ex) {
				if (failure == null) failure = ex; else if (failure != ex) failure.addSuppressed(ex);
			}
		}
		destroyed = true; entries.clear(); entryAttributes.clear(); entryIdentities.clear();
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
		try { closeCallback(oldLoader, DriverFailureKind.LOADER_FAILURE); }
		catch (GemFireDriverException ex) { if (failure == null) failure = ex; else if (failure != ex) failure.addSuppressed(ex); }
		try { closeCallback(oldWriter, DriverFailureKind.WRITER_VETO); }
		catch (GemFireDriverException ex) {
			if (failure == null) failure = ex;
			else if (failure != ex) failure.addSuppressed(ex);
		}
		if (failure != null) throw failure;
	}
	private void releaseTree(DriverOperation operation, Object argument) {
		connection.removeRegion(name, this);
		release(new RegionEvent(operation, argument));
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
	void checkOpen() {
		connection.checkOpen();
		if (destroyed) throw new DriverRegionUnavailableException(getFullPath(), null);
	}
	private void checkKey(K key) {
		checkOpen();
		if (key == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Region key must not be null", null);
	}
	private void checkValue(V value) {
		if (value == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Region value must not be null", null);
		if (attributes.getValueConstraint() != null && !attributes.getValueConstraint().isInstance(value)) {
			throw failure(DriverFailureKind.UNKNOWN, "Value constraint violated", new ClassCastException());
		}
	}
	private void checkKeyConstraint(K key) {
		if (attributes.getKeyConstraint() != null && !attributes.getKeyConstraint().isInstance(key)) {
			throw failure(DriverFailureKind.UNKNOWN, "Key constraint violated", new ClassCastException());
		}
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

	@Override public void create(K key, V value) { create(key, value, null); }
	@Override public synchronized void create(K key, V value, Object argument) {
		checkKey(key);
		if (entries.containsKey(key)) throw new DriverEntryExistsException(entries.get(key), null);
		putValue(key, value, argument, DriverOperation.CREATE);
	}
	@Override public V destroy(K key) { return destroy(key, null); }
	@Override public void invalidate(K key) { invalidate(key, null); }
	@Override public void destroyRegion() { destroyRegion(null); }
	@Override public void invalidateRegion() { invalidateRegion(null); }
	@Override public synchronized void closeRegion() { endRegion(DriverOperation.REGION_CLOSE, null); }
	@Override public synchronized boolean containsValue(Object value) {
		checkOpen(); if (value == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Null value", null);
		return entries.containsValue(value);
	}
	@Override public synchronized boolean containsValueForKey(K key) { checkKey(key); return entries.get(key) != null; }
	@Override public synchronized boolean isEmpty() { return size() == 0; }
	@Override public long sizeLong() { return size(); }
	@Override public synchronized V replace(K key, V value) {
		checkKey(key); checkValue(value);
		return entries.containsKey(key) ? putValue(key, value, null, DriverOperation.REPLACE) : null;
	}
	@Override public synchronized boolean replace(K key, V oldValue, V newValue) {
		checkKey(key); checkValue(newValue);
		if (!entries.containsKey(key) || !java.util.Objects.equals(entries.get(key), oldValue)) return false;
		putValue(key, newValue, null, DriverOperation.REPLACE); return true;
	}
	@Override public synchronized boolean remove(K key, Object value) {
		checkKey(key);
		if (!entries.containsKey(key) || !java.util.Objects.equals(entries.get(key), value)) return false;
		destroyEntry(key, null, DriverOperation.REMOVE); return true;
	}
	@Override public V getOrDefault(K key, V defaultValue) { V value = get(key); return value == null ? defaultValue : value; }
	// Model ConcurrentMap default algorithms using the region operations (including their callbacks).
	// These are local mock semantics, not a new atomicity guarantee for provider implementations.
	@Override public V computeIfAbsent(K key, java.util.function.Function<? super K, ? extends V> function) {
		requireFunction(function); V old = get(key);
		if (old != null) return old;
		V value = evaluate(() -> function.apply(key)); if (value == null) return null;
		V previous = putIfAbsent(key, value); return previous == null ? value : previous;
	}
	@Override public V computeIfPresent(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> function) {
		requireFunction(function);
		for (;;) {
			V old = get(key); if (old == null) return null;
			V value = evaluate(() -> function.apply(key, old));
			if (value == null ? remove(key, old) : replace(key, old, value)) return value;
		}
	}
	@Override public V compute(K key, java.util.function.BiFunction<? super K, ? super V, ? extends V> function) {
		requireFunction(function);
		for (;;) {
			V old = get(key); V value = evaluate(() -> function.apply(key, old));
			if (value == null) { if (old == null || remove(key, old)) return null; }
			else if (old == null) { V raced = putIfAbsent(key, value); if (raced == null) return value; }
			else if (replace(key, old, value)) return value;
		}
	}
	@Override public V merge(K key, V value, java.util.function.BiFunction<? super V, ? super V, ? extends V> function) {
		requireFunction(function); checkValue(value);
		return compute(key, (k, old) -> old == null ? value : function.apply(old, value));
	}
	@Override public void forEach(java.util.function.BiConsumer<? super K, ? super V> action) {
		requireFunction(action); for (var entry : entrySet()) evaluate(() -> { action.accept(entry.getKey(), entry.getValue()); return null; });
	}
	@Override public void replaceAll(java.util.function.BiFunction<? super K, ? super V, ? extends V> function) {
		requireFunction(function);
		forEach((key, old) -> { V value = old; for (;;) { V previous = value; V next = evaluate(() -> function.apply(key, previous)); if (replace(key, value, next)) break; value = get(key); if (value == null) break; } });
	}
	@Override public void putAll(Map<? extends K, ? extends V> values) { putAll(values, null); }
	@Override public synchronized void putAll(Map<? extends K, ? extends V> values, Object argument) {
		checkOpen(); if (values == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Null map", null);
		values.forEach((k, v) -> { checkKey(k); checkKeyConstraint(k); checkValue(v); });
		values.forEach((k, v) -> putValue(k, v, argument, entries.containsKey(k) ? DriverOperation.PUTALL_UPDATE : DriverOperation.PUTALL_CREATE));
	}
	@Override public Map<K, V> getAll(java.util.Collection<? extends K> keys) { return new HashMap<>(getAll(keys, null)); }
	@Override public synchronized <T extends K> Map<T, V> getAll(java.util.Collection<T> keys, Object argument) {
		checkOpen(); if (keys == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Null keys", null);
		keys.forEach(this::checkKey); Map<T, V> result = new HashMap<>();
		keys.forEach(key -> result.put(key, get(key, argument))); return result;
	}
	@Override public void removeAll(java.util.Collection<? extends K> keys) { removeAll(keys, null); }
	@Override public synchronized void removeAll(java.util.Collection<? extends K> keys, Object argument) {
		checkOpen(); if (keys == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Null keys", null);
		keys.forEach(this::checkKey);
		for (K key : keys) if (entries.containsKey(key)) destroyEntry(key, argument, DriverOperation.REMOVEALL_DESTROY);
	}
	@Override public synchronized void localClear() {
		checkOpen(); entries.clear(); entryAttributes.clear(); entryIdentities.clear();
		var event = new RegionEvent(DriverOperation.REGION_LOCAL_CLEAR, null);
		notifyListeners(l -> l.afterRegionClear(event));
	}
	@Override public void localDestroy(K key) { localDestroy(key, null); }
	@Override public synchronized void localDestroy(K key, Object argument) { checkExisting(key); destroyEntry(key, argument, DriverOperation.LOCAL_DESTROY); }
	@Override public void localInvalidate(K key) { localInvalidate(key, null); }
	@Override public synchronized void localInvalidate(K key, Object argument) {
		checkExisting(key); V old = entries.put(key, null);
		var event = new EntryEvent(DriverOperation.LOCAL_INVALIDATE, argument, key, old, null);
		notifyListeners(l -> l.afterInvalidate(event));
	}
	@Override public void localDestroyRegion() { localDestroyRegion(null); }
	@Override public synchronized void localDestroyRegion(Object argument) { endRegion(DriverOperation.REGION_LOCAL_DESTROY, argument); }
	private void endRegion(DriverOperation operation, Object argument) {
		checkOpen(); connection.removeRegion(name, this); release(new RegionEvent(operation, argument));
	}
	@Override public void localInvalidateRegion() { localInvalidateRegion(null); }
	@Override public synchronized void localInvalidateRegion(Object argument) {
		checkOpen(); entries.replaceAll((k, v) -> null);
		for (var child : connection.subregions(this, false)) child.localInvalidateRegion(argument);
		var event = new RegionEvent(DriverOperation.REGION_LOCAL_INVALIDATE, argument);
		notifyListeners(l -> l.afterRegionInvalidate(event));
	}
	@Override public String getFullPath() { return parent == null ? "/" + name : parent.getFullPath() + "/" + name; }
	@Override public DriverRegion<?, ?> getParentRegion() { return parent; }
	@Override public DriverRegion<?, ?> getSubregion(String path) {
		checkOpen(); if (path == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Null path", null);
		if (path.isEmpty()) return this;
		return connection.getRegion(path.startsWith("/") ? path : getFullPath() + "/" + path);
	}
	@Override public java.util.Set<DriverRegion<?, ?>> subregions(boolean recursive) { checkOpen(); return connection.subregions(this, recursive); }
	@Override public synchronized DriverRegionEntry<K, V> getEntry(K key) {
		checkKey(key); return entries.containsKey(key) ? new Entry(key) : null;
	}
	@Override public java.util.Set<K> keySet() { checkOpen(); return new SetView<>(0); }
	@Override public java.util.Collection<V> values() { checkOpen(); return new View<>(1); }
	@Override public java.util.Set<Map.Entry<K, V>> entrySet() { checkOpen(); return new SetView<>(2); }
	@Override public java.util.Set<DriverRegionEntry<?, ?>> entrySet(boolean recursive) {
		checkOpen();
		if (!recursive) return new SetView<>(2);
		return new java.util.AbstractSet<>() {
			private java.util.List<DriverRegionEntry<?, ?>> current() {
				var result = new ArrayList<DriverRegionEntry<?, ?>>(MockRegion.this.entrySet(false));
				for (var child : subregions(true)) result.addAll(child.entrySet(false));
				return result;
			}
			@Override public int size() { return isDestroyed() ? 0 : current().size(); }
			@Override public java.util.Iterator<DriverRegionEntry<?, ?>> iterator() {
				checkOpen(); var values = current().iterator();
				return new java.util.Iterator<>() {
					@Override public boolean hasNext() { checkOpen(); return values.hasNext(); }
					@Override public DriverRegionEntry<?, ?> next() { checkOpen(); return values.next(); }
					@Override public void remove() { throw failure(DriverFailureKind.UNKNOWN, "Region view mutation is unsupported", new UnsupportedOperationException()); }
				};
			}
		};
	}
	@Override public DriverRegionService getRegionService() { return connection; }
	@Override public DriverConnection getCache() { return connection; }
	@Override public Object getUserAttribute() { checkOpen(); return userAttribute; }
	@Override public void setUserAttribute(Object value) { checkOpen(); userAttribute = value; }
	@Override public DriverCacheStatistics getStatistics() {
		checkOpen(); throw failure(DriverFailureKind.SYSTEM_FAILURE, "Statistics are disabled", null);
	}
	@Override public RegionMetadata metadata() { return new RegionMetadata(attributes.getDataPolicy(), attributes.getPoolName()); }
	@Override public boolean containsKeyOnServer(K key) { throw UnsupportedFeatureException.notIntroduced("Region.containsKeyOnServer"); }
	@Override public java.util.Set<K> keySetOnServer() { throw UnsupportedFeatureException.notIntroduced("Region.keySetOnServer"); }
	@Override public int sizeOnServer() { throw UnsupportedFeatureException.notIntroduced("Region.sizeOnServer"); }
	@Override public long sizeLongOnServer() { throw UnsupportedFeatureException.notIntroduced("Region.sizeLongOnServer"); }
	@Override public boolean isEmptyOnServer() { throw UnsupportedFeatureException.notIntroduced("Region.isEmptyOnServer"); }

	private void requireFunction(Object function) {
		if (function == null) throw failure(DriverFailureKind.INVALID_ARGUMENT, "Null map function", null);
	}
	private <T> T evaluate(java.util.function.Supplier<T> action) {
		try { return action.get(); }
		catch (GemFireDriverException ex) { throw ex; }
		catch (RuntimeException ex) {
			DriverFailureKind kind = ex instanceof IllegalArgumentException || ex instanceof NullPointerException
				? DriverFailureKind.INVALID_ARGUMENT : ex instanceof IllegalStateException ? DriverFailureKind.INVALID_STATE : DriverFailureKind.UNKNOWN;
			throw failure(kind, "Map function failed", ex);
		}
	}

	private final class Entry implements DriverRegionEntry<K, V> {
		private final K key;
		private boolean observedDestroyed;
		Entry(K key) { this.key = key; }
		private void checkEntry() {
			checkOpen(); if (!entries.containsKey(key)) throw failure(DriverFailureKind.ENTRY_DESTROYED, "Entry is destroyed", null);
		}
		@Override public K getKey() { checkEntry(); return key; }
		@Override public V getValue() { checkEntry(); return entries.get(key); }
		@Override public V setValue(V value) { checkEntry(); return put(key, value); }
		@Override public DriverRegion<K, V> getRegion() { checkEntry(); return MockRegion.this; }
		@Override public boolean isLocal() { return true; }
		@Override public boolean isDestroyed() { if (MockRegion.this.isDestroyed() || !entries.containsKey(key)) observedDestroyed = true; return observedDestroyed; }
		@Override public DriverCacheStatistics getStatistics() { checkEntry(); return MockRegion.this.getStatistics(); }
		@Override public Object getUserAttribute() { checkEntry(); return entryAttributes.get(key); }
		@Override public Object setUserAttribute(Object value) { checkEntry(); return entryAttributes.put(key, value); }
		@Override public boolean equals(Object other) { checkEntry(); return other instanceof MockRegion<?, ?>.Entry e && getRegion() == e.getRegion() && java.util.Objects.equals(key, e.getKey()); }
		@Override public int hashCode() { checkEntry(); return System.identityHashCode(entryIdentities.get(key)) ^ MockRegion.this.hashCode(); }
	}
	private final class SetView<T> extends java.util.AbstractSet<T> {
		private final View<T> view;
		SetView(int kind) { view = new View<>(kind); }
		@Override public int size() { return view.size(); }
		@Override public java.util.Iterator<T> iterator() { return view.iterator(); }
		@Override public boolean add(T value) { return view.add(value); }
	}
	private class View<T> extends java.util.AbstractCollection<T> {
		private final int kind;
		View(int kind) { this.kind = kind; }
		@Override public int size() { return kind == 1 ? (int) entries.values().stream().filter(java.util.Objects::nonNull).count() : entries.size(); }
		@Override public boolean add(T value) { throw failure(DriverFailureKind.UNKNOWN, "Region view mutation is unsupported", new UnsupportedOperationException()); }
		@Override public java.util.Iterator<T> iterator() {
			checkOpen(); var keys = new ArrayList<>(entries.keySet());
			if (kind == 1) keys.removeIf(key -> entries.get(key) == null);
			var iterator = keys.iterator();
			return new java.util.Iterator<>() {
				@Override public boolean hasNext() { checkOpen(); return iterator.hasNext(); }
				@Override public void remove() { throw failure(DriverFailureKind.UNKNOWN, "Region view mutation is unsupported", new UnsupportedOperationException()); }
				@Override @SuppressWarnings("unchecked") public T next() {
					checkOpen(); K key = iterator.next();
					return (T) (kind == 0 ? key : kind == 1 ? entries.get(key) : new Entry(key));
				}
			};
		}
	}
}
