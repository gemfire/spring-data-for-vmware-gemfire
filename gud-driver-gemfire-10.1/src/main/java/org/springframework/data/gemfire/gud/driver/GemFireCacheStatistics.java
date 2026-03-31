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
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.1 CacheStatistics adapter
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.CacheStatistics;

import org.springframework.data.gemfire.gud.api.GudCacheStatistics;

/**
 * GUD API adapter for GemFire 10.1 CacheStatistics.
 */
public class GemFireCacheStatistics implements GudCacheStatistics, NativeWrapper<CacheStatistics> {

    private final CacheStatistics nativeStatistics;

    public GemFireCacheStatistics(CacheStatistics nativeStatistics) {
        this.nativeStatistics = nativeStatistics;
    }

    @Override
    public CacheStatistics getNative() {
        return nativeStatistics;
    }

    @Override
    public long getHitCount() {
        return nativeStatistics.getHitCount();
    }

    @Override
    public float getHitRatio() {
        return nativeStatistics.getHitRatio();
    }

    @Override
    public long getMissCount() {
        return nativeStatistics.getMissCount();
    }

    @Override
    public long getLastAccessedTime() {
        return nativeStatistics.getLastAccessedTime();
    }

    @Override
    public long getLastModifiedTime() {
        return nativeStatistics.getLastModifiedTime();
    }

    @Override
    public void resetCounts() {
        nativeStatistics.resetCounts();
    }
}
