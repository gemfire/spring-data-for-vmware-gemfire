/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPartitionListener interface as 1:1 mapping of GemFire PartitionListener
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire PartitionListener interface.
 * Listener for partition events.
 */
public interface GudPartitionListener {

    void afterPrimary(int bucketId);

    void afterRegionCreate(GudRegion<?, ?> region);

    void afterBucketRemoved(int bucketId, Iterable<?> keys);

    void afterBucketCreated(int bucketId, Iterable<?> keys);
}
