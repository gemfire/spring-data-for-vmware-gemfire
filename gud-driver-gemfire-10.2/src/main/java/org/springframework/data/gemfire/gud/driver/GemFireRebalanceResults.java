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
 * 2026-03-13: Created GemFire 10.2 RebalanceResults adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.geode.cache.control.RebalanceResults;

import org.springframework.data.gemfire.gud.api.GudPartitionRebalanceInfo;
import org.springframework.data.gemfire.gud.api.GudRebalanceResults;

/**
 * GUD API adapter for GemFire 10.2 RebalanceResults.
 */
public class GemFireRebalanceResults implements GudRebalanceResults, NativeWrapper<RebalanceResults> {

    private final RebalanceResults nativeResults;

    public GemFireRebalanceResults(RebalanceResults nativeResults) {
        this.nativeResults = nativeResults;
    }

    @Override
    public RebalanceResults getNative() {
        return nativeResults;
    }

    @Override
    public long getTotalBucketCreatesCompleted() {
        return nativeResults.getTotalBucketCreatesCompleted();
    }

    @Override
    public long getTotalBucketCreateBytes() {
        return nativeResults.getTotalBucketCreateBytes();
    }

    @Override
    public long getTotalBucketCreateTime() {
        return nativeResults.getTotalBucketCreateTime();
    }

    @Override
    public long getTotalBucketTransfersCompleted() {
        return nativeResults.getTotalBucketTransfersCompleted();
    }

    @Override
    public long getTotalBucketTransferBytes() {
        return nativeResults.getTotalBucketTransferBytes();
    }

    @Override
    public long getTotalBucketTransferTime() {
        return nativeResults.getTotalBucketTransferTime();
    }

    @Override
    public long getTotalPrimaryTransfersCompleted() {
        return nativeResults.getTotalPrimaryTransfersCompleted();
    }

    @Override
    public long getTotalPrimaryTransferTime() {
        return nativeResults.getTotalPrimaryTransferTime();
    }

    @Override
    public long getTotalTime() {
        return nativeResults.getTotalTime();
    }

    @Override
    public Set<GudPartitionRebalanceInfo> getPartitionRebalanceDetails() {
        return nativeResults.getPartitionRebalanceDetails().stream()
            .map(GemFirePartitionRebalanceInfo::new)
            .collect(Collectors.toSet());
    }
}
