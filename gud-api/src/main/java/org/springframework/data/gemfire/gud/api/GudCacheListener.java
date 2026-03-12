/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheListener interface as 1:1 mapping of GemFire CacheListener
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheListener interface.
 * Listener for cache events that occur after an operation completes.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudCacheListener<K, V> extends GudCacheCallback {

    void afterCreate(GudEntryEvent<K, V> event);

    void afterUpdate(GudEntryEvent<K, V> event);

    void afterInvalidate(GudEntryEvent<K, V> event);

    void afterDestroy(GudEntryEvent<K, V> event);

    void afterRegionInvalidate(GudRegionEvent<K, V> event);

    void afterRegionDestroy(GudRegionEvent<K, V> event);

    void afterRegionClear(GudRegionEvent<K, V> event);

    void afterRegionCreate(GudRegionEvent<K, V> event);

    void afterRegionLive(GudRegionEvent<K, V> event);
}
