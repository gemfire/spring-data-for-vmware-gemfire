/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.server.CacheServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Integration Tests for the use of the variable {@literal servers} attribute on &lt;gfe:pool/&lt; element
 * in SDG XML Namespace configuration metadata when connecting a client and server.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see ServerProcess
 * @see ForkingClientServerIntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.6.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheVariableServersIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static final Logger logger = LoggerFactory.getLogger(ClientCacheVariableServersIntegrationTests.class);

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		final int cacheServerPortOne = findAndReserveAvailablePort();
		final int cacheServerPortTwo = findAndReserveAvailablePort();

		System.setProperty("test.cache.server.port.one", String.valueOf(cacheServerPortOne));
		System.setProperty("test.cache.server.port.two", String.valueOf(cacheServerPortTwo));

		List<String> arguments = new ArrayList<>();

		arguments.add(String.format("-Dtest.cache.server.port.one=%d", cacheServerPortOne));
		arguments.add(String.format("-Dtest.cache.server.port.two=%d", cacheServerPortTwo));
		arguments.add(getServerContextXmlFileLocation(ClientCacheVariableServersIntegrationTests.class));

		startGemFireServer(ServerProcess.class, arguments.toArray(new String[0]));
	}

	@AfterClass
	public static void cleanup() {
		Arrays.asList("test.cache.server.port.one", "test.cache.server.port.two").forEach(System::clearProperty);
	}

	@Autowired
	private Pool serverPool;

	@Autowired
	@Qualifier("Example")
	private Region<String, Integer> example;

	@Before
	public void setup() {

		assertThat(this.serverPool).isNotNull();
		assertThat(this.serverPool.getName()).isEqualTo("serverPool");
		assertThat(this.serverPool.getServers()).hasSize(3);
		assertThat(this.example).isNotNull();
		assertThat(this.example.getName()).isEqualTo("Example");
		assertThat(this.example.getAttributes()).isNotNull();
		assertThat(this.example.getAttributes().getPoolName()).isEqualTo("serverPool");
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
		public Integer load(LoaderHelper<String, Integer> helper) throws CacheLoaderException {
			return cacheMissCounter.incrementAndGet();
		}

		@Override
		public void close() {
			cacheMissCounter.set(0);
		}
	}

	public static final class CacheServerConfigurationApplicationListener
			implements ApplicationListener<ContextRefreshedEvent> {

		@Override
		public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

			ApplicationContext applicationContext = contextRefreshedEvent.getApplicationContext();

			Map<String, CacheServer> cacheServers =
				CollectionUtils.nullSafeMap(applicationContext.getBeansOfType(CacheServer.class));

			assertThat(cacheServers).hasSize(3);

			cacheServers.values().forEach(cacheServer -> logger.info("CacheServer host:port [{}:{}]%n",
				cacheServer.getBindAddress(), cacheServer.getPort()));
		}
	}
}
