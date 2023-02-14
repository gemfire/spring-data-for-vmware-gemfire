/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.File;
import java.util.UUID;

import org.apache.geode.cache.DiskStore;

/**
 * The {@link DiskStoreMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire {@link DiskStore} objects and related objects in the {@literal org.apache.geode.cache} package.
 *
 * @author John Blum
 * @see DiskStore
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class DiskStoreMockObjects extends MockObjectsSupport {

	public static DiskStore mockDiskStore(String name, boolean allowForceCompaction, boolean autoCompact,
			int compactionThreshold, File[] diskDirectories, int[] diskDirectorySizes, float diskUsageCriticalPercentage,
			float diskUsageWarningPercentage, long maxOplogSize, int queueSize, long timeInterval, int writeBufferSize) {

		DiskStore mockDiskStore = mock(DiskStore.class, withSettings().name(name).lenient());

		when(mockDiskStore.getAllowForceCompaction()).thenReturn(allowForceCompaction);
		when(mockDiskStore.getAutoCompact()).thenReturn(autoCompact);
		when(mockDiskStore.getCompactionThreshold()).thenReturn(compactionThreshold);
		when(mockDiskStore.getDiskDirs()).thenReturn(diskDirectories);
		when(mockDiskStore.getDiskDirSizes()).thenReturn(diskDirectorySizes);
		when(mockDiskStore.getDiskStoreUUID()).thenReturn(UUID.randomUUID());
		when(mockDiskStore.getDiskUsageCriticalPercentage()).thenReturn(diskUsageCriticalPercentage);
		when(mockDiskStore.getDiskUsageWarningPercentage()).thenReturn(diskUsageWarningPercentage);
		when(mockDiskStore.getMaxOplogSize()).thenReturn(maxOplogSize);
		when(mockDiskStore.getName()).thenReturn(name);
		when(mockDiskStore.getQueueSize()).thenReturn(queueSize);
		when(mockDiskStore.getTimeInterval()).thenReturn(timeInterval);
		when(mockDiskStore.getWriteBufferSize()).thenReturn(writeBufferSize);

		return mockDiskStore;
	}
}
