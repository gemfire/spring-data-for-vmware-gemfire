/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheStatistics interface as 1:1 mapping of GemFire CacheStatistics
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CacheStatistics interface.
 * Cache statistics for entries and regions.
 */
public interface GudCacheStatistics {

    long getHitCount();

    float getHitRatio();

    long getMissCount();

    long getLastAccessedTime();

    long getLastModifiedTime();

    void resetCounts();
}
