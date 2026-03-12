/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDiskStoreFactory interface as 1:1 mapping of GemFire DiskStoreFactory
 */

package org.springframework.data.gemfire.gud.api;

import java.io.File;

/**
 * GUD API abstraction for GemFire DiskStoreFactory interface.
 * Factory for creating DiskStore instances.
 */
public interface GudDiskStoreFactory {

    // Default values
    boolean DEFAULT_AUTO_COMPACT = true;
    float DEFAULT_DISK_USAGE_WARNING_PERCENTAGE = 90.0f;
    float DEFAULT_DISK_USAGE_CRITICAL_PERCENTAGE = 99.0f;
    int DEFAULT_COMPACTION_THRESHOLD = 50;
    boolean DEFAULT_ALLOW_FORCE_COMPACTION = false;
    long DEFAULT_MAX_OPLOG_SIZE = 1024L;
    long DEFAULT_TIME_INTERVAL = 1000L;
    int DEFAULT_WRITE_BUFFER_SIZE = 32768;
    int DEFAULT_QUEUE_SIZE = 0;

    GudDiskStoreFactory setAutoCompact(boolean autoCompact);
    GudDiskStoreFactory setDiskUsageWarningPercentage(float warningPercentage);
    GudDiskStoreFactory setDiskUsageCriticalPercentage(float criticalPercentage);
    GudDiskStoreFactory setCompactionThreshold(int compactionThreshold);
    GudDiskStoreFactory setAllowForceCompaction(boolean allowForceCompaction);
    GudDiskStoreFactory setMaxOplogSize(long maxOplogSize);
    GudDiskStoreFactory setTimeInterval(long timeInterval);
    GudDiskStoreFactory setWriteBufferSize(int writeBufferSize);
    GudDiskStoreFactory setQueueSize(int queueSize);
    GudDiskStoreFactory setDiskDirs(File[] diskDirs);
    GudDiskStoreFactory setDiskDirsAndSizes(File[] diskDirs, int[] diskDirSizes);

    GudDiskStore create(String name);
}
