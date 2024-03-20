/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.server.CacheServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link CacheServer} configuration using SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see CacheServer
 * @see org.springframework.data.gemfire.server.CacheServerFactoryBean
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration("cache-server-with-subscription-disk-store.xml")
@SuppressWarnings("unused")
public class CacheServerIntegrationTests extends IntegrationTestsSupport {

	private static File createFile(final String pathname) {
		return new File(pathname);
	}

	private static void deleteRecursive(final File path) {

		if (path.isDirectory()) {
			for (File file : path.listFiles()) {
				deleteRecursive(file);
			}
		}

		path.delete();
	}

	@Autowired
	private CacheServer cacheServer;

	@BeforeClass
	public static void setupBeforeClass() {
		assertThat(createDirectory(createFile("./gemfire/subscription-disk-store")).isDirectory()).isTrue();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		deleteRecursive(createFile("./gemfire"));
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testCacheServerRunningWithSubscription() {

		assertThat(cacheServer).isNotNull();
		assertThat(cacheServer.isRunning()).isTrue();
		assertThat(cacheServer.getBindAddress()).isEqualTo("localhost");
		assertThat(cacheServer.getGroups()).isNotNull();
		assertThat(cacheServer.getGroups().length).isEqualTo(1);
		assertThat(cacheServer.getGroups()[0]).isEqualTo("test-server");
		assertThat(cacheServer.getMaxConnections()).isEqualTo(1);
		assertThat(cacheServer.getClientSubscriptionConfig()).isNotNull();
		assertThat(cacheServer.getClientSubscriptionConfig().getCapacity()).isEqualTo(512);
		assertThat(cacheServer.getClientSubscriptionConfig().getDiskStoreName()).isEqualTo("testSubscriptionDiskStore");
		assertThat("ENTRY".equalsIgnoreCase(cacheServer.getClientSubscriptionConfig().getEvictionPolicy())).isTrue();
	}
}
