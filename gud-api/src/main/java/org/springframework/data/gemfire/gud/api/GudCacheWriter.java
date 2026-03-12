/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheWriter interface as 1:1 mapping of GemFire CacheWriter
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheWriter interface.
 * A write-through cache writer that is invoked before an operation occurs.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudCacheWriter<K, V> extends GudCacheCallback {

    void beforeCreate(GudEntryEvent<K, V> event) throws GudCacheWriterException;

    void beforeUpdate(GudEntryEvent<K, V> event) throws GudCacheWriterException;

    void beforeDestroy(GudEntryEvent<K, V> event) throws GudCacheWriterException;

    void beforeRegionDestroy(GudRegionEvent<K, V> event) throws GudCacheWriterException;

    void beforeRegionClear(GudRegionEvent<K, V> event) throws GudCacheWriterException;
}
