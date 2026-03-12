/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudLoaderHelper interface as 1:1 mapping of GemFire LoaderHelper
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire LoaderHelper interface.
 * Helper passed to cache loader during load operations.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface GudLoaderHelper<K, V> {

    K getKey();

    GudRegion<K, V> getRegion();

    Object getArgument();

    V netSearch(boolean doNetLoad) throws GudCacheLoaderException;
}
