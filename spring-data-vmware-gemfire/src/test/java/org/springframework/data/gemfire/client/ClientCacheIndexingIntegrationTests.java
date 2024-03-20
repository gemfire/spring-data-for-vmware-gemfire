/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexType;
import org.apache.geode.cache.query.QueryService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Tests for testing {@link ClientCache} {@link Index Indexes}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.query.Index
 * @see org.apache.geode.cache.query.QueryService
 * @see org.springframework.data.gemfire.IndexFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.5.2
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheIndexingIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@Autowired
	private ClientCache clientCache;

	@Autowired
	private Index exampleIndex;

	@BeforeClass
	public static void startGeodeServer() throws IOException {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port",String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	private Index getIndex(GemFireCache gemfireCache, String indexName) {

		QueryService queryService = gemfireCache instanceof ClientCache
			? ((ClientCache) gemfireCache).getLocalQueryService()
			: gemfireCache.getQueryService();

		for (Index index : queryService.getIndexes()) {
			if (index.getName().equals(indexName)) {
				return index;
			}
		}

		return null;
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testIndexByName() {

		assertThat(clientCache)
			.describedAs("The GemFire ClientCache was not properly configured and initialized")
			.isNotNull();

		Index actualIndex = getIndex(clientCache, "ExampleIndex");

		assertThat(actualIndex).isNotNull();
		assertThat(actualIndex.getName()).isEqualTo("ExampleIndex");
		assertThat(actualIndex.getType()).isEqualTo(IndexType.HASH);
		assertThat(actualIndex).isSameAs(exampleIndex);
	}
}
