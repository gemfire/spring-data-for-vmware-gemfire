/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRebalanceOperation interface as 1:1 mapping of GemFire RebalanceOperation
 */

package org.springframework.data.gemfire.gud.api;

import java.util.concurrent.TimeUnit;

/**
 * GUD API abstraction for GemFire RebalanceOperation interface.
 * Represents a running or completed rebalance operation.
 */
public interface GudRebalanceOperation {

    boolean cancel();

    boolean isCancelled();

    boolean isDone();

    GudRebalanceResults getResults() throws InterruptedException;

    GudRebalanceResults getResults(long timeout, TimeUnit unit) throws InterruptedException;
}
