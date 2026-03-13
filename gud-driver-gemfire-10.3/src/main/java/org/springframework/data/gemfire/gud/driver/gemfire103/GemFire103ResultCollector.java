/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 ResultCollector adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;

import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudFunctionException;
import org.springframework.data.gemfire.gud.api.GudResultCollector;

/**
 * GUD API adapter for GemFire 10.3 ResultCollector.
 *
 * @param <OUT> the type of the result item
 * @param <AGG> the type of the aggregated result
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class GemFire103ResultCollector<OUT, AGG> implements GudResultCollector<OUT, AGG>, NativeWrapper<ResultCollector> {

    private final ResultCollector nativeCollector;

    public GemFire103ResultCollector(ResultCollector nativeCollector) {
        this.nativeCollector = nativeCollector;
    }

    @Override
    public ResultCollector getNative() {
        return nativeCollector;
    }

    @Override
    public AGG getResult() throws GudFunctionException {
        try {
            return (AGG) nativeCollector.getResult();
        } catch (Exception e) {
            throw new GudFunctionException(e.getMessage(), e);
        }
    }

    @Override
    public AGG getResult(long timeout, TimeUnit unit) throws GudFunctionException, InterruptedException {
        try {
            return (AGG) nativeCollector.getResult(timeout, unit);
        } catch (InterruptedException e) {
            throw e;
        } catch (Exception e) {
            throw new GudFunctionException(e.getMessage(), e);
        }
    }

    @Override
    public void addResult(GudDistributedMember memberID, OUT resultOfSingleExecution) {
        if (memberID instanceof NativeWrapper) {
            DistributedMember nativeMember = ((NativeWrapper<DistributedMember>) memberID).getNative();
            nativeCollector.addResult(nativeMember, resultOfSingleExecution);
        }
    }

    @Override
    public void endResults() {
        nativeCollector.endResults();
    }

    @Override
    public void clearResults() {
        nativeCollector.clearResults();
    }
}
