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

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.client.PoolManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the use of variable {@literal locators} attribute on &lt;gfe:pool/&lt; in SDG XML Namespace
 * configuration metadata when connecting a client and server.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.6.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheVariableLocatorsIntegrationTests {

	private static GemFireCluster gemFireCluster;
	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 3, 1);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=Example --type=PARTITION");

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));

		System.setProperty("spring.data.gemfire.locator.port.one", String.valueOf(gemFireCluster.getLocatorPorts().get(0)));
		System.setProperty("spring.data.gemfire.locator.port.two", String.valueOf(gemFireCluster.getLocatorPorts().get(1)));
		System.setProperty("spring.data.gemfire.locator.port.three", String.valueOf(gemFireCluster.getLocatorPorts().get(2)));

	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
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

		this.example.put("one" ,1);
		this.example.put("two" ,2);
		this.example.put("three" ,3);

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
}
