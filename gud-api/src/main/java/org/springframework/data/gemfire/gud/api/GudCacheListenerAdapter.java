/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheListenerAdapter for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheListenerAdapter.
 * Provides default no-op implementations for GudCacheListener methods.
 */
public abstract class GudCacheListenerAdapter<K, V> implements GudCacheListener<K, V> {

    @Override
    public void afterCreate(GudEntryEvent<K, V> event) {
    }

    @Override
    public void afterUpdate(GudEntryEvent<K, V> event) {
    }

    @Override
    public void afterInvalidate(GudEntryEvent<K, V> event) {
    }

    @Override
    public void afterDestroy(GudEntryEvent<K, V> event) {
    }

    @Override
    public void afterRegionInvalidate(GudRegionEvent<K, V> event) {
    }

    @Override
    public void afterRegionDestroy(GudRegionEvent<K, V> event) {
    }

    @Override
    public void afterRegionClear(GudRegionEvent<K, V> event) {
    }

    @Override
    public void afterRegionCreate(GudRegionEvent<K, V> event) {
    }

    @Override
    public void afterRegionLive(GudRegionEvent<K, V> event) {
    }

    @Override
    public void close() {
    }
}
