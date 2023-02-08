/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.DiskStoreFactoryBean;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for {@link DiskStoreDirectoryBeanPostProcessor}.
 *
 * @author John Blum
 * @see Test
 * @see org.apache.geode.cache.DiskStore
 * @see DiskStoreFactoryBean
 * @see DiskStoreDirectoryBeanPostProcessor
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
public class DiskStoreDirectoryBeanPostProcessorIntegrationTests extends IntegrationTestsSupport {

	@BeforeClass
	public static void testSuiteSetup() {
		assertThat(new File("./gemfire/disk-stores/ds2/local").mkdirs()).isTrue();
	}

	@AfterClass
	public static void testSuiteTearDown() {
		assertThat(FileSystemUtils.deleteRecursive(new File("./gemfire"))).isTrue();
		assertThat(FileSystemUtils.deleteRecursive(new File("./gfe"))).isTrue();
	}

	@Test
	public void diskStoreDirectoriesExist() {
		assertThat(new File("./gemfire/disk-stores/ds1").isDirectory()).isTrue();
		assertThat(new File("./gemfire/disk-stores/ds2/local").isDirectory()).isTrue();
		assertThat(new File("./gemfire/disk-stores/ds2/remote").isDirectory()).isTrue();
		assertThat(new File("./gfe/ds/store3/local").isDirectory()).isTrue();
	}

	@PeerCacheApplication(logLevel = "error")
	@SuppressWarnings("unused")
	static class DiskStoreDirectoryBeanPostProcessorConfiguration {

		DiskStoreFactoryBean.DiskDir newDiskDir(String location) {
			return new DiskStoreFactoryBean.DiskDir(location);
		}

		@Bean
		DiskStoreFactoryBean diskStoreOne(GemFireCache gemfireCache) {
			DiskStoreFactoryBean diskStoreOne = new DiskStoreFactoryBean();
			diskStoreOne.setCache(gemfireCache);
			diskStoreOne.setDiskDirs(Collections.singletonList(newDiskDir("./gemfire/disk-stores/ds1")));
			return diskStoreOne;
		}

		@Bean
		DiskStoreFactoryBean diskStoreTwo(GemFireCache gemfireCache) {
			DiskStoreFactoryBean diskStoreTwo = new DiskStoreFactoryBean();
			diskStoreTwo.setCache(gemfireCache);
			diskStoreTwo.setDiskDirs(Arrays.asList(newDiskDir("./gemfire/disk-stores/ds2/local"),
				newDiskDir("./gemfire/disk-stores/ds2/remote")));
			return diskStoreTwo;
		}

		@Bean
		DiskStoreFactoryBean diskStoreThree(GemFireCache gemfireCache) {
			DiskStoreFactoryBean diskStoreThree = new DiskStoreFactoryBean();
			diskStoreThree.setCache(gemfireCache);
			diskStoreThree.setDiskDirs(Collections.singletonList(newDiskDir("./gfe/ds/store3/local")));
			return diskStoreThree;
		}
	}
}
