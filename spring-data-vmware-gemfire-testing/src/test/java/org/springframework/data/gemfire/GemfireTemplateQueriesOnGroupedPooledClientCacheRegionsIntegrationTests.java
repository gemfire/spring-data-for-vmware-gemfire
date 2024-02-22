/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.support.ConnectionEndpointList;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integrations Tests for {@link GemfireTemplate} testing the proper function and behavior of executing OQL queries
 * from a cache client application using the {@link GemfireTemplate} to a cluster of Apache Geode servers that have
 * been grouped according to business function and data access in order to distribute the load.
 *
 * Each Apache Geode {@link Pool} is configured to target a specific server group.  Each group of servers in the cluster
 * defines specific {@link Region Regions} to manage data independently and separately from other data that might garner
 * high frequency access.
 *
 * Spring Data for Apache Geode's {@link GemfireTemplate} should intelligently employ the right
 * {@link org.apache.geode.cache.query.QueryService} configured with the {@link Region Region's} {@link Pool}
 * metadata when executing the query in order to ensure the right servers containing the {@link Region Region's}
 * with the data of interest are targeted.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.GemfireTemplate
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see <a href="https://jira.spring.io/browse/SGF-555">Repository queries on client Regions associated with a Pool configured with a specified server group can lead to a RegionNotFoundException.</a>
 * @since 1.9.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes =
	GemfireTemplateQueriesOnGroupedPooledClientCacheRegionsIntegrationTests.GemFireClientCacheConfiguration.class)
@SuppressWarnings("unused")
public class GemfireTemplateQueriesOnGroupedPooledClientCacheRegionsIntegrationTests {

	@Autowired
	@Qualifier("catsTemplate")
	private GemfireTemplate catsTemplate;

	@Autowired
	@Qualifier("dogsTemplate")
	private GemfireTemplate dogsTemplate;

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 2)
				.withGemFireProperty("server-0", "groups", "serverOne")
				.withGemFireProperty("server-1", "groups", "serverTwo")
				.withGfsh(false,
						"create region --name=Cats --type=LOCAL --group=serverOne",
						"create region --name=Dogs --type=LOCAL --group=serverTwo");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Before
	public void setup() {
		saveCat("Grey");
		saveCat("Patchit");
		saveCat("Tyger");
		saveCat("Molly");
		saveCat("Sammy");

		saveDog("Spuds");
		saveDog("Maha");
	}

	private void saveCat(String cat) {
		catsTemplate.put(cat, cat);
	}

	private void saveDog(String dog) {
		dogsTemplate.put(dog, dog);
	}

	@Test
	public void findsAllCats() {

		List<String> catNames = catsTemplate.<String>find("SELECT * FROM /Cats").asList();

		assertThat(catNames).isNotNull();
		assertThat(catNames.size()).isEqualTo(5);
		assertThat(catNames).containsAll(Arrays.asList("Grey", "Patchit", "Tyger", "Molly", "Sammy"));
	}

	@Test
	public void findsAllDogs() {

		List<String> dogNames = dogsTemplate.<String>find("SELECT * FROM /Dogs").asList();

		assertThat(dogNames).isNotNull();
		assertThat(dogNames.size()).isEqualTo(2);
		assertThat(dogNames).containsAll(Arrays.asList("Spuds", "Maha"));
	}

	@Configuration
	static class GemFireClientCacheConfiguration {

		Properties gemfireProperties() {

			return PropertiesBuilder.create()
				.setProperty("name", applicationName())
				.setProperty("log-level", "error")
				.build();
		}

		String applicationName() {
			return GemfireTemplateQueriesOnGroupedPooledClientCacheRegionsIntegrationTests.class.getName();
		}

		@Bean
		ClientCacheFactoryBean gemfireCache() {

			ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();

			gemfireCache.setClose(true);
			gemfireCache.setPoolName("ServerOnePool");
			gemfireCache.setProperties(gemfireProperties());

			return gemfireCache;
		}

		@Bean(name = "ServerOnePool")
		PoolFactoryBean serverOnePool() {

			PoolFactoryBean serverOnePool = new PoolFactoryBean();

			serverOnePool.setMaxConnections(2);
			serverOnePool.setPingInterval(TimeUnit.SECONDS.toMillis(5));
			serverOnePool.setReadTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(30)).intValue());
			serverOnePool.setRetryAttempts(1);
			serverOnePool.setServerGroup("serverOne");
			serverOnePool.setLocators(ConnectionEndpointList.from(
					ConnectionEndpoint.from("localhost", gemFireCluster.getLocatorPort())));

			return serverOnePool;
		}

		@Bean(name = "ServerTwoPool")
		PoolFactoryBean serverTwoPool() {

			PoolFactoryBean serverOnePool = new PoolFactoryBean();

			serverOnePool.setMaxConnections(2);
			serverOnePool.setPingInterval(TimeUnit.SECONDS.toMillis(5));
			serverOnePool.setReadTimeout(Long.valueOf(TimeUnit.SECONDS.toMillis(30)).intValue());
			serverOnePool.setRetryAttempts(1);
			serverOnePool.setServerGroup("serverTwo");
			serverOnePool.setLocators(ConnectionEndpointList.from(
					ConnectionEndpoint.from("localhost", gemFireCluster.getLocatorPort())));

			return serverOnePool;
		}

		@Bean(name = "Cats")
		ClientRegionFactoryBean<String, String> catsRegion(GemFireCache gemfireCache,
				@Qualifier("ServerOnePool") Pool serverOnePool) {

			ClientRegionFactoryBean<String, String> catsRegion = new ClientRegionFactoryBean<>();

			catsRegion.setCache(gemfireCache);
			catsRegion.setPoolName(serverOnePool.getName());
			catsRegion.setShortcut(ClientRegionShortcut.PROXY);

			return catsRegion;
		}

		@Bean(name = "Dogs")
		ClientRegionFactoryBean<String, String> dogsRegion(GemFireCache gemfireCache,
				@Qualifier("ServerTwoPool") Pool serverTwoPool) {

			ClientRegionFactoryBean<String, String> dogsRegion = new ClientRegionFactoryBean<>();

			dogsRegion.setCache(gemfireCache);
			dogsRegion.setPoolName(serverTwoPool.getName());
			dogsRegion.setShortcut(ClientRegionShortcut.PROXY);

			return dogsRegion;
		}

		@Bean
		@DependsOn("Cats")
		GemfireTemplate catsTemplate(GemFireCache gemfireCache) {
			return new GemfireTemplate(gemfireCache.getRegion("Cats"));
		}

		@Bean
		@DependsOn("Dogs")
		GemfireTemplate dogsTemplate(GemFireCache gemfireCache) {
			return new GemfireTemplate(gemfireCache.getRegion("Dogs"));
		}
	}
}
