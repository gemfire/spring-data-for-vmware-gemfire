/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.springframework.data.gemfire.config.annotation.CompressionConfiguration.SNAPPY_COMPRESSOR_BEAN_NAME;
import java.util.Arrays;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.compression.Compressor;
import org.apache.geode.compression.SnappyCompressor;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnableCompression} and {@link CompressionConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.CompressionConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableCompression
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
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

		Arrays.asList("People", "ExampleLocalRegion")
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

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableCompression
	@SuppressWarnings("unused")
	static class EnableCompressionForAllRegionsConfiguration {

		@Bean("ExampleLocalRegion")
		public ClientRegionFactoryBean<Object, Object> localRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> localRegion = new ClientRegionFactoryBean<>();

			localRegion.setCache(gemfireCache);
			localRegion.setPersistent(false);

			return localRegion;
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableCompression(compressorBeanName = "MockCompressor", regionNames = "People")
	@SuppressWarnings("unused")
	static class EnableCompressionForSelectRegionsConfiguration {

		@Bean("ExampleClientRegion")
		public ClientRegionFactoryBean<Object, Object> clientRegion(ClientCache gemfireCache) {

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
