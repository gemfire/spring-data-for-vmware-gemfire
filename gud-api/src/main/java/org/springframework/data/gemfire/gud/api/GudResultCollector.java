/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudResultCollector interface as 1:1 mapping of GemFire ResultCollector
 */

package org.springframework.data.gemfire.gud.api;

import java.util.concurrent.TimeUnit;

/**
 * GUD API abstraction for GemFire ResultCollector interface.
 * Collects results from function executions.
 *
 * @param <T> the type of individual results
 * @param <S> the type of the aggregated result
 */
public interface GudResultCollector<T, S> {

    S getResult() throws GudFunctionException;

    S getResult(long timeout, TimeUnit unit) throws GudFunctionException, InterruptedException;

    void addResult(GudDistributedMember memberID, T resultOfSingleExecution);

    void endResults();

    void clearResults();
}
