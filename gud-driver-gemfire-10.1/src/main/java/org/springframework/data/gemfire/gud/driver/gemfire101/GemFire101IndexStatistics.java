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
 * 2026-03-13: Created GemFire 10.1 IndexStatistics adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import org.apache.geode.cache.query.IndexStatistics;

import org.springframework.data.gemfire.gud.api.GudIndexStatistics;

/**
 * GUD API adapter for GemFire 10.1 IndexStatistics.
 */
public class GemFire101IndexStatistics implements GudIndexStatistics, NativeWrapper<IndexStatistics> {

    private final IndexStatistics nativeStatistics;

    public GemFire101IndexStatistics(IndexStatistics nativeStatistics) {
        this.nativeStatistics = nativeStatistics;
    }

    @Override
    public IndexStatistics getNative() {
        return nativeStatistics;
    }

    @Override
    public long getNumberOfBucketIndexes() {
        return nativeStatistics.getNumberOfBucketIndexes();
    }

    @Override
    public long getNumberOfMapIndexKeys() {
        return nativeStatistics.getNumberOfMapIndexKeys();
    }

    @Override
    public long getNumberOfKeys() {
        return nativeStatistics.getNumberOfKeys();
    }

    @Override
    public long getNumberOfValues() {
        return nativeStatistics.getNumberOfValues();
    }

    @Override
    public long getNumUpdates() {
        return nativeStatistics.getNumUpdates();
    }

    @Override
    public long getTotalUpdateTime() {
        return nativeStatistics.getTotalUpdateTime();
    }

    @Override
    public long getTotalUses() {
        return nativeStatistics.getTotalUses();
    }

    @Override
    public long getReadLockCount() {
        return nativeStatistics.getReadLockCount();
    }
}
