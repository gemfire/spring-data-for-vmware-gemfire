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
 * 2026-03-13: Created CacheLoader adapter to wrap GudCacheLoader for native GemFire usage
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;

import org.springframework.data.gemfire.gud.api.GudCacheLoader;

/**
 * Adapter that wraps a GudCacheLoader to implement the native GemFire CacheLoader interface.
 * This allows application-defined GudCacheLoader implementations to be used with native GemFire regions.
 */
public class GemFireCacheLoaderAdapter<K, V> implements CacheLoader<K, V> {

    private final GudCacheLoader<K, V> gudCacheLoader;

    public GemFireCacheLoaderAdapter(GudCacheLoader<K, V> gudCacheLoader) {
        this.gudCacheLoader = gudCacheLoader;
    }

    @Override
    public V load(LoaderHelper<K, V> helper) throws CacheLoaderException {
        try {
            return gudCacheLoader.load(new GemFireLoaderHelper<>(helper));
        } catch (Exception e) {
            throw new CacheLoaderException("Error in GudCacheLoader.load()", e);
        }
    }

    @Override
    public void close() {
        gudCacheLoader.close();
    }

    public GudCacheLoader<K, V> getGudCacheLoader() {
        return gudCacheLoader;
    }
}
