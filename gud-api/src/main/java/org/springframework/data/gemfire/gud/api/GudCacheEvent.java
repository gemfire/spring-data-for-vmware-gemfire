/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheEvent interface as 1:1 mapping of GemFire CacheEvent
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheEvent interface.
 * Base interface for cache events.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudCacheEvent<K, V> {

    GudRegion<K, V> getRegion();

    GudOperation getOperation();

    GudDistributedMember getDistributedMember();

    boolean isOriginRemote();

    boolean isExpiration();

    boolean isDistributed();
}
