/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
 * 2026-03-13: Created GemFire 10.1 DiskStoreFactory adapter
 * 2026-03-17: Added setSegments() override (supported in 10.1+)
 */

package org.springframework.data.gemfire.gud.driver;

import java.io.File;

import org.apache.geode.cache.DiskStoreFactory;

import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudDiskStoreFactory;

/**
 * GUD API adapter for GemFire 10.1 DiskStoreFactory.
 */
public class GemFireDiskStoreFactory implements GudDiskStoreFactory, NativeWrapper<DiskStoreFactory> {

    private final DiskStoreFactory nativeFactory;

    public GemFireDiskStoreFactory(DiskStoreFactory nativeFactory) {
        this.nativeFactory = nativeFactory;
    }

    @Override
    public DiskStoreFactory getNative() {
        return nativeFactory;
    }

    @Override
    public GudDiskStoreFactory setAutoCompact(boolean autoCompact) {
        nativeFactory.setAutoCompact(autoCompact);
        return this;
    }

    @Override
    public GudDiskStoreFactory setDiskUsageWarningPercentage(float warningPercentage) {
        nativeFactory.setDiskUsageWarningPercentage(warningPercentage);
        return this;
    }

    @Override
    public GudDiskStoreFactory setDiskUsageCriticalPercentage(float criticalPercentage) {
        nativeFactory.setDiskUsageCriticalPercentage(criticalPercentage);
        return this;
    }

    @Override
    public GudDiskStoreFactory setCompactionThreshold(int compactionThreshold) {
        nativeFactory.setCompactionThreshold(compactionThreshold);
        return this;
    }

    @Override
    public GudDiskStoreFactory setAllowForceCompaction(boolean allowForceCompaction) {
        nativeFactory.setAllowForceCompaction(allowForceCompaction);
        return this;
    }

    @Override
    public GudDiskStoreFactory setMaxOplogSize(long maxOplogSize) {
        nativeFactory.setMaxOplogSize(maxOplogSize);
        return this;
    }

    @Override
    public GudDiskStoreFactory setTimeInterval(long timeInterval) {
        nativeFactory.setTimeInterval(timeInterval);
        return this;
    }

    @Override
    public GudDiskStoreFactory setWriteBufferSize(int writeBufferSize) {
        nativeFactory.setWriteBufferSize(writeBufferSize);
        return this;
    }

    @Override
    public GudDiskStoreFactory setQueueSize(int queueSize) {
        nativeFactory.setQueueSize(queueSize);
        return this;
    }

    @Override
    public GudDiskStoreFactory setDiskDirs(File[] diskDirs) {
        nativeFactory.setDiskDirs(diskDirs);
        return this;
    }

    @Override
    public GudDiskStoreFactory setDiskDirsAndSizes(File[] diskDirs, int[] diskDirSizes) {
        nativeFactory.setDiskDirsAndSizes(diskDirs, diskDirSizes);
        return this;
    }

    @Override
    public GudDiskStoreFactory setSegments(int segments) {
        nativeFactory.setSegments(segments);
        return this;
    }

    @Override
    public GudDiskStore create(String name) {
        return new GemFireDiskStore(nativeFactory.create(name));
    }
}
