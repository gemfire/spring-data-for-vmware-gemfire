/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.data.gemfire.config.annotation.CompressionConfiguration.SNAPPY_COMPRESSOR_BEAN_NAME;

import java.util.Arrays;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.compression.Compressor;
import org.apache.geode.compression.SnappyCompressor;

import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnableCompression} and {@link CompressionConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see GemFireCache
 * @see Region
 * @see CompressionConfiguration
 * @see EnableCompression
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @since 2.0.0
 */
public class EnableCompressionConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	private void assertRegionCompressor(Region<?, ?> region, String regionName, Compressor compressor) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(regionName);
		assertThat(region.getFullPath()).isEqualTo(GemfireUtils.toRegionPath(regionName));
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getCompressor()).isEqualTo(compressor);
	}

	@Test
	public void enableCompressionForAllRegions() {

		newApplicationContext(EnableCompressionForAllRegionsConfiguration.class);

		assertThat(containsBean("ExampleClientRegion")).isFalse();

		Compressor compressor = getBean(Compressor.class);

		assertThat(compressor).isInstanceOf(SnappyCompressor.class);

		Arrays.asList("People", "ExampleLocalRegion", "ExamplePartitionRegion", "ExampleReplicateRegion")
			.forEach(regionName -> {
				assertThat(containsBean(regionName)).isTrue();
				assertRegionCompressor(getBean(regionName, Region.class), regionName, compressor);
			});
	}

	@Test
	public void enableCompressionForSelectRegions() {

		newApplicationContext(EnableCompressionForSelectRegionsConfiguration.class);

		Compressor compressor = getBean("MockCompressor", Compressor.class);

		assertThat(compressor).isNotNull();
		assertThat(compressor).isNotInstanceOf(SnappyCompressor.class);
		assertThat(containsBean(SNAPPY_COMPRESSOR_BEAN_NAME)).isTrue();

		Arrays.asList("People", "ExampleClientRegion").forEach(regionName -> {
			assertThat(containsBean(regionName)).isTrue();
			assertRegionCompressor(getBean(regionName, Region.class), regionName,
				"People".equals(regionName) ? compressor : null);
		});
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableCompression
	@SuppressWarnings("unused")
	static class EnableCompressionForAllRegionsConfiguration {

		@Bean("ExampleLocalRegion")
		public LocalRegionFactoryBean<Object, Object> localRegion(GemFireCache gemfireCache) {

			LocalRegionFactoryBean<Object, Object> localRegion = new LocalRegionFactoryBean<>();

			localRegion.setCache(gemfireCache);
			localRegion.setPersistent(false);

			return localRegion;
		}

		@Bean("ExamplePartitionRegion")
		public PartitionedRegionFactoryBean<Object, Object> partitionRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<Object, Object> partitionRegion = new PartitionedRegionFactoryBean<>();

			partitionRegion.setCache(gemfireCache);
			partitionRegion.setPersistent(false);

			return partitionRegion;
		}

		@Bean("ExampleReplicateRegion")
		public ReplicatedRegionFactoryBean<Object, Object> replicateRegion(GemFireCache gemfireCache) {

			ReplicatedRegionFactoryBean<Object, Object> replicateRegion = new ReplicatedRegionFactoryBean<>();

			replicateRegion.setCache(gemfireCache);
			replicateRegion.setPersistent(false);

			return replicateRegion;
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableCompression(compressorBeanName = "MockCompressor", regionNames = "People")
	@SuppressWarnings("unused")
	static class EnableCompressionForSelectRegionsConfiguration {

		@Bean("ExampleClientRegion")
		public ClientRegionFactoryBean<Object, Object> clientRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(gemfireCache);
			clientRegion.setShortcut(ClientRegionShortcut.LOCAL);

			return clientRegion;
		}

		@Bean("MockCompressor")
		Compressor mockCompressor() {
			return mock(Compressor.class);
		}
	}
}
