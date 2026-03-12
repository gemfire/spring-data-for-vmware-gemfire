/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudFixedPartitionAttributes interface as 1:1 mapping of GemFire FixedPartitionAttributes
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire FixedPartitionAttributes interface.
 * Defines fixed partition settings.
 */
public interface GudFixedPartitionAttributes {

    String getPartitionName();

    boolean isPrimary();

    int getNumBuckets();
}
