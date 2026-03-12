/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionEvent interface as 1:1 mapping of GemFire RegionEvent
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire RegionEvent interface.
 * Contains information about a region event.
 *
 * @param <K> the type of keys in the region
 * @param <V> the type of values in the region
 */
public interface GudRegionEvent<K, V> extends GudCacheEvent<K, V> {

    Object getCallbackArgument();

    boolean isCallbackArgumentAvailable();

    boolean isReinitializing();
}
