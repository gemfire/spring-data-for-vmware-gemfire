/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import com.vmware.gemfire.testcontainers.GemFireCluster;
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
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.fork.ServerProcess
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.6.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheVariableServersIntegrationTests {

	private static final Logger logger = LoggerFactory.getLogger(ClientCacheVariableServersIntegrationTests.class);

	private static GemFireCluster gemFireCluster;
	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 3);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=Example --type=REPLICATE");

		System.setProperty("gemfire.locator.port",String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
		System.setProperty("test.cache.server.port.one", String.valueOf(gemFireCluster.getServerPorts().get(1)));
		System.setProperty("test.cache.server.port.two", String.valueOf(gemFireCluster.getServerPorts().get(2)));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
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

		example.put("one", 1);
		example.put("two", 2);
		example.put("three", 3);
	}

	@Test
	public void clientServerConnectionSuccessful() {

		assertThat(example.get("one")).isEqualTo(1);
		assertThat(example.get("two")).isEqualTo(2);
		assertThat(example.get("three")).isEqualTo(3);
	}
}
