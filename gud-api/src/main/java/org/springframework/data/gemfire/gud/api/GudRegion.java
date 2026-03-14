/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
 * 2026-03-11: Created GudRegion interface as 1:1 mapping of GemFire Region
 * 2026-03-14: Added default methods for API evolution support (10.4+ features)
 */

package org.springframework.data.gemfire.gud.api;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;

/**
 * GUD API abstraction for GemFire Region interface.
 * Manages subregions and cached data with hierarchical namespace support.
 *
 * @param <K> the type of keys maintained by this region
 * @param <V> the type of mapped values
 */
public interface GudRegion<K, V> extends ConcurrentMap<K, V> {

    String SEPARATOR = "/";
    char SEPARATOR_CHAR = '/';

    // Region identification
    String getName();
    String getFullPath();

    // Region hierarchy
    GudRegion<?, ?> getParentRegion();
    <SK, SV> GudRegion<SK, SV> getSubregion(String path);
    Set<GudRegion<?, ?>> subregions(boolean recursive);

    // Region attributes and mutation
    GudRegionAttributes<K, V> getAttributes();
    GudAttributesMutator<K, V> getAttributesMutator();

    // Cache/RegionService access
    GudRegionService getRegionService();

    // Statistics
    GudCacheStatistics getStatistics();

    // Entry operations
    V get(Object key);
    V get(Object key, Object aCallbackArgument);
    V put(K key, V value);
    V put(K key, V value, Object aCallbackArgument);
    void create(K key, V value);
    void create(K key, V value, Object aCallbackArgument);
    V destroy(Object key);
    V destroy(Object key, Object aCallbackArgument);
    void invalidate(Object key);
    void invalidate(Object key, Object aCallbackArgument);

    // Bulk operations
    Map<K, V> getAll(Collection<?> keys);
    Map<K, V> getAll(Collection<?> keys, Object aCallbackArgument);
    void putAll(Map<? extends K, ? extends V> map);
    void putAll(Map<? extends K, ? extends V> map, Object aCallbackArgument);
    void removeAll(Collection<?> keys);
    void removeAll(Collection<?> keys, Object aCallbackArgument);

    // Local operations
    void localDestroy(Object key);
    void localDestroy(Object key, Object aCallbackArgument);
    void localInvalidate(Object key);
    void localInvalidate(Object key, Object aCallbackArgument);
    void localClear();

    // Region lifecycle
    void destroyRegion();
    void destroyRegion(Object aCallbackArgument);
    void localDestroyRegion();
    void localDestroyRegion(Object aCallbackArgument);
    void invalidateRegion();
    void invalidateRegion(Object aCallbackArgument);
    void localInvalidateRegion();
    void localInvalidateRegion(Object aCallbackArgument);
    void close();
    boolean isDestroyed();

    /**
     * Determines whether this region is a non-distributed, local region.
     * A local region is one that does not distribute operations to other members.
     *
     * @return {@code true} if this is a local (non-distributed) region, {@code false} otherwise
     */
    boolean isLocalRegion();

    // Entry access
    GudRegionEntry<K, V> getEntry(Object key);
    Set<Map.Entry<K, V>> entrySet();
    Set<GudRegionEntry<K, V>> entrySet(boolean recursive);
    Set<K> keySet();
    Collection<V> values();

    // Server operations
    boolean containsKeyOnServer(Object key);
    boolean containsValueForKey(Object key);
    Set<K> keySetOnServer();
    boolean isEmptyOnServer();
    int sizeOnServer();

    // Interest registration (client regions)
    void registerInterest(K key);
    void registerInterest(K key, boolean isDurable);
    void registerInterest(K key, boolean isDurable, boolean receiveValues);
    void registerInterest(K key, GudInterestResultPolicy policy);
    void registerInterest(K key, GudInterestResultPolicy policy, boolean isDurable);
    void registerInterest(K key, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues);
    void registerInterestForAllKeys();
    void registerInterestForAllKeys(GudInterestResultPolicy policy);
    void registerInterestForAllKeys(GudInterestResultPolicy policy, boolean isDurable);
    void registerInterestForAllKeys(GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues);
    void registerInterestForKeys(Iterable<K> keys);
    void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy);
    void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy, boolean isDurable);
    void registerInterestForKeys(Iterable<K> keys, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues);
    void registerInterestRegex(String regex);
    void registerInterestRegex(String regex, boolean isDurable);
    void registerInterestRegex(String regex, boolean isDurable, boolean receiveValues);
    void registerInterestRegex(String regex, GudInterestResultPolicy policy);
    void registerInterestRegex(String regex, GudInterestResultPolicy policy, boolean isDurable);
    void registerInterestRegex(String regex, GudInterestResultPolicy policy, boolean isDurable, boolean receiveValues);
    void unregisterInterest(K key);
    void unregisterInterestRegex(String regex);
    List<K> getInterestList();
    List<String> getInterestListRegex();

    // Query operations
    GudSelectResults<V> query(String queryPredicate);
    Object selectValue(String queryPredicate);
    boolean existsValue(String queryPredicate);

    // Locking
    Lock getDistributedLock(Object key);
    Lock getRegionDistributedLock();
    void becomeLockGrantor();

    // User attribute
    Object getUserAttribute();
    void setUserAttribute(Object value);

    // Snapshot operations
    void saveSnapshot(OutputStream outputStream);
    void loadSnapshot(InputStream inputStream);
    GudRegionSnapshotService<K, V> getSnapshotService();

    // Disk operations (deprecated but included for compatibility)
    void writeToDisk();
    void forceRolling();

    /**
     * Interface representing an entry in a GudRegion.
     * Used for iteration and callbacks.
     *
     * @param <K> the type of the key
     * @param <V> the type of the value
     */
    interface Entry<K, V> {
        K getKey();
        V getValue();
        GudRegion<K, V> getRegion();
        boolean isLocal();
        GudCacheStatistics getStatistics();
        Object getUserAttribute();
        Object setUserAttribute(Object userAttribute);
        boolean isDestroyed();
    }

    // ===== API Evolution: 10.4+ Features =====
    // These default methods throw UnsupportedOperationException when called on older drivers.
    // Drivers supporting these features override these methods with actual implementations.

    /**
     * Returns partition statistics for this region.
     * This feature requires GemFire 10.4 or later.
     *
     * @return partition statistics
     * @throws GudUnsupportedOperationException if the driver doesn't support this feature
     * @since GUD API 1.1 (GemFire 10.4+)
     */
    default GudPartitionStatistics getPartitionStatistics() {
        throw new GudUnsupportedOperationException(
            "getPartitionStatistics() requires a GemFire 10.4+ driver. " +
            "Check driver.supportsCapability(GudCapability.PARTITION_STATISTICS) before calling.",
            "PARTITION_STATISTICS", "10.4");
    }
}
