/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GemFire 10.3 Region adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.3 Region.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFire103Region<K, V> implements GudRegion<K, V>, NativeWrapper<Region<K, V>> {

    private final Region<K, V> nativeRegion;

    public GemFire103Region(Region<K, V> nativeRegion) {
        this.nativeRegion = nativeRegion;
    }

    @Override
    public Region<K, V> getNative() {
        return nativeRegion;
    }

    @Override
    public String getName() {
        return nativeRegion.getName();
    }

    @Override
    public String getFullPath() {
        return nativeRegion.getFullPath();
    }

    @Override
    public GudRegion<?, ?> getParentRegion() {
        Region<?, ?> parent = nativeRegion.getParentRegion();
        return parent != null ? new GemFire103Region<>(parent) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <SK, SV> GudRegion<SK, SV> getSubregion(String path) {
        Region<SK, SV> subregion = nativeRegion.getSubregion(path);
        return subregion != null ? new GemFire103Region<>(subregion) : null;
    }

    @Override
    public Set<GudRegion<?, ?>> subregions(boolean recursive) {
        return nativeRegion.subregions(recursive).stream()
            .map(r -> (GudRegion<?, ?>) new GemFire103Region<>(r))
            .collect(Collectors.toSet());
    }

    @Override
    public GudRegionAttributes<K, V> getAttributes() {
        return new GemFire103RegionAttributes<>(nativeRegion.getAttributes());
    }

    @Override
    public GudAttributesMutator<K, V> getAttributesMutator() {
        return new GemFire103AttributesMutator<>(nativeRegion.getAttributesMutator());
    }

    @Override
    public GudRegionService getRegionService() {
        // TODO: implement proper wrapper
        throw new UnsupportedOperationException("getRegionService not yet implemented");
    }

    @Override
    public GudCacheStatistics getStatistics() {
        // TODO: implement proper wrapper
        throw new UnsupportedOperationException("getStatistics not yet implemented");
    }

    @Override
    public V get(Object key) {
        return nativeRegion.get(key);
    }

    @Override
    public V get(Object key, Object aCallbackArgument) {
        return nativeRegion.get(key, aCallbackArgument);
    }

    @Override
    public V put(K key, V value) {
        return nativeRegion.put(key, value);
    }

    @Override
    public V put(K key, V value, Object aCallbackArgument) {
        return nativeRegion.put(key, value, aCallbackArgument);
    }

    @Override
    public void create(K key, V value) {
        nativeRegion.create(key, value);
    }

    @Override
    public void create(K key, V value, Object aCallbackArgument) {
        nativeRegion.create(key, value, aCallbackArgument);
    }

    @Override
    public V destroy(Object key) {
        return nativeRegion.destroy(key);
    }

    @Override
    public V destroy(Object key, Object aCallbackArgument) {
        return nativeRegion.destroy(key, aCallbackArgument);
    }

    @Override
    public void invalidate(Object key) {
        nativeRegion.invalidate(key);
    }

    @Override
    public void invalidate(Object key, Object aCallbackArgument) {
        nativeRegion.invalidate(key, aCallbackArgument);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, V> getAll(Collection<?> keys) {
        return nativeRegion.getAll((Collection<K>) keys);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<K, V> getAll(Collection<?> keys, Object aCallbackArgument) {
        return nativeRegion.getAll((Collection<K>) keys, aCallbackArgument);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        nativeRegion.putAll(map);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map, Object aCallbackArgument) {
        nativeRegion.putAll(map, aCallbackArgument);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeAll(Collection<?> keys) {
        nativeRegion.removeAll((Collection<? extends K>) keys);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeAll(Collection<?> keys, Object aCallbackArgument) {
        nativeRegion.removeAll((Collection<? extends K>) keys, aCallbackArgument);
    }

    @Override
    public void localDestroy(Object key) {
        nativeRegion.localDestroy(key);
    }

    @Override
    public void localDestroy(Object key, Object aCallbackArgument) {
        nativeRegion.localDestroy(key, aCallbackArgument);
    }

    @Override
    public void localInvalidate(Object key) {
        nativeRegion.localInvalidate(key);
    }

    @Override
    public void localInvalidate(Object key, Object aCallbackArgument) {
        nativeRegion.localInvalidate(key, aCallbackArgument);
    }

    @Override
    public void localClear() {
        nativeRegion.localClear();
    }

    @Override
    public void destroyRegion() {
        nativeRegion.destroyRegion();
    }

    @Override
    public void destroyRegion(Object aCallbackArgument) {
        nativeRegion.destroyRegion(aCallbackArgument);
    }

    @Override
    public void localDestroyRegion() {
        nativeRegion.localDestroyRegion();
    }

    @Override
    public void localDestroyRegion(Object aCallbackArgument) {
        nativeRegion.localDestroyRegion(aCallbackArgument);
    }

    @Override
    public void invalidateRegion() {
        nativeRegion.invalidateRegion();
    }

    @Override
    public void invalidateRegion(Object aCallbackArgument) {
        nativeRegion.invalidateRegion(aCallbackArgument);
    }

    @Override
    public void localInvalidateRegion() {
        nativeRegion.localInvalidateRegion();
    }

    @Override
    public void localInvalidateRegion(Object aCallbackArgument) {
        nativeRegion.localInvalidateRegion(aCallbackArgument);
    }

    @Override
    public void close() {
        nativeRegion.close();
    }

    @Override
    public boolean isDestroyed() {
        return nativeRegion.isDestroyed();
    }

    @Override
    public boolean isLocalRegion() {
        try {
            Class<?> localRegionClass = Class.forName("org.apache.geode.internal.cache.LocalRegion");
            return localRegionClass.isInstance(nativeRegion);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    public GudRegionEntry<K, V> getEntry(Object key) {
        // TODO: implement proper wrapper
        throw new UnsupportedOperationException("getEntry not yet implemented");
    }

    @Override
    public Set<java.util.Map.Entry<K, V>> entrySet() {
        return nativeRegion.entrySet();
    }

    @Override
    public Set<GudRegionEntry<K, V>> entrySet(boolean recursive) {
        // TODO: implement proper wrapper
        throw new UnsupportedOperationException("entrySet(recursive) not yet implemented");
    }

    @Override
    public Set<K> keySet() {
        return nativeRegion.keySet();
    }

    @Override
    public Collection<V> values() {
        return nativeRegion.values();
    }

    @Override
    public boolean containsKeyOnServer(Object key) {
        return nativeRegion.containsKeyOnServer(key);
    }

    @Override
    public boolean containsValueForKey(Object key) {
        return nativeRegion.containsValueForKey(key);
    }

    @Override
    public Set<K> keySetOnServer() {
        return nativeRegion.keySetOnServer();
    }

    @Override
    public boolean isEmptyOnServer() {
        return nativeRegion.isEmptyOnServer();
    }

    @Override
    public int sizeOnServer() {
        return nativeRegion.sizeOnServer();
    }

    // Interest registration methods - delegate to native
    @Override
    public void registerInterest(K key) {
        nativeRegion.registerInterest(key);
    }

    @Override
    public void registerInterest(K key, boolean isDurable) {
        nativeRegion.registerInterest(key, isDurable);
    }

    @Override
    public void registerInterest(K key, boolean isDurable, boolean receiveValues) {
        nativeRegion.registerInterest(key, isDurable, receiveValues);
    }

    @Override
    public void registerInterest(K key, GudInterestResultPolicy policy) {
        nativeRegion.registerInterest(key, toNativeInterestResultPolicy(policy));
    }

    @Override
    public void registerInterest(K key, GudInterestResultPolicy policy, boolean isDurable) {
        nativeRegion.registerInterest(key, toNativeInterestResultPolicy(policy), isDurable);
    }

    @Override
    public void registerInterest(K key, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        nativeRegion.registerInterest(key, toNativeInterestResultPolicy(policy), isDurable, receiveValues);
    }

    @Override
    public void registerInterestForAllKeys() {
        nativeRegion.registerInterestForAllKeys();
    }

    @Override
    public void registerInterestForAllKeys(GudInterestResultPolicy policy) {
        nativeRegion.registerInterestForAllKeys(toNativeInterestResultPolicy(policy));
    }

    @Override
    public void registerInterestForAllKeys(GudInterestResultPolicy policy, boolean isDurable) {
        nativeRegion.registerInterestForAllKeys(toNativeInterestResultPolicy(policy), isDurable);
    }

    @Override
    public void registerInterestForAllKeys(GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        nativeRegion.registerInterestForAllKeys(toNativeInterestResultPolicy(policy), isDurable, receiveValues);
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys) {
        nativeRegion.registerInterestForKeys(keys);
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy) {
        nativeRegion.registerInterestForKeys(keys, toNativeInterestResultPolicy(policy));
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy, boolean isDurable) {
        nativeRegion.registerInterestForKeys(keys, toNativeInterestResultPolicy(policy), isDurable);
    }

    @Override
    public void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        nativeRegion.registerInterestForKeys(keys, toNativeInterestResultPolicy(policy), isDurable, receiveValues);
    }

    @Override
    public void registerInterestRegex(String regex) {
        nativeRegion.registerInterestRegex(regex);
    }

    @Override
    public void registerInterestRegex(String regex, boolean isDurable) {
        nativeRegion.registerInterestRegex(regex, isDurable);
    }

    @Override
    public void registerInterestRegex(String regex, boolean isDurable, boolean receiveValues) {
        nativeRegion.registerInterestRegex(regex, isDurable, receiveValues);
    }

    @Override
    public void registerInterestRegex(String regex, GudInterestResultPolicy policy) {
        nativeRegion.registerInterestRegex(regex, toNativeInterestResultPolicy(policy));
    }

    @Override
    public void registerInterestRegex(String regex, GudInterestResultPolicy policy, boolean isDurable) {
        nativeRegion.registerInterestRegex(regex, toNativeInterestResultPolicy(policy), isDurable);
    }

    @Override
    public void registerInterestRegex(String regex, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues) {
        nativeRegion.registerInterestRegex(regex, toNativeInterestResultPolicy(policy), isDurable, receiveValues);
    }

    @Override
    public void unregisterInterest(K key) {
        nativeRegion.unregisterInterest(key);
    }

    @Override
    public void unregisterInterestRegex(String regex) {
        nativeRegion.unregisterInterestRegex(regex);
    }

    @Override
    public List<K> getInterestList() {
        return nativeRegion.getInterestList();
    }

    @Override
    public List<String> getInterestListRegex() {
        return nativeRegion.getInterestListRegex();
    }

    @Override
    public GudSelectResults<V> query(String queryPredicate) {
        // TODO: implement proper wrapper
        throw new UnsupportedOperationException("query not yet implemented");
    }

    @Override
    public Object selectValue(String queryPredicate) {
        try {
            return nativeRegion.selectValue(queryPredicate);
        } catch (Exception e) {
            throw new RuntimeException("Query failed", e);
        }
    }

    @Override
    public boolean existsValue(String queryPredicate) {
        try {
            return nativeRegion.existsValue(queryPredicate);
        } catch (Exception e) {
            throw new RuntimeException("Query failed", e);
        }
    }

    @Override
    public Lock getDistributedLock(Object key) {
        return nativeRegion.getDistributedLock(key);
    }

    @Override
    public Lock getRegionDistributedLock() {
        return nativeRegion.getRegionDistributedLock();
    }

    @Override
    public void becomeLockGrantor() {
        nativeRegion.becomeLockGrantor();
    }

    @Override
    public Object getUserAttribute() {
        return nativeRegion.getUserAttribute();
    }

    @Override
    public void setUserAttribute(Object value) {
        nativeRegion.setUserAttribute(value);
    }

    @Override
    public void saveSnapshot(OutputStream outputStream) {
        throw new UnsupportedOperationException("saveSnapshot not yet implemented");
    }

    @Override
    public void loadSnapshot(InputStream inputStream) {
        throw new UnsupportedOperationException("loadSnapshot not yet implemented");
    }

    @Override
    public GudRegionSnapshotService<K, V> getSnapshotService() {
        throw new UnsupportedOperationException("getSnapshotService not yet implemented");
    }

    @Override
    public void writeToDisk() {
        // Deprecated, no-op
    }

    @Override
    public void forceRolling() {
        nativeRegion.forceRolling();
    }

    // ConcurrentMap methods
    @Override
    public int size() {
        return nativeRegion.size();
    }

    @Override
    public boolean isEmpty() {
        return nativeRegion.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return nativeRegion.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return nativeRegion.containsValue(value);
    }

    @Override
    public V remove(Object key) {
        return nativeRegion.remove(key);
    }

    @Override
    public void clear() {
        nativeRegion.clear();
    }

    @Override
    public V putIfAbsent(K key, V value) {
        return nativeRegion.putIfAbsent(key, value);
    }

    @Override
    public boolean remove(Object key, Object value) {
        return nativeRegion.remove(key, value);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        return nativeRegion.replace(key, oldValue, newValue);
    }

    @Override
    public V replace(K key, V value) {
        return nativeRegion.replace(key, value);
    }

    private org.apache.geode.cache.InterestResultPolicy toNativeInterestResultPolicy(GudInterestResultPolicy policy) {
        if (policy == null) {
            return null;
        }
        switch (policy) {
            case KEYS: return org.apache.geode.cache.InterestResultPolicy.KEYS;
            case KEYS_VALUES: return org.apache.geode.cache.InterestResultPolicy.KEYS_VALUES;
            case NONE: return org.apache.geode.cache.InterestResultPolicy.NONE;
            default: return org.apache.geode.cache.InterestResultPolicy.DEFAULT;
        }
    }
}
