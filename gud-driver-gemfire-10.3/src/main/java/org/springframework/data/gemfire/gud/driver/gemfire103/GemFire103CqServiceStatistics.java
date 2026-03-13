/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 CqServiceStatistics adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.query.CqServiceStatistics;

import org.springframework.data.gemfire.gud.api.GudCqServiceStatistics;

/**
 * GUD API adapter for GemFire 10.3 CqServiceStatistics.
 */
public class GemFire103CqServiceStatistics implements GudCqServiceStatistics, NativeWrapper<CqServiceStatistics> {

    private final CqServiceStatistics nativeStatistics;

    public GemFire103CqServiceStatistics(CqServiceStatistics nativeStatistics) {
        this.nativeStatistics = nativeStatistics;
    }

    @Override
    public CqServiceStatistics getNative() {
        return nativeStatistics;
    }

    @Override
    public long numCqsActive() {
        return nativeStatistics.numCqsActive();
    }

    @Override
    public long numCqsStopped() {
        return nativeStatistics.numCqsStopped();
    }

    @Override
    public long numCqsClosed() {
        return nativeStatistics.numCqsClosed();
    }

    @Override
    public long numCqsCreated() {
        return nativeStatistics.numCqsCreated();
    }

    @Override
    public long numCqsOnClient() {
        return nativeStatistics.numCqsOnClient();
    }
}
