/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDiskStoreFactory interface as 1:1 mapping of GemFire DiskStoreFactory
 * 2026-03-14: Added version-aware default method for setSegments (requires 10.1+)
 * 2026-03-17: Fixed setSegments version from 10.2 to 10.1
 * 2026-04-02: Updated default method to use GudCapability enum for type safety
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
    int DEFAULT_SEGMENTS = 1;
    int DEFAULT_DISK_DIR_SIZE = Integer.MAX_VALUE;

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

    /**
     * Sets the number of segments in the disk store.
     * <p>This feature was added in GemFire 10.1. Drivers for older versions
     * will throw {@link GudUnsupportedOperationException}.
     *
     * @param segments the number of segments
     * @return this factory
     * @throws GudUnsupportedOperationException if not supported by the driver
     * @since GemFire 10.1
     */
    default GudDiskStoreFactory setSegments(int segments) {
        throw new GudUnsupportedOperationException(
            "setSegments() is not supported. This feature was added in GemFire 10.1.",
            GudCapability.DISK_STORE_SEGMENTS, "10.1");
    }

    GudDiskStore create(String name);
}
