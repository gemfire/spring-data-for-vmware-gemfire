/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudRebalanceResults interface as 1:1 mapping of GemFire RebalanceResults
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Set;

/**
 * GUD API abstraction for GemFire RebalanceResults interface.
 * Results of a rebalance operation.
 */
public interface GudRebalanceResults {

    Set<GudPartitionRebalanceInfo> getPartitionRebalanceDetails();

    long getTotalBucketCreatesCompleted();
    long getTotalBucketCreateBytes();
    long getTotalBucketCreateTime();

    long getTotalBucketTransfersCompleted();
    long getTotalBucketTransferBytes();
    long getTotalBucketTransferTime();

    long getTotalPrimaryTransfersCompleted();
    long getTotalPrimaryTransferTime();

    long getTotalTime();
}
