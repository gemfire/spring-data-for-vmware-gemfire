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
 * 2026-03-13: Created LoaderHelper wrapper for GUD API
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;

import org.springframework.data.gemfire.gud.api.GudCacheLoaderException;
import org.springframework.data.gemfire.gud.api.GudLoaderHelper;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Wrapper that adapts a native GemFire LoaderHelper to the GudLoaderHelper interface.
 */
public class GemFire101LoaderHelper<K, V> implements GudLoaderHelper<K, V> {

    private final LoaderHelper<K, V> nativeHelper;

    public GemFire101LoaderHelper(LoaderHelper<K, V> nativeHelper) {
        this.nativeHelper = nativeHelper;
    }

    @Override
    public K getKey() {
        return nativeHelper.getKey();
    }

    @Override
    public GudRegion<K, V> getRegion() {
        return new GemFire101Region<>(nativeHelper.getRegion());
    }

    @Override
    public Object getArgument() {
        return nativeHelper.getArgument();
    }

    @Override
    public V netSearch(boolean doNetLoad) throws GudCacheLoaderException {
        try {
            return nativeHelper.netSearch(doNetLoad);
        } catch (CacheLoaderException e) {
            throw new GudCacheLoaderException(e.getMessage(), e);
        }
    }
}
