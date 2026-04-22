/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory ConcurrentHashMap-backed GudRegion for unit tests
 */

package org.springframework.data.gemfire.gud.driver.mock;

import static org.mockito.Mockito.mock;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.data.gemfire.gud.api.GudAttributesMutator;
import org.springframework.data.gemfire.gud.api.GudCacheStatistics;
import org.springframework.data.gemfire.gud.api.GudInterestResultPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionEntry;
import org.springframework.data.gemfire.gud.api.GudRegionService;
import org.springframework.data.gemfire.gud.api.GudRegionSnapshotService;
import org.springframework.data.gemfire.gud.api.GudSelectResults;

/**
 * In-memory mock {@link GudRegion} backed by a {@link ConcurrentHashMap}.
 * <p>
 * Supports the core {@link java.util.concurrent.ConcurrentMap} operations and tracks
 * destroyed/closed state.  Non-core methods (interest registration, snapshot, distributed
 * locking) have no-op or mock-backed implementations.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class MockGudRegion<K, V> implements GudRegion<K, V> {

    private final String name;
    private final String fullPath;
    private final ConcurrentHashMap<K, V> data = new ConcurrentHashMap<>();
    private final Map<String, MockGudRegion<?, ?>> subregions = new ConcurrentHashMap<>();
    private final GudRegion<?, ?> parent;
    private final GudRegionService regionService;
    private final List<K> interestList = Collections.synchronizedList(new ArrayList<>());
    private final List<String> interestRegexList = Collections.synchronizedList(new ArrayList<>());
    private final Lock regionLock = new ReentrantLock();
    private volatile boolean destroyed;
    private volatile Object userAttribute;

    public MockGudRegion(String name, GudRegionService regionService) {
        this(name, SEPARATOR + name, null, regionService);
    }

    public MockGudRegion(String name, String fullPath, GudRegion<?, ?> parent, GudRegionService regionService) {
        this.name = name;
        this.fullPath = fullPath;
        this.parent = parent;
        this.regionService = regionService;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFullPath() {
        return fullPath;
    }

    @Override
    public GudRegion<?, ?> getParentRegion() {
        return parent;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SK, SV> GudRegion<SK, SV> getSubregion(String path) {
        return (GudRegion<SK, SV>) subregions.get(path);
    }

    @Override
    public Set<GudRegion<?, ?>> subregions(boolean recursive) {
        Set<GudRegion<?, ?>> result = new HashSet<>(subregions.values());
        if (recursive) {
            for (MockGudRegion<?, ?> sub : subregions.values()) {
                result.addAll(sub.subregions(true));
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    void addSubregion(MockGudRegion<?, ?> subregion) {
        subregions.put(subregion.getName(), subregion);
    }

    @Override
    public GudRegionAttributes<K, V> getAttributes() {
        return mock(GudRegionAttributes.class);
    }

    @Override
    public GudAttributesMutator<K, V> getAttributesMutator() {
        return mock(GudAttributesMutator.class);
    }

    @Override
    public GudRegionService getRegionService() {
        return regionService;
    }

    @Override
    public GudCacheStatistics getStatistics() {
        return mock(GudCacheStatistics.class);
    }

    // ConcurrentMap / Map methods delegate to the backing ConcurrentHashMap

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return data.get(key);
    }

    @Override
    public V get(Object key, Object aCallbackArgument) {
        return data.get(key);
    }

    @Override
    public V put(K key, V value) {
        return data.put(key, value);
    }

    @Override
    public V put(K key, V value, Object aCallbackArgument) {
        return data.put(key, value);
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return data.putIfAbsent(key, value);
    }

    @Override
    public V remove(Object key) {
        return data.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return data.remove(key, value);
    }

    @Override
    public V replace(K key, V value) {
        return data.replace(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return data.replace(key, oldValue, newValue);
    }

    @Override
    public void create(K key, V value) {
        if (data.putIfAbsent(key, value) != null) {
            throw new IllegalStateException("Entry already exists: " + key);
        }
    }

    @Override
    public void create(K key, V value, Object aCallbackArgument) {
        create(key, value);
    }

    @Override
    public V destroy(Object key) {
        return data.remove(key);
    }

    @Override
    public V destroy(Object key, Object aCallbackArgument) {
        return data.remove(key);
    }

    @Override
    public void invalidate(Object key) {
        // Invalidate without destroy: set value to null (not supported by ConcurrentHashMap
        // which disallows null values); we simulate by removing the entry.
        data.remove(key);
    }

    @Override
    public void invalidate(Object key, Object aCallbackArgument) {
        invalidate(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        data.putAll(map);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map, Object aCallbackArgument) {
        data.putAll(map);
    }

    @Override
    public Map<K, V> getAll(Collection<?> keys) {
        Map<K, V> result = new HashMap<>();
        for (Object key : keys) {
            @SuppressWarnings("unchecked")
            K typedKey = (K) key;
            V value = data.get(typedKey);
            if (value != null) {
                result.put(typedKey, value);
            }
        }
        return result;
    }

    @Override
    public Map<K, V> getAll(Collection<?> keys, Object aCallbackArgument) {
        return getAll(keys);
    }

    @Override
    public void removeAll(Collection<?> keys) {
        for (Object key : keys) {
            data.remove(key);
        }
    }

    @Override
    public void removeAll(Collection<?> keys, Object aCallbackArgument) {
        removeAll(keys);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public void localDestroy(Object key) {
        data.remove(key);
    }

    @Override
    public void localDestroy(Object key, Object aCallbackArgument) {
        data.remove(key);
    }

    @Override
    public void localInvalidate(Object key) {
        data.remove(key);
    }

    @Override
    public void localInvalidate(Object key, Object aCallbackArgument) {
        data.remove(key);
    }

    @Override
    public void localClear() {
        data.clear();
    }

    @Override
    public void destroyRegion() {
        destroyed = true;
        data.clear();
    }

    @Override
    public void destroyRegion(Object aCallbackArgument) {
        destroyRegion();
    }

    @Override
    public void localDestroyRegion() {
        destroyRegion();
    }

    @Override
    public void localDestroyRegion(Object aCallbackArgument) {
        destroyRegion();
    }

    @Override
    public void invalidateRegion() {
        data.clear();
    }

    @Override
    public void invalidateRegion(Object aCallbackArgument) {
        data.clear();
    }

    @Override
    public void localInvalidateRegion() {
        data.clear();
    }

    @Override
    public void localInvalidateRegion(Object aCallbackArgument) {
        data.clear();
    }

    @Override
    public void close() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public boolean isLocalRegion() {
        return false;
    }

    @Override
    public GudRegionEntry<K, V> getEntry(Object key) {
        V value = data.get(key);
        if (value == null) {
            return null;
        }
        @SuppressWarnings("unchecked")
        K typedKey = (K) key;
        return new MockRegionEntry<>(typedKey, value, this);
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return data.entrySet();
    }

    @Override
    public Set<GudRegionEntry<K, V>> entrySet(boolean recursive) {
        Set<GudRegionEntry<K, V>> result = new HashSet<>();
        for (Map.Entry<K, V> e : data.entrySet()) {
            result.add(new MockRegionEntry<>(e.getKey(), e.getValue(), this));
        }
        return result;
    }

    @Override
    public Set<K> keySet() {
        return data.keySet();
    }

    @Override
    public Collection<V> values() {
        return data.values();
    }

    @Override
    public boolean containsKeyOnServer(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValueForKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public Set<K> keySetOnServer() {
        return data.keySet();
    }

    @Override
    public boolean isEmptyOnServer() {
        return data.isEmpty();
    }

    @Override
    public int sizeOnServer() {
        return data.size();
    }

    // Interest registration - track the requests; no-op since there's no real server

    @Override
    public void registerInterest(K key) {
        interestList.add(key);
    }

    @Override
    public void registerInterest(K key, boolean isDurable) {
        interestList.add(key);
    }

    @Override
    public void registerInterest(K key, boolean isDurable, boolean receiveValues) {
        interestList.add(key);
    }

    @Override
    public void registerInterest(K key, GudInterestResultPolicy policy) {
        interestList.add(key);
    }

    @Override
    public void registerInterest(K key, GudInterestResultPolicy policy, boolean isDurable) {
        interestList.add(key);
    }

    @Override
    public void registerInterest(K key, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        interestList.add(key);
    }

    @Override
    public void registerInterestForAllKeys() {
    }

    @Override
    public void registerInterestForAllKeys(GudInterestResultPolicy policy) {
    }

    @Override
    public void registerInterestForAllKeys(GudInterestResultPolicy policy, boolean isDurable) {
    }

    @Override
    public void registerInterestForAllKeys(GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys) {
        for (K k : keys) {
            interestList.add(k);
        }
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy) {
        registerInterestForKeys(keys);
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy, boolean isDurable) {
        registerInterestForKeys(keys);
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        registerInterestForKeys(keys);
    }

    @Override
    public void registerInterestRegex(String regex) {
        interestRegexList.add(regex);
    }

    @Override
    public void registerInterestRegex(String regex, boolean isDurable) {
        interestRegexList.add(regex);
    }

    @Override
    public void registerInterestRegex(String regex, boolean isDurable, boolean receiveValues) {
        interestRegexList.add(regex);
    }

    @Override
    public void registerInterestRegex(String regex, GudInterestResultPolicy policy) {
        interestRegexList.add(regex);
    }

    @Override
    public void registerInterestRegex(String regex, GudInterestResultPolicy policy, boolean isDurable) {
        interestRegexList.add(regex);
    }

    @Override
    public void registerInterestRegex(String regex, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        interestRegexList.add(regex);
    }

    @Override
    public void unregisterInterest(K key) {
        interestList.remove(key);
    }

    @Override
    public void unregisterInterestRegex(String regex) {
        interestRegexList.remove(regex);
    }

    @Override
    public List<K> getInterestList() {
        return new ArrayList<>(interestList);
    }

    @Override
    public List<String> getInterestListRegex() {
        return new ArrayList<>(interestRegexList);
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudSelectResults<V> query(String queryPredicate) {
        return mock(GudSelectResults.class);
    }

    @Override
    public Object selectValue(String queryPredicate) {
        return null;
    }

    @Override
    public boolean existsValue(String queryPredicate) {
        return false;
    }

    @Override
    public Lock getDistributedLock(Object key) {
        return regionLock;
    }

    @Override
    public Lock getRegionDistributedLock() {
        return regionLock;
    }

    @Override
    public void becomeLockGrantor() {
    }

    @Override
    public Object getUserAttribute() {
        return userAttribute;
    }

    @Override
    public void setUserAttribute(Object value) {
        this.userAttribute = value;
    }

    @Override
    public void saveSnapshot(OutputStream outputStream) {
    }

    @Override
    public void loadSnapshot(InputStream inputStream) {
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionSnapshotService<K, V> getSnapshotService() {
        return mock(GudRegionSnapshotService.class);
    }

    @Override
    public void writeToDisk() {
    }

    @Override
    public void forceRolling() {
    }

    /**
     * Lightweight {@link GudRegionEntry} impl for {@link #getEntry(Object)} /
     * {@link #entrySet(boolean)} results.
     */
    private static final class MockRegionEntry<K, V> implements GudRegionEntry<K, V> {

        private final K key;
        private final V value;
        private final GudRegion<K, V> region;

        MockRegionEntry(K key, V value, GudRegion<K, V> region) {
            this.key = key;
            this.value = value;
            this.region = region;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public GudRegion<K, V> getRegion() {
            return region;
        }

        @Override
        public boolean isLocal() {
            return true;
        }

        @Override
        public GudCacheStatistics getStatistics() {
            return mock(GudCacheStatistics.class);
        }

        @Override
        public Object getUserAttribute() {
            return null;
        }

        @Override
        public Object setUserAttribute(Object userAttribute) {
            return null;
        }

        @Override
        public boolean isDestroyed() {
            return false;
        }

        @Override
        public V setValue(V newValue) {
            throw new UnsupportedOperationException("MockRegionEntry is immutable");
        }
    }
}
