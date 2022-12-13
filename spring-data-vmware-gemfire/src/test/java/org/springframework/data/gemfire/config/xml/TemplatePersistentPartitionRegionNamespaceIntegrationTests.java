/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.EntryOperation;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.PartitionResolver;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests with test cases testing the functionality of {@link Region} templates
 * with a persistent, {@literal PARTITION} {@link Region} configuration.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Region
 * @see org.springframework.data.gemfire.PartitionedRegionFactoryBean
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @link https://jira.spring.io/browse/SGF-384
 * @since 1.6.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class TemplatePersistentPartitionRegionNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private DiskStore exampleDataStore;

	@Autowired
	@Qualifier("Example")
	private Region<?, ?> example;

	@After
	public void tearDown() {

		for (File diskDirectory : exampleDataStore.getDiskDirs()) {
			FileSystemUtils.deleteRecursive(FileSystemUtils.getRootRelativeToWorkingDirectoryOrPath(
				diskDirectory.getAbsoluteFile()));
		}
	}

	@Test
	public void testExampleTemplatedPersistentPartitionRegion() {

		assertThat(example).describedAs("The '/Example' PARTITION Region was not properly configured and initialized").isNotNull();
		assertThat(example.getName()).isEqualTo("Example");
		assertThat(example.getFullPath()).isEqualTo("/Example");
		assertThat(example.getAttributes()).isNotNull();
		assertThat(example.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_PARTITION);
		assertThat(example.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(example.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);
		assertThat(example.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(example.getAttributes().getPartitionAttributes()).isNotNull();
		assertThat(example.getAttributes().getPartitionAttributes().getRedundantCopies()).isEqualTo(1);
		assertThat(example.getAttributes().getPartitionAttributes().getTotalNumBuckets()).isEqualTo(163);
		assertThat(example.getAttributes().getPartitionAttributes().getPartitionResolver()).isNotNull();
		assertThat(example.getAttributes().getPartitionAttributes().getPartitionResolver().getName())
			.isEqualTo("TestPartitionResolver");
	}

	public static final class TestPartitionResolver<K, V> implements PartitionResolver<K, V> {

		private String name;

		@Override
		public Object getRoutingObject(final EntryOperation<K, V> kvEntryOperation) {
			throw new UnsupportedOperationException("Not Implemented");
		}

		@Override
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public void close() {
		}
	}
}
