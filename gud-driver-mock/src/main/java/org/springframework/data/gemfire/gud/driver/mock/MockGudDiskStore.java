/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: In-memory mock GudDiskStore
 */

package org.springframework.data.gemfire.gud.driver.mock;

import java.io.File;
import java.util.UUID;

import org.springframework.data.gemfire.gud.api.GudDiskStore;

/**
 * In-memory mock {@link GudDiskStore}.  Stores all configuration set through the factory.
 * No disk I/O is performed.
 */
public class MockGudDiskStore implements GudDiskStore {

    private final String name;
    private final MockGudDiskStoreState state;
    private final UUID uuid = UUID.randomUUID();

    public MockGudDiskStore(String name, MockGudDiskStoreState state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getAutoCompact() {
        return state.autoCompact;
    }

    @Override
    public float getDiskUsageWarningPercentage() {
        return state.diskUsageWarningPercentage;
    }

    @Override
    public float getDiskUsageCriticalPercentage() {
        return state.diskUsageCriticalPercentage;
    }

    @Override
    public int getCompactionThreshold() {
        return state.compactionThreshold;
    }

    @Override
    public boolean getAllowForceCompaction() {
        return state.allowForceCompaction;
    }

    @Override
    public long getMaxOplogSize() {
        return state.maxOplogSize;
    }

    @Override
    public long getTimeInterval() {
        return state.timeInterval;
    }

    @Override
    public int getWriteBufferSize() {
        return state.writeBufferSize;
    }

    @Override
    public int getQueueSize() {
        return state.queueSize;
    }

    @Override
    public File[] getDiskDirs() {
        return state.diskDirs != null ? state.diskDirs.clone() : new File[0];
    }

    @Override
    public int[] getDiskDirSizes() {
        return state.diskDirSizes != null ? state.diskDirSizes.clone() : new int[0];
    }

    @Override
    public UUID getDiskStoreUUID() {
        return uuid;
    }

    @Override
    public void forceRoll() {
    }

    @Override
    public boolean forceCompaction() {
        return true;
    }

    @Override
    public void flush() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public int[] getSegments() {
        return new int[] { state.segments };
    }

    /** Accumulates disk store factory setter calls. */
    public static class MockGudDiskStoreState {
        boolean autoCompact = true;
        float diskUsageWarningPercentage = 90.0f;
        float diskUsageCriticalPercentage = 99.0f;
        int compactionThreshold = 50;
        boolean allowForceCompaction = false;
        long maxOplogSize = 1024L;
        long timeInterval = 1000L;
        int writeBufferSize = 32768;
        int queueSize = 0;
        File[] diskDirs;
        int[] diskDirSizes;
        int segments = 1;
    }
}
