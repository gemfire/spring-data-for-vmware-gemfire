/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.IndexFactoryBean;
import org.springframework.data.gemfire.IndexType;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.remote.RestHttpGemfireAdminTemplate;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for the {@link EnableClusterConfiguration} annotation
 * and {@link ClusterConfigurationConfiguration} class.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.context.annotation.Import
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.admin.GemfireAdminOperations
 * @see org.springframework.data.gemfire.config.annotation.ClusterConfigurationConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableClusterConfigurationIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class EnableClusterConfigurationIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	@Autowired
	private ClientCache gemfireCache;

	private GemfireAdminOperations adminOperations;

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGemFireServer() throws Exception {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withGfsh(false, "create region --name=RegionTwo --type=REPLICATE",
						"create region --name=RegionFour --type=PARTITION",
						"create index --name=IndexTwo --region=RegionTwo --type=HASH --expression=name");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.management.http.port", String.valueOf(gemFireCluster.getHttpPorts().get(0)));
	}

	@Before
	public void setup() {

		this.adminOperations = new RestHttpGemfireAdminTemplate.Builder()
			.with(this.gemfireCache)
			.on("localhost")
			.listenOn(gemFireCluster.getServerPorts().get(0))
			.build();
	}

	@Test
	public void serverIndexesAreCorrect() {
		assertThat(gemFireCluster.gfsh(false, "list indexes"))
			.contains("IndexOne", "IndexTwo");
	}

	@Test
	public void serverRegionsAreCorrect() {

		assertThat(gemFireCluster.gfsh(false, "list regions"))
			.contains("RegionOne", "RegionTwo", "RegionThree", "RegionFour");
	}

	@Configuration
	@EnableClusterConfiguration(useHttp = true, requireHttps = false)
	@Import(GeodeClientTestConfiguration.class)
	static class TestConfiguration { }

	@ClientCacheApplication(subscriptionEnabled = true)
	static class GeodeClientTestConfiguration {

		@Bean
		ClientCacheConfigurer clientCachePoolPortConfigurer() {

			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean
				.setServers(Collections.singletonList(new ConnectionEndpoint("localhost", gemFireCluster.getServerPorts().get(0))));
		}

		@Bean("IndexOne")
		@DependsOn("RegionOne")
		IndexFactoryBean indexOne(GemFireCache gemfireCache) {

			IndexFactoryBean indexFactory = new IndexFactoryBean();

			indexFactory.setCache(gemfireCache);
			indexFactory.setExpression("id");
			indexFactory.setFrom("/RegionOne");
			indexFactory.setType(IndexType.KEY);

			return indexFactory;
		}

		@Bean("RegionOne")
		ClientRegionFactoryBean<Object, Object> regionOne(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegionFactory = new ClientRegionFactoryBean<>();

			clientRegionFactory.setCache(gemfireCache);
			clientRegionFactory.setClose(false);
			clientRegionFactory.setShortcut(ClientRegionShortcut.PROXY);

			return clientRegionFactory;
		}

		@Bean("RegionTwo")
		ClientRegionFactoryBean<Object, Object> regionTwo(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegionFactory = new ClientRegionFactoryBean<>();

			clientRegionFactory.setCache(gemfireCache);
			clientRegionFactory.setClose(false);
			clientRegionFactory.setShortcut(ClientRegionShortcut.PROXY);

			return clientRegionFactory;
		}

		@Bean("RegionThree")
		ClientRegionFactoryBean<Object, Object> regionThree(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegionFactory = new ClientRegionFactoryBean<>();

			clientRegionFactory.setCache(gemfireCache);
			clientRegionFactory.setClose(false);
			clientRegionFactory.setShortcut(ClientRegionShortcut.CACHING_PROXY);

			return clientRegionFactory;
		}
	}
}
