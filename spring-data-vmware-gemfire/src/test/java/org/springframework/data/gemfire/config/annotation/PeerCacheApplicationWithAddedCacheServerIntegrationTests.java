/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.server.CacheServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests to test and assert the property configuration of a {@link PeerCacheApplication} along with
 * added {@link CacheServer CacheServers} using the {@link EnableCacheServer} annotation.
 *
 * @author John Blum
 * @see Test
 * @see Cache
 * @see CacheServer
 * @see org.apache.geode.distributed.Locator
 * @see ForkingClientServerIntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.2.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PeerCacheApplicationWithAddedCacheServerIntegrationTests.TestPeerCacheConfiguration.class)
@SuppressWarnings("unused")
public class PeerCacheApplicationWithAddedCacheServerIntegrationTests
		extends ForkingClientServerIntegrationTestsSupport {

	private static int cacheServerPort;
	private static int locatorPort;

	private static ProcessWrapper gemfireLocator;

	@BeforeClass
	public static void startGemFireLocator() throws Exception {

		locatorPort = findAvailablePort();

		gemfireLocator = run(TestLocatorConfiguration.class,
"-Dspring.data.gemfire.locator.port=" + locatorPort,
			String.format("-D%1$s%2$s=%3$s", GemFireProperties.GEMFIRE_PROPERTY_NAME_PREFIX,
				GemFireProperties.ENABLE_CLUSTER_CONFIGURATION.getName(), false));

		waitForServerToStart("localhost", locatorPort);

		cacheServerPort = findAvailablePort();

		System.setProperty("spring.data.gemfire.cache.peer.locators", String.format("localhost[%d]", locatorPort));
		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(cacheServerPort));
	}

	@AfterClass
	public static void stopGemFireLocator() {
		stop(gemfireLocator);
	}

	@Autowired
	private Cache cache;

	@Before
	public void setup() {

		assertThat(this.cache).isNotNull();
		assertThat(this.cache.getName()).isEqualTo("PeerCacheApplicationWithAddedCacheServerIntegrationTests");
		assertThat(this.cache.getDistributedSystem()).isNotNull();
		assertThat(this.cache.getDistributedSystem().getProperties()).isNotNull();
		assertThat(this.cache.getDistributedSystem().getProperties().getProperty(GemFireProperties.LOCATORS.getName()))
			.isEqualTo(String.format("localhost[%d]", locatorPort));
		assertThat(this.cache.getDistributedSystem().getProperties().getProperty(GemFireProperties.NAME.getName()))
			.isEqualTo("PeerCacheApplicationWithAddedCacheServerIntegrationTests");
	}

	@Test
	public void cacheServerWasConfiguredCorrectly() {

		List<CacheServer> cacheServers = this.cache.getCacheServers();

		assertThat(cacheServers).isNotNull();
		assertThat(cacheServers).hasSize(1);

		CacheServer cacheServer = cacheServers.get(0);

		assertThat(cacheServer).isNotNull();
		assertThat(cacheServer.getPort()).isEqualTo(cacheServerPort);
	}

	@LocatorApplication
	static class TestLocatorConfiguration {

		public static void main(String[] args) {
			runSpringApplication(TestLocatorConfiguration.class, args);
			block();
		}
	}

	@EnableCacheServer
	@PeerCacheApplication(name = "PeerCacheApplicationWithAddedCacheServerIntegrationTests")
	static class TestPeerCacheConfiguration { }

}
