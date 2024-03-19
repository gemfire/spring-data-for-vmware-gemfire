/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;
import org.mockito.stubbing.Answer;

import org.apache.geode.cache.DiskStore;

import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Unit Tests for the {@link EnableDiskStore} and {@link EnableDiskStores} annotations as well as
 * the {@link DiskStoreConfiguration} and {@link DiskStoresConfiguration} classes.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.DiskStore
 * @see org.springframework.data.gemfire.config.annotation.EnableDiskStore
 * @see org.springframework.data.gemfire.config.annotation.EnableDiskStores
 * @see org.springframework.data.gemfire.config.annotation.DiskStoreConfiguration
 * @see org.springframework.data.gemfire.config.annotation.DiskStoresConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
@SuppressWarnings("unused")
public class EnableDiskStoresConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	private static final AtomicInteger MOCK_ID = new AtomicInteger(0);

	private void assertDiskStore(DiskStore diskStore, String name, boolean allowForceCompaction, boolean autoCompact,
			int compactionThreshold, float diskUsageCriticalPercentage, float diskUsageWarningPercentage,
			long maxOplogSize, int queueSize, long timeInterval, int writeBufferSize) {

		assertThat(diskStore).isNotNull();
		assertThat(diskStore.getName()).isEqualTo(name);
		assertThat(diskStore.getAllowForceCompaction()).isEqualTo(allowForceCompaction);
		assertThat(diskStore.getAutoCompact()).isEqualTo(autoCompact);
		assertThat(diskStore.getCompactionThreshold()).isEqualTo(compactionThreshold);
		assertThat(diskStore.getDiskUsageCriticalPercentage()).isEqualTo(diskUsageCriticalPercentage);
		assertThat(diskStore.getDiskUsageWarningPercentage()).isEqualTo(diskUsageWarningPercentage);
		assertThat(diskStore.getMaxOplogSize()).isEqualTo(maxOplogSize);
		assertThat(diskStore.getQueueSize()).isEqualTo(queueSize);
		assertThat(diskStore.getTimeInterval()).isEqualTo(timeInterval);
		assertThat(diskStore.getWriteBufferSize()).isEqualTo(writeBufferSize);
	}

	private void assertDiskStoreDirectoryLocations(DiskStore diskStore, File... diskDirectories) {

		assertThat(diskStore).isNotNull();

		File[] diskStoreDirectories = diskStore.getDiskDirs();

		assertThat(diskStoreDirectories).isNotNull();
		assertThat(diskStoreDirectories.length).isEqualTo(diskDirectories.length);

		int index = 0;

		for (File diskDirectory : diskDirectories) {
			assertThat(diskStoreDirectories[index++]).isEqualTo(diskDirectory);
		}
	}

	private void assertDiskStoreDirectorySizes(DiskStore diskStore, int... diskDirectorySizes) {

		assertThat(diskStore).isNotNull();

		int[] diskStoreDirectorySizes = diskStore.getDiskDirSizes();

		assertThat(diskStoreDirectorySizes).isNotNull();
		assertThat(diskStoreDirectorySizes.length).isEqualTo(diskDirectorySizes.length);

		int index = 0;

		for (int size : diskDirectorySizes) {
			assertThat(diskStoreDirectorySizes[index++]).isEqualTo(size);
		}
	}

	private File newFile(String location) {
		return new File(location);
	}

	@Test
	public void enableSingleDiskStore() {

		newApplicationContext(SingleDiskStoreConfiguration.class);

		DiskStore testDiskStore = getBean("TestDiskStore", DiskStore.class);

		assertDiskStore(testDiskStore, "TestDiskStore", true, true, 75, 95.0f, 75.0f, 8192L, 100, 2000L, 65536);
		assertDiskStoreDirectoryLocations(testDiskStore, newFile("/absolute/path/to/gemfire/disk/directory"),
			newFile("relative/path/to/gemfire/disk/directory"));
		assertDiskStoreDirectorySizes(testDiskStore, 1024, 4096);
	}

	@Test
	public void enableMultipleDiskStores() {

		newApplicationContext(MultipleDiskStoresConfiguration.class);

		DiskStore testDiskStoreOne = getBean("TestDiskStoreOne", DiskStore.class);

		assertDiskStore(testDiskStoreOne, "TestDiskStoreOne", false, true, 75, 99.0f, 90.0f, 2048L, 100, 1000L, 32768);

		DiskStore testDiskStoreTwo = getBean("TestDiskStoreTwo", DiskStore.class);

		assertDiskStore(testDiskStoreTwo, "TestDiskStoreTwo", true, true, 85, 99.0f, 90.0f, 4096L, 0, 1000L, 32768);
	}

	static String mockName(String baseName) {
		return String.format("%s%d", baseName, MOCK_ID.incrementAndGet());
	}

	protected static <R> Answer<R> newGetter(AtomicReference<R> returnValue) {
		return invocation -> returnValue.get();
	}

	protected static <T, R> Answer<R> newSetter(Class<T> parameterType, AtomicReference<T> argument, R returnValue) {

		return invocation -> {
			argument.set(invocation.getArgument(0));
			return returnValue;
		};
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableDiskStore(name = "TestDiskStore", allowForceCompaction = true, autoCompact = true, compactionThreshold = 75,
		diskUsageCriticalPercentage = 95.0f, diskUsageWarningPercentage = 75.0f, maxOplogSize = 8192L, queueSize = 100,
		timeInterval = 2000L, writeBufferSize = 65536, diskDirectories = {
			@EnableDiskStore.DiskDirectory(location = "/absolute/path/to/gemfire/disk/directory", maxSize = 1024),
			@EnableDiskStore.DiskDirectory(location = "relative/path/to/gemfire/disk/directory", maxSize = 4096)
	})
	static class SingleDiskStoreConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableDiskStores(autoCompact = true, compactionThreshold = 75, maxOplogSize = 2048L, diskStores = {
		@EnableDiskStore(name = "TestDiskStoreOne", queueSize = 100),
		@EnableDiskStore(name = "TestDiskStoreTwo", allowForceCompaction = true,
			compactionThreshold = 85, maxOplogSize = 4096)
	})
	static class MultipleDiskStoresConfiguration { }

}
