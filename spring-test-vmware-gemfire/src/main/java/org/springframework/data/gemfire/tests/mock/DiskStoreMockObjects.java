/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */
package org.springframework.data.gemfire.tests.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import java.io.File;
import java.util.UUID;

import org.springframework.data.gemfire.gud.api.GudDiskStore;

/**
 * The {@link DiskStoreMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire {@link GudDiskStore} objects and related objects in the {@literal org.apache.geode.cache} package.
 *
 * @author John Blum
 * @see GudDiskStore
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class DiskStoreMockObjects extends MockObjectsSupport {

	public static GudDiskStore mockGudDiskStore(String name, boolean allowForceCompaction, boolean autoCompact,
			int compactionThreshold, File[] diskDirectories, int[] diskDirectorySizes, float diskUsageCriticalPercentage,
			float diskUsageWarningPercentage, long maxOplogSize, int queueSize, long timeInterval, int writeBufferSize, int segments) {

		GudDiskStore mockGudDiskStore = mock(GudDiskStore.class, withSettings().name(name).lenient());

		when(mockGudDiskStore.getAllowForceCompaction()).thenReturn(allowForceCompaction);
		when(mockGudDiskStore.getAutoCompact()).thenReturn(autoCompact);
		when(mockGudDiskStore.getCompactionThreshold()).thenReturn(compactionThreshold);
		when(mockGudDiskStore.getDiskDirs()).thenReturn(diskDirectories);
		when(mockGudDiskStore.getDiskDirSizes()).thenReturn(diskDirectorySizes);
		when(mockGudDiskStore.getDiskStoreUUID()).thenReturn(UUID.randomUUID());
		when(mockGudDiskStore.getDiskUsageCriticalPercentage()).thenReturn(diskUsageCriticalPercentage);
		when(mockGudDiskStore.getDiskUsageWarningPercentage()).thenReturn(diskUsageWarningPercentage);
		when(mockGudDiskStore.getMaxOplogSize()).thenReturn(maxOplogSize);
		when(mockGudDiskStore.getName()).thenReturn(name);
		when(mockGudDiskStore.getQueueSize()).thenReturn(queueSize);
		when(mockGudDiskStore.getTimeInterval()).thenReturn(timeInterval);
		when(mockGudDiskStore.getWriteBufferSize()).thenReturn(writeBufferSize);
		when(mockGudDiskStore.getSegments()).thenReturn(new int[] { segments });

		return mockGudDiskStore;
	}
}
