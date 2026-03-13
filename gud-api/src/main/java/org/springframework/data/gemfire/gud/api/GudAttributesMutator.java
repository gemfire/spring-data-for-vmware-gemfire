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
 * 2026-03-11: Created GudAttributesMutator interface as 1:1 mapping of GemFire AttributesMutator
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire AttributesMutator interface.
 * Used to modify region attributes after region creation.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudAttributesMutator<K, V> {

    GudRegion<K, V> getRegion();

    // Expiration
    GudExpirationAttributes setRegionTimeToLive(GudExpirationAttributes timeToLive);
    GudExpirationAttributes setRegionIdleTimeout(GudExpirationAttributes idleTimeout);
    GudExpirationAttributes setEntryTimeToLive(GudExpirationAttributes timeToLive);
    GudExpirationAttributes setEntryIdleTimeout(GudExpirationAttributes idleTimeout);
    GudCustomExpiry<K, V> setCustomEntryTimeToLive(GudCustomExpiry<K, V> customExpiry);
    GudCustomExpiry<K, V> setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customExpiry);

    // Callbacks
    GudCacheLoader<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader);
    GudCacheWriter<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter);
    void addCacheListener(GudCacheListener<K, V> cacheListener);
    void removeCacheListener(GudCacheListener<K, V> cacheListener);
    void initCacheListeners(GudCacheListener<K, V>[] cacheListeners);

    // Eviction
    GudEvictionAttributesMutator getEvictionAttributesMutator();
    void setEvictionMaximum(int maximum);

    // Cloning
    void setCloningEnabled(boolean cloningEnabled);

    // Gateway senders
    void addGatewaySenderId(String gatewaySenderId);
    void removeGatewaySenderId(String gatewaySenderId);

    // Async event queues
    void addAsyncEventQueueId(String asyncEventQueueId);
    void removeAsyncEventQueueId(String asyncEventQueueId);
}
