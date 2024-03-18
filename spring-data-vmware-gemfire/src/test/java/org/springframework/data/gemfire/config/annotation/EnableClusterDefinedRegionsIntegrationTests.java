/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.util.CacheUtils;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link EnableClusterDefinedRegions} and {@link ClusterDefinedRegionsConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.config.annotation.ClusterDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterDefinedRegions
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableClusterDefinedRegionsIntegrationTests.GeodeClientTestConfiguration.class)
@SuppressWarnings("unused")
public class EnableClusterDefinedRegionsIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		gemFireCluster.gfsh(false, "create region --name=PartitionRegion --type=PARTITION",
				"create region --name=ReplicateRegion --type=REPLICATE", "create region --name=LocalRegion --type=LOCAL");

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Autowired
	private ClientCache cache;

	@Autowired
	@Qualifier("LocalRegion")
	private Region<?, ?> localClientProxyRegion;

	@Autowired
	@Qualifier("PartitionRegion")
	private Region<?, ?> partitionClientProxyRegion;

	@Autowired
	@Qualifier("ReplicateRegion")
	private Region<?, ?> replicateClientProxyRegion;

	private void assertRegion(Region<?, ?> region, String expectedName) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(expectedName);
		assertThat(region.getFullPath()).isEqualTo(RegionUtils.toRegionPath(expectedName));
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
	}

	@Before
	public void setup() {

		assertThat(this.cache).isNotNull();
		assertThat(CacheUtils.isClient(this.cache));
	}

	@Test
	public void clusterRegionsExistOnClient() {

		assertRegion(this.localClientProxyRegion, "LocalRegion");
		assertRegion(this.partitionClientProxyRegion, "PartitionRegion");
		assertRegion(this.replicateClientProxyRegion, "ReplicateRegion");
	}

	@ClientCacheApplication
	@EnableClusterDefinedRegions
	static class GeodeClientTestConfiguration {

		@Bean
		static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
			return new PropertySourcesPlaceholderConfigurer();
		}

		@Bean
		ClientCacheConfigurer clientCachePoolPortConfigurer() {

			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
				Collections.singletonList(new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}

		@Bean("TestBean")
		Object testBean(@Qualifier("LocalRegion") Region<?, ?> localRegion) {
			return "TEST";
		}
	}
}
