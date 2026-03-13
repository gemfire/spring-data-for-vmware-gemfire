/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 PartitionRebalanceInfo adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.partition.PartitionRebalanceInfo;

import org.springframework.data.gemfire.gud.api.GudPartitionRebalanceInfo;

/**
 * GUD API adapter for GemFire 10.3 PartitionRebalanceInfo.
 */
public class GemFire103PartitionRebalanceInfo implements GudPartitionRebalanceInfo, NativeWrapper<PartitionRebalanceInfo> {

    private final PartitionRebalanceInfo nativeInfo;

    public GemFire103PartitionRebalanceInfo(PartitionRebalanceInfo nativeInfo) {
        this.nativeInfo = nativeInfo;
    }

    @Override
    public PartitionRebalanceInfo getNative() {
        return nativeInfo;
    }

    @Override
    public String getRegionPath() {
        return nativeInfo.getRegionPath();
    }

    @Override
    public long getBucketCreatesCompleted() {
        return nativeInfo.getBucketCreatesCompleted();
    }

    @Override
    public long getBucketCreateBytes() {
        return nativeInfo.getBucketCreateBytes();
    }

    @Override
    public long getBucketCreateTime() {
        return nativeInfo.getBucketCreateTime();
    }

    @Override
    public long getBucketTransfersCompleted() {
        return nativeInfo.getBucketTransfersCompleted();
    }

    @Override
    public long getBucketTransferBytes() {
        return nativeInfo.getBucketTransferBytes();
    }

    @Override
    public long getBucketTransferTime() {
        return nativeInfo.getBucketTransferTime();
    }

    @Override
    public long getPrimaryTransfersCompleted() {
        return nativeInfo.getPrimaryTransfersCompleted();
    }

    @Override
    public long getPrimaryTransferTime() {
        return nativeInfo.getPrimaryTransferTime();
    }
}
