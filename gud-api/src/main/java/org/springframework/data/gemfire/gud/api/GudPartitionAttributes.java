/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPartitionAttributes interface as 1:1 mapping of GemFire PartitionAttributes
 */

package org.springframework.data.gemfire.gud.api;

import java.util.List;
import java.util.Properties;

/**
 * GUD API abstraction for GemFire PartitionAttributes interface.
 * Defines partitioning settings for partitioned regions.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public interface GudPartitionAttributes<K, V> {

    int getRedundantCopies();

    int getTotalNumBuckets();

    int getTotalMaxMemory();

    int getLocalMaxMemory();

    String getColocatedWith();

    long getRecoveryDelay();

    long getStartupRecoveryDelay();

    GudPartitionResolver<K, V> getPartitionResolver();

    List<GudPartitionListener> getPartitionListeners();

    GudFixedPartitionAttributes[] getFixedPartitionAttributes();
}
