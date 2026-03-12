/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudQueryStatistics interface as 1:1 mapping of GemFire QueryStatistics
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire QueryStatistics interface.
 * Statistics for query execution.
 */
public interface GudQueryStatistics {

    long getNumExecutions();

    long getTotalExecutionTime();
}
