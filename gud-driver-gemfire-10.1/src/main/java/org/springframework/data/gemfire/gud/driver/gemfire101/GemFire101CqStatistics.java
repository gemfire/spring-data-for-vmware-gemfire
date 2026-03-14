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
 * 2026-03-13: Created GemFire 10.1 CqStatistics adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import org.apache.geode.cache.query.CqStatistics;

import org.springframework.data.gemfire.gud.api.GudCqStatistics;

/**
 * GUD API adapter for GemFire 10.1 CqStatistics.
 */
public class GemFire101CqStatistics implements GudCqStatistics, NativeWrapper<CqStatistics> {

    private final CqStatistics nativeStatistics;

    public GemFire101CqStatistics(CqStatistics nativeStatistics) {
        this.nativeStatistics = nativeStatistics;
    }

    @Override
    public CqStatistics getNative() {
        return nativeStatistics;
    }

    @Override
    public long numInserts() {
        return nativeStatistics.numInserts();
    }

    @Override
    public long numUpdates() {
        return nativeStatistics.numUpdates();
    }

    @Override
    public long numDeletes() {
        return nativeStatistics.numDeletes();
    }

    @Override
    public long numEvents() {
        return nativeStatistics.numEvents();
    }
}
