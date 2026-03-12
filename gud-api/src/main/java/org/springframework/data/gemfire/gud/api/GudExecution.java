/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudExecution interface as 1:1 mapping of GemFire Execution
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire Execution interface.
 * Provides the ability to execute functions.
 *
 * @param <IN> the type of the argument
 * @param <OUT> the type of the result item
 * @param <AGG> the type of the aggregated result
 */
public interface GudExecution<IN, OUT, AGG> {

    GudExecution<IN, OUT, AGG> withFilter(Set<?> filter);

    GudExecution<IN, OUT, AGG> withArgs(IN args);

    GudExecution<IN, OUT, AGG> withCollector(GudResultCollector<OUT, AGG> collector);

    GudResultCollector<OUT, AGG> execute(String functionId);

    GudResultCollector<OUT, AGG> execute(GudFunction function);
}
