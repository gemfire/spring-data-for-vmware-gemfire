/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
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
 * 2026-03-11: Created GudRegionFactory interface as 1:1 mapping of GemFire RegionFactory
 * 2026-04-17: Marked deprecated — peer region factories are outside client-only GUD
 */

package org.springframework.data.gemfire.gud.api;

import java.io.File;

/**
 * GUD API abstraction for GemFire {@code RegionFactory} on a <strong>peer</strong> cache.
 *
 * @deprecated The GUD contract is <strong>client-only</strong>. Client applications should use
 *             {@link GudClientRegionFactory}. Server-side / peer region creation should be driven
 *             by GemFire Testcontainers (or native server tooling), not {@code GudRegionFactory}.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
@Deprecated(since = "4.0")
public interface GudRegionFactory<K, V> {

    GudRegionFactory<K, V> setDataPolicy(GudDataPolicy dataPolicy);
    GudRegionFactory<K, V> setScope(GudScope scope);
    GudRegionFactory<K, V> setKeyConstraint(Class<K> keyConstraint);
    GudRegionFactory<K, V> setValueConstraint(Class<V> valueConstraint);

    GudRegionFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader);
    GudRegionFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter);
    GudRegionFactory<K, V> addCacheListener(GudCacheListener<K, V> cacheListener);
    GudRegionFactory<K, V> initCacheListeners(GudCacheListener<K, V>[] cacheListeners);

    GudRegionFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive);
    GudRegionFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout);
    GudRegionFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive);
    GudRegionFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout);
    GudRegionFactory<K, V> setCustomEntryTimeToLive(GudCustomExpiry<K, V> customExpiry);
    GudRegionFactory<K, V> setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customExpiry);

    GudRegionFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes);

    GudRegionFactory<K, V> setPartitionAttributes(GudPartitionAttributes<K, V> partitionAttributes);
    GudRegionFactory<K, V> setMembershipAttributes(GudMembershipAttributes membershipAttributes);
    GudRegionFactory<K, V> setSubscriptionAttributes(GudSubscriptionAttributes subscriptionAttributes);

    GudRegionFactory<K, V> setDiskStoreName(String name);
    GudRegionFactory<K, V> setDiskSynchronous(boolean isSynchronous);
    
    GudRegionFactory<K, V> setEnableSubscriptionConflation(boolean enableSubscriptionConflation);
    GudRegionFactory<K, V> setIgnoreJTA(boolean ignoreJTA);

    GudRegionFactory<K, V> setPoolName(String poolName);

    GudRegionFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled);
    GudRegionFactory<K, V> setLockGrantor(boolean lockGrantor);
    GudRegionFactory<K, V> setConcurrencyLevel(int concurrencyLevel);
    GudRegionFactory<K, V> setConcurrencyChecksEnabled(boolean concurrencyChecksEnabled);
    GudRegionFactory<K, V> setInitialCapacity(int initialCapacity);
    GudRegionFactory<K, V> setLoadFactor(float loadFactor);

    GudRegionFactory<K, V> setCloningEnabled(boolean cloningEnabled);
    GudRegionFactory<K, V> setMulticastEnabled(boolean multicastEnabled);
    GudRegionFactory<K, V> setOffHeap(boolean offHeap);

    GudRegionFactory<K, V> setCompressor(GudCompressor compressor);

    GudRegionFactory<K, V> addGatewaySenderId(String gatewaySenderId);
    GudRegionFactory<K, V> addAsyncEventQueueId(String asyncEventQueueId);

    GudRegion<K, V> create(String regionName);
    GudRegion<K, V> createSubregion(GudRegion<?, ?> parent, String subregionName);
}
