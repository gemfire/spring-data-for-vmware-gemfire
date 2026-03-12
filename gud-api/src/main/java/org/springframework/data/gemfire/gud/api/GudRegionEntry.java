/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRegionEntry interface as 1:1 mapping of GemFire Region.Entry
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Map;

/**
 * GUD API abstraction for GemFire Region.Entry interface.
 * A key-value pair containing cached data in a region.
 *
 * @param <K> the type of the key
 * @param <V> the type of the value
 */
public interface GudRegionEntry<K, V> extends Map.Entry<K, V> {

    GudRegion<K, V> getRegion();

    GudCacheStatistics getStatistics();

    Object getUserAttribute();

    Object setUserAttribute(Object userAttribute);

    boolean isLocal();

    boolean isDestroyed();
}
