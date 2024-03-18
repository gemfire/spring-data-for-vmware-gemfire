/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import java.io.IOException;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vmware.gemfire.testcontainers.GemFireCluster;

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

		Assertions.assertThat(this.serverPool).isNotNull();
		Assertions.assertThat(this.serverPool.getName()).isEqualTo("serverPool");
		Assertions.assertThat(this.serverPool.getServers()).hasSize(3);
		Assertions.assertThat(this.example).isNotNull();
		Assertions.assertThat(this.example.getName()).isEqualTo("Example");
		Assertions.assertThat(this.example.getAttributes()).isNotNull();
		Assertions.assertThat(this.example.getAttributes().getPoolName()).isEqualTo("serverPool");

		example.put("one", 1);
		example.put("two", 2);
		example.put("three", 3);
	}

	@Test
	public void clientServerConnectionSuccessful() {

		Assertions.assertThat(example.get("one")).isEqualTo(1);
		Assertions.assertThat(example.get("two")).isEqualTo(2);
		Assertions.assertThat(example.get("three")).isEqualTo(3);
	}
}
