/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-14: Created GudPartitionStatistics interface for partition region statistics (10.4+ feature)
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for partition region statistics.
 * Provides statistics about partitioned region data distribution.
 * 
 * <p>This interface is available starting with GemFire 10.4.
 * Calling methods on this interface with older drivers will throw
 * {@link GudUnsupportedOperationException}.
 * 
 * @since GUD API 1.1 (GemFire 10.4+)
 */
public interface GudPartitionStatistics {

    /**
     * Returns the number of buckets on this member.
     *
     * @return the bucket count
     */
    int getBucketCount();

    /**
     * Returns the total number of primary buckets on this member.
     *
     * @return the primary bucket count
     */
    int getPrimaryBucketCount();

    /**
     * Returns the configured total number of buckets for the region.
     *
     * @return the configured bucket count
     */
    int getConfiguredBucketCount();

    /**
     * Returns the configured redundant copies.
     *
     * @return the redundant copies count
     */
    int getConfiguredRedundantCopies();

    /**
     * Returns the actual redundant copies.
     *
     * @return the actual redundant copies count
     */
    int getActualRedundantCopies();

    /**
     * Returns the total number of bytes used by this partitioned region
     * on this member.
     *
     * @return the data store bytes used
     */
    long getDataStoreBytesUsed();

    /**
     * Returns the total number of entries in this partitioned region
     * on this member.
     *
     * @return the data store entry count
     */
    long getDataStoreEntryCount();
}
