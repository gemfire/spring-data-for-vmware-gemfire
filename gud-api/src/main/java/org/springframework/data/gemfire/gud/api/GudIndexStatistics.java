/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudIndexStatistics interface as 1:1 mapping of GemFire IndexStatistics
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire IndexStatistics interface.
 * Statistics for an index.
 */
public interface GudIndexStatistics {

    long getNumberOfBucketIndexes();
    long getNumberOfMapIndexKeys();
    long getNumberOfKeys();
    long getNumberOfValues();
    long getNumUpdates();
    long getTotalUpdateTime();
    long getTotalUses();
    long getReadLockCount();
}
