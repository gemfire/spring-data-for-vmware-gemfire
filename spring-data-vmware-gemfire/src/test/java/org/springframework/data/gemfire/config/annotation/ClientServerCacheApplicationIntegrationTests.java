/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Collections;

/**
 * Integration Tests testing the contract and functionality of the {@link CacheServerApplication}
 * and {@link ClientCacheApplication} SDG annotations for configuring and bootstrapping an Apache Geode
 * client/server topology
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.server.CacheServer
 * @see org.springframework.data.gemfire.config.annotation.CacheServerApplication
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.9.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
public class ClientServerCacheApplicationIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=Echo --type=PARTITION");
		gemFireCluster.gfsh(false, "put --region=/Echo --key=Hello --value=Hello");
		gemFireCluster.gfsh(false, "put --region=/Echo --key=Test --value=Test");
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Autowired
	private ClientCache clientCache;

	@Autowired
	@Qualifier("Echo")
	private Region<String, String> echo;

	@Test
	public void echoClientProxyRegionEchoesKeysForValues() {
		assertThat(echo.get("Hello")).isEqualTo("Hello");
		assertThat(echo.get("Test")).isEqualTo("Test");
	}

	@ClientCacheApplication
	static class GeodeClientTestConfiguration {

		@Bean(name = "Echo")
		ClientRegionFactoryBean<String, String> echoRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<String, String> echoRegion = new ClientRegionFactoryBean<String, String>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setClose(false);
			echoRegion.setShortcut(ClientRegionShortcut.PROXY);

			return echoRegion;
		}

		@Bean
		ClientCacheConfigurer clientCacheConfigurer() {
			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
					Collections.singletonList(
							new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}
	}
}
