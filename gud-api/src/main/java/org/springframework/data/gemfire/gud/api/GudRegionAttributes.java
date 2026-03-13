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
 * 2026-03-11: Created GudRegionAttributes interface as 1:1 mapping of GemFire RegionAttributes
 */

package org.springframework.data.gemfire.gud.api;

import java.io.File;
import java.util.Set;

/**
 * GUD API abstraction for GemFire RegionAttributes interface.
 * Defines the attributes of a region.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudRegionAttributes<K, V> {

    // Basic attributes
    GudDataPolicy getDataPolicy();
    GudScope getScope();
    boolean getStatisticsEnabled();
    boolean getCloningEnabled();
    boolean getConcurrencyChecksEnabled();
    int getConcurrencyLevel();
    boolean isLockGrantor();

    // Persistence
    boolean isDiskSynchronous();
    String getDiskStoreName();

    // Pool
    String getPoolName();

    // Callbacks
    GudCacheLoader<K, V> getCacheLoader();
    GudCacheWriter<K, V> getCacheWriter();
    GudCacheListener<K, V>[] getCacheListeners();

    // Expiration
    GudExpirationAttributes getRegionTimeToLive();
    GudExpirationAttributes getRegionIdleTimeout();
    GudExpirationAttributes getEntryTimeToLive();
    GudExpirationAttributes getEntryIdleTimeout();
    GudCustomExpiry<K, V> getCustomEntryTimeToLive();
    GudCustomExpiry<K, V> getCustomEntryIdleTimeout();

    // Eviction
    GudEvictionAttributes getEvictionAttributes();

    // Membership
    GudMembershipAttributes getMembershipAttributes();
    GudSubscriptionAttributes getSubscriptionAttributes();
    
    // Subscription conflation
    boolean getEnableSubscriptionConflation();
    
    // JTA
    boolean getIgnoreJTA();

    // Partitioning
    GudPartitionAttributes<K, V> getPartitionAttributes();

    // Compression
    GudCompressor getCompressor();

    // Key constraint
    Class<K> getKeyConstraint();
    Class<V> getValueConstraint();

    // Initial capacity
    int getInitialCapacity();
    float getLoadFactor();

    // Multicast
    boolean getMulticastEnabled();

    // Offheap
    boolean getOffHeap();

    // Gateway
    Set<String> getGatewaySenderIds();
    Set<String> getAsyncEventQueueIds();
}
