/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.snapshot;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.snapshot.SnapshotServiceFactoryBean.SnapshotServiceAdapterSupport;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;

/**
 * Integration Tests with test cases testing the file archive handling capabilities of
 * the SnapshotServiceFactoryBean.SnapshotServiceAdpterSupport class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see SnapshotServiceAdapterSupport
 * @since 1.7.0
 */
@SuppressWarnings("rawtypes")
public class SnapshotServiceFactoryBeanIntegrationTests {

	private final SnapshotServiceAdapterSupport snapshotService = new TestSnapshotServiceAdapter();

	private List<String> toFilenames(File... files) {

		List<String> filenames = new ArrayList<>(files.length);

		for (File file : files) {
			filenames.add(file.getName());
		}

		return filenames;
	}

	@Test
	public void handleNonArchiveFileLocation() {

		File expectedFile = new File("/path/to/non-existing/snapshot/file.gfd");

		File[] files = snapshotService.handleFileLocation(expectedFile);

		assertThat(files).isNotNull();
		assertThat(files.length).isEqualTo(1);
		assertThat(files[0]).isEqualTo(expectedFile);
	}

	@Test
	public void handleArchiveFileLocation() throws Exception {

		File cacheSnapshotZipDirectory = null;

		try {
			File cacheSnapshotZip = new ClassPathResource("/cache_snapshot.zip").getFile();

			File[] actualSnapshots = snapshotService.handleFileLocation(cacheSnapshotZip);

			assertThat(actualSnapshots).isNotNull();
			assertThat(actualSnapshots.length).isEqualTo(3);
			assertThat(toFilenames(actualSnapshots).containsAll(Arrays.asList(
				"accounts.snapshot", "address.snapshot", "people.snapshot"))).isTrue();

			cacheSnapshotZipDirectory = new File(System.getProperty("java.io.tmpdir"),
				cacheSnapshotZip.getName().replaceAll("\\.", "-"));

			assertThat(cacheSnapshotZipDirectory.isDirectory()).isTrue();
			assertThat(cacheSnapshotZipDirectory.listFiles(FileSystemUtils.FileOnlyFilter.INSTANCE)).isEqualTo(actualSnapshots);
		}
		finally {
			if (cacheSnapshotZipDirectory != null && cacheSnapshotZipDirectory.isDirectory()) {
				FileSystemUtils.deleteRecursive(cacheSnapshotZipDirectory);
			}
		}
	}

	protected static final class TestSnapshotServiceAdapter<K, V> extends SnapshotServiceAdapterSupport<K, V> {

		@Override
		protected File[] handleLocation(final SnapshotServiceFactoryBean.SnapshotMetadata<K, V> configuration) {
			throw new UnsupportedOperationException("not implemented");
		}
	}
}
