/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheLoader interface as 1:1 mapping of GemFire CacheLoader
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheLoader interface.
 * Allows data from an outside source to be placed into a region.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudCacheLoader<K, V> extends GudCacheCallback {

    V load(GudLoaderHelper<K, V> helper) throws GudCacheLoaderException;
}
