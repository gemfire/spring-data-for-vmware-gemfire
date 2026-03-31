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
 * 2026-03-13: Created GemFire 10.3 RebalanceOperation adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.control.RebalanceOperation;

import org.springframework.data.gemfire.gud.api.GudRebalanceOperation;
import org.springframework.data.gemfire.gud.api.GudRebalanceResults;

/**
 * GUD API adapter for GemFire 10.3 RebalanceOperation.
 */
public class GemFireRebalanceOperation implements GudRebalanceOperation, NativeWrapper<RebalanceOperation> {

    private final RebalanceOperation nativeOperation;

    public GemFireRebalanceOperation(RebalanceOperation nativeOperation) {
        this.nativeOperation = nativeOperation;
    }

    @Override
    public RebalanceOperation getNative() {
        return nativeOperation;
    }

    @Override
    public boolean cancel() {
        return nativeOperation.cancel();
    }

    @Override
    public GudRebalanceResults getResults() throws CancellationException, InterruptedException {
        return new GemFireRebalanceResults(nativeOperation.getResults());
    }

    @Override
    public GudRebalanceResults getResults(long timeout, TimeUnit unit) throws InterruptedException {
        try {
            return new GemFireRebalanceResults(nativeOperation.getResults(timeout, unit));
        } catch (java.util.concurrent.TimeoutException e) {
            throw new RuntimeException("Rebalance timed out", e);
        }
    }

    @Override
    public boolean isCancelled() {
        return nativeOperation.isCancelled();
    }

    @Override
    public boolean isDone() {
        return nativeOperation.isDone();
    }
}
