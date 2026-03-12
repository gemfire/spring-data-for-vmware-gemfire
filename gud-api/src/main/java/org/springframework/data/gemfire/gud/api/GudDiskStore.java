/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDiskStore interface as 1:1 mapping of GemFire DiskStore
 */

package org.springframework.data.gemfire.gud.api;

import java.io.File;
import java.util.UUID;

/**
 * GUD API abstraction for GemFire DiskStore interface.
 * Represents a disk store for persistent data.
 */
public interface GudDiskStore {

    String getName();

    boolean getAutoCompact();

    float getDiskUsageWarningPercentage();

    float getDiskUsageCriticalPercentage();

    int getCompactionThreshold();

    boolean getAllowForceCompaction();

    long getMaxOplogSize();

    long getTimeInterval();

    int getWriteBufferSize();

    int getQueueSize();

    File[] getDiskDirs();

    int[] getDiskDirSizes();

    UUID getDiskStoreUUID();

    void forceRoll();

    boolean forceCompaction();

    void flush();

    void destroy();
}
