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
 * 2026-03-13: Created GemFire 10.2 Execution adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.util.Set;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.ResultCollector;

import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudResultCollector;

/**
 * GUD API adapter for GemFire 10.2 Execution.
 *
 * @param <IN> the type of the argument
 * @param <OUT> the type of the result item
 * @param <AGG> the type of the aggregated result
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class GemFireExecution<IN, OUT, AGG> implements GudExecution<IN, OUT, AGG>, NativeWrapper<Execution> {

    private Execution nativeExecution;

    public GemFireExecution(Execution nativeExecution) {
        this.nativeExecution = nativeExecution;
    }

    @Override
    public Execution getNative() {
        return nativeExecution;
    }

    @Override
    public GudExecution<IN, OUT, AGG> withFilter(Set<?> filter) {
        nativeExecution = nativeExecution.withFilter(filter);
        return this;
    }

    @Override
    public GudExecution<IN, OUT, AGG> withArgs(IN args) {
        nativeExecution = nativeExecution.setArguments(args);
        return this;
    }

    @Override
    public GudExecution<IN, OUT, AGG> setArguments(Object args) {
        nativeExecution = nativeExecution.setArguments(args);
        return this;
    }

    @Override
    public GudExecution<IN, OUT, AGG> withCollector(GudResultCollector<OUT, AGG> collector) {
        if (collector instanceof NativeWrapper) {
            nativeExecution = nativeExecution.withCollector(((NativeWrapper<ResultCollector>) collector).getNative());
        }
        return this;
    }

    @Override
    public GudResultCollector<OUT, AGG> execute(String functionId) {
        ResultCollector result = nativeExecution.execute(functionId);
        return new GemFireResultCollector<>(result);
    }

    @Override
    public GudResultCollector<OUT, AGG> execute(GudFunction function) {
        if (function instanceof NativeWrapper) {
            ResultCollector result = nativeExecution.execute(((NativeWrapper<org.apache.geode.cache.execute.Function>) function).getNative());
            return new GemFireResultCollector<>(result);
        }
        throw new IllegalArgumentException("Function must be a native wrapper or use function ID");
    }
}
