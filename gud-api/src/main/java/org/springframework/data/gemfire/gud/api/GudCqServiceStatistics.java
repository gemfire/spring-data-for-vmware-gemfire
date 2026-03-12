/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCqServiceStatistics interface as 1:1 mapping of GemFire CqServiceStatistics
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire CqServiceStatistics interface.
 * Statistics for the CQ service.
 */
public interface GudCqServiceStatistics {

    long numCqsActive();
    long numCqsStopped();
    long numCqsClosed();
    long numCqsCreated();
    long numCqsOnClient();
}
