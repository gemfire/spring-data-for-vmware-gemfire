/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.2 DiskStore adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire102;

import java.io.File;
import java.util.UUID;

import org.apache.geode.cache.DiskStore;

import org.springframework.data.gemfire.gud.api.GudDiskStore;

/**
 * GUD API adapter for GemFire 10.2 DiskStore.
 */
public class GemFire102DiskStore implements GudDiskStore, NativeWrapper<DiskStore> {

    private final DiskStore nativeDiskStore;

    public GemFire102DiskStore(DiskStore nativeDiskStore) {
        this.nativeDiskStore = nativeDiskStore;
    }

    @Override
    public DiskStore getNative() {
        return nativeDiskStore;
    }

    @Override
    public String getName() {
        return nativeDiskStore.getName();
    }

    @Override
    public boolean getAutoCompact() {
        return nativeDiskStore.getAutoCompact();
    }

    @Override
    public float getDiskUsageWarningPercentage() {
        return nativeDiskStore.getDiskUsageWarningPercentage();
    }

    @Override
    public float getDiskUsageCriticalPercentage() {
        return nativeDiskStore.getDiskUsageCriticalPercentage();
    }

    @Override
    public int getCompactionThreshold() {
        return nativeDiskStore.getCompactionThreshold();
    }

    @Override
    public boolean getAllowForceCompaction() {
        return nativeDiskStore.getAllowForceCompaction();
    }

    @Override
    public long getMaxOplogSize() {
        return nativeDiskStore.getMaxOplogSize();
    }

    @Override
    public long getTimeInterval() {
        return nativeDiskStore.getTimeInterval();
    }

    @Override
    public int getWriteBufferSize() {
        return nativeDiskStore.getWriteBufferSize();
    }

    @Override
    public int getQueueSize() {
        return nativeDiskStore.getQueueSize();
    }

    @Override
    public File[] getDiskDirs() {
        return nativeDiskStore.getDiskDirs();
    }

    @Override
    public int[] getDiskDirSizes() {
        return nativeDiskStore.getDiskDirSizes();
    }

    @Override
    public UUID getDiskStoreUUID() {
        return nativeDiskStore.getDiskStoreUUID();
    }

    @Override
    public void forceRoll() {
        nativeDiskStore.forceRoll();
    }

    @Override
    public boolean forceCompaction() {
        return nativeDiskStore.forceCompaction();
    }

    @Override
    public void flush() {
        nativeDiskStore.flush();
    }

    @Override
    public void destroy() {
        nativeDiskStore.destroy();
    }

    @Override
    public int[] getSegments() {
        // GemFire 10.2 may not have getSegments - return default
        return new int[]{1};
    }
}
