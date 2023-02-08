/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the use of variable {@literal locators} attribute on &lt;gfe:pool/&lt; in SDG XML Namespace
 * configuration metadata when connecting a client and server.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.fork.ServerProcess
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.6.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheVariableLocatorsIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		final int locatorPort = findAndReserveAvailablePort();

		System.setProperty("spring.data.gemfire.locator.port", String.valueOf(locatorPort));

		startGemFireServer(ServerProcess.class,
			getServerContextXmlFileLocation(ClientCacheVariableLocatorsIntegrationTests.class));
	}

	@Autowired
	@Qualifier("Example")
	private Region<String, Integer> example;

	@Before
	public void setup() {

		assertThat(this.example).isNotNull();
		assertThat(this.example.getName()).isEqualTo("Example");
		assertThat(this.example.getAttributes()).isNotNull();
		assertThat(this.example.getAttributes().getPoolName()).isEqualTo("locatorPool");

		Pool locatorPool = PoolManager.find("locatorPool");

		assertThat(locatorPool).isNotNull();
		assertThat(locatorPool.getName()).isEqualTo("locatorPool");
		assertThat(locatorPool.getLocators()).hasSize(3);
	}

	@Test
	public void clientServerConnectionSuccessful() {

		assertThat(example.get("one")).isEqualTo(1);
		assertThat(example.get("two")).isEqualTo(2);
		assertThat(example.get("three")).isEqualTo(3);
	}

	public static class CacheMissCounterCacheLoader implements CacheLoader<String, Integer> {

		private static final AtomicInteger cacheMissCounter = new AtomicInteger(0);

		@Override
		public Integer load(final LoaderHelper<String, Integer> helper) throws CacheLoaderException {
			return cacheMissCounter.incrementAndGet();
		}

		@Override
		public void close() {
			cacheMissCounter.set(0);
		}
	}
}
