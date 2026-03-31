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
 * 2026-03-13: Created adapter to wrap GudCacheListener for native GemFire use
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.CacheListener;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.RegionEvent;

import org.springframework.data.gemfire.gud.api.GudCacheListener;

/**
 * Adapts a GudCacheListener to the native GemFire CacheListener interface.
 * This allows application code to implement GudCacheListener and have it
 * work with native GemFire regions.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFireCacheListenerAdapter<K, V> implements CacheListener<K, V>, NativeWrapper<GudCacheListener<K, V>> {

    private final GudCacheListener<K, V> gudListener;

    public GemFireCacheListenerAdapter(GudCacheListener<K, V> gudListener) {
        this.gudListener = gudListener;
    }

    @Override
    public GudCacheListener<K, V> getNative() {
        return gudListener;
    }

    @Override
    public void afterCreate(EntryEvent<K, V> event) {
        gudListener.afterCreate(new GemFireEntryEvent<>(event));
    }

    @Override
    public void afterUpdate(EntryEvent<K, V> event) {
        gudListener.afterUpdate(new GemFireEntryEvent<>(event));
    }

    @Override
    public void afterInvalidate(EntryEvent<K, V> event) {
        gudListener.afterInvalidate(new GemFireEntryEvent<>(event));
    }

    @Override
    public void afterDestroy(EntryEvent<K, V> event) {
        gudListener.afterDestroy(new GemFireEntryEvent<>(event));
    }

    @Override
    public void afterRegionInvalidate(RegionEvent<K, V> event) {
        gudListener.afterRegionInvalidate(new GemFireRegionEvent<>(event));
    }

    @Override
    public void afterRegionDestroy(RegionEvent<K, V> event) {
        gudListener.afterRegionDestroy(new GemFireRegionEvent<>(event));
    }

    @Override
    public void afterRegionClear(RegionEvent<K, V> event) {
        gudListener.afterRegionClear(new GemFireRegionEvent<>(event));
    }

    @Override
    public void afterRegionCreate(RegionEvent<K, V> event) {
        gudListener.afterRegionCreate(new GemFireRegionEvent<>(event));
    }

    @Override
    public void afterRegionLive(RegionEvent<K, V> event) {
        gudListener.afterRegionLive(new GemFireRegionEvent<>(event));
    }

    @Override
    public void close() {
        gudListener.close();
    }
}
