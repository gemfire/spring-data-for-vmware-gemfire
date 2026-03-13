/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created adapter to wrap GudCacheWriter for native GemFire use
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.CacheWriter;
import org.apache.geode.cache.CacheWriterException;
import org.apache.geode.cache.EntryEvent;
import org.apache.geode.cache.RegionEvent;

import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudCacheWriterException;

/**
 * Adapts a GudCacheWriter to the native GemFire CacheWriter interface.
 * This allows application code to implement GudCacheWriter and have it
 * work with native GemFire regions.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFire103CacheWriterAdapter<K, V> implements CacheWriter<K, V>, NativeWrapper<GudCacheWriter<K, V>> {

    private final GudCacheWriter<K, V> gudWriter;

    public GemFire103CacheWriterAdapter(GudCacheWriter<K, V> gudWriter) {
        this.gudWriter = gudWriter;
    }

    @Override
    public GudCacheWriter<K, V> getNative() {
        return gudWriter;
    }

    @Override
    public void beforeCreate(EntryEvent<K, V> event) throws CacheWriterException {
        try {
            gudWriter.beforeCreate(new GemFire103EntryEvent<>(event));
        } catch (GudCacheWriterException e) {
            throw new CacheWriterException(e.getMessage(), e);
        }
    }

    @Override
    public void beforeUpdate(EntryEvent<K, V> event) throws CacheWriterException {
        try {
            gudWriter.beforeUpdate(new GemFire103EntryEvent<>(event));
        } catch (GudCacheWriterException e) {
            throw new CacheWriterException(e.getMessage(), e);
        }
    }

    @Override
    public void beforeDestroy(EntryEvent<K, V> event) throws CacheWriterException {
        try {
            gudWriter.beforeDestroy(new GemFire103EntryEvent<>(event));
        } catch (GudCacheWriterException e) {
            throw new CacheWriterException(e.getMessage(), e);
        }
    }

    @Override
    public void beforeRegionDestroy(RegionEvent<K, V> event) throws CacheWriterException {
        try {
            gudWriter.beforeRegionDestroy(new GemFire103RegionEvent<>(event));
        } catch (GudCacheWriterException e) {
            throw new CacheWriterException(e.getMessage(), e);
        }
    }

    @Override
    public void beforeRegionClear(RegionEvent<K, V> event) throws CacheWriterException {
        try {
            gudWriter.beforeRegionClear(new GemFire103RegionEvent<>(event));
        } catch (GudCacheWriterException e) {
            throw new CacheWriterException(e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        gudWriter.close();
    }
}
