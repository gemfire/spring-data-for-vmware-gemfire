/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPartitionRebalanceInfo interface
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for partition rebalance info.
 */
public interface GudPartitionRebalanceInfo {

    String getRegionPath();

    long getBucketCreatesCompleted();
    long getBucketCreateBytes();
    long getBucketCreateTime();

    long getBucketTransfersCompleted();
    long getBucketTransferBytes();
    long getBucketTransferTime();

    long getPrimaryTransfersCompleted();
    long getPrimaryTransferTime();
}
