/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.control.ResourceManager;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Unit Tests for {@link EnableOffHeap} and {@link OffHeapConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.EnableOffHeap
 * @see org.springframework.data.gemfire.config.annotation.OffHeapConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.0.2
 */
public class EnableOffHeapConfigurationUnitTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	private void assertRegionOffHeap(Region<?, ?> region, String regionName, boolean offHeapEnabled) {

		assertThat(region).isNotNull();
		assertThat(region.getName()).isEqualTo(regionName);
		assertThat(region.getFullPath()).isEqualTo(GemfireUtils.toRegionPath(regionName));
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getOffHeap()).isEqualTo(offHeapEnabled);
	}

	@Test
	public void offHeapCriticalAndEvictionMemoryPercentagesConfiguredProperly() {

		newApplicationContext(OffHeapCriticalAndEvictionMemoryPercentagesConfiguration.class);

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).containsKey("off-heap-memory-size");
		assertThat(gemfireCache.getDistributedSystem().getProperties().getProperty("off-heap-memory-size"))
			.isEqualTo("1024g");

		ResourceManager resourceManager = gemfireCache.getResourceManager();

		assertThat(resourceManager).isNotNull();
		assertThat(resourceManager.getCriticalHeapPercentage()).isEqualTo(95.55f);
		assertThat(resourceManager.getCriticalOffHeapPercentage()).isEqualTo(90.5f);
		assertThat(resourceManager.getEvictionHeapPercentage()).isEqualTo(85.75f);
		assertThat(resourceManager.getEvictionOffHeapPercentage()).isEqualTo(75.25f);
	}

	@Test
	public void offHeapConfiguredForAllRegions() {

		newApplicationContext(EnableOffHeapForAllRegionsConfiguration.class);

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).containsKey("off-heap-memory-size");
		assertThat(gemfireCache.getDistributedSystem().getProperties().getProperty("off-heap-memory-size"))
			.isEqualTo("8192m");

		Arrays.asList("People", "ExampleLocalRegion", "ExamplePartitionRegion", "ExampleReplicateRegion")
			.forEach(regionName -> {
				assertThat(containsBean(regionName)).isTrue();
				assertRegionOffHeap(getBean(regionName, Region.class), regionName, true);
			});
	}

	@Test
	public void offHeapConfiguredForSelectRegions() {

		newApplicationContext(EnableOffHeapForSelectRegionsConfiguration.class);

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).containsKey("off-heap-memory-size");
		assertThat(gemfireCache.getDistributedSystem().getProperties().getProperty("off-heap-memory-size"))
			.isEqualTo("1024m");

		Arrays.asList("People", "ExampleLocalRegion", "ExamplePartitionRegion", "ExampleReplicateRegion")
			.forEach(regionName -> {
				assertThat(containsBean(regionName)).isTrue();
				assertRegionOffHeap(getBean(regionName, Region.class), regionName,
					Arrays.asList("People", "ExamplePartitionRegion").contains(regionName));
			});
	}

	@Configuration
	@SuppressWarnings("unused")
	static class TestRegionConfiguration {

		@Bean("ExampleLocalRegion")
		public LocalRegionFactoryBean<Object, Object> localRegion(GemFireCache gemfireCache) {

			LocalRegionFactoryBean<Object, Object> localRegion = new LocalRegionFactoryBean<>();

			localRegion.setCache(gemfireCache);
			localRegion.setClose(false);
			localRegion.setPersistent(false);

			return localRegion;
		}

		@Bean("ExamplePartitionRegion")
		public PartitionedRegionFactoryBean<Object, Object> partitionRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<Object, Object> partitionRegion = new PartitionedRegionFactoryBean<>();

			partitionRegion.setCache(gemfireCache);
			partitionRegion.setClose(false);
			partitionRegion.setPersistent(false);

			return partitionRegion;
		}

		@Bean("ExampleReplicateRegion")
		public ReplicatedRegionFactoryBean<Object, Object> replicateRegion(GemFireCache gemfireCache) {

			ReplicatedRegionFactoryBean<Object, Object> replicateRegion = new ReplicatedRegionFactoryBean<>();

			replicateRegion.setCache(gemfireCache);
			replicateRegion.setClose(false);
			replicateRegion.setPersistent(false);

			return replicateRegion;
		}
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableOffHeap(memorySize = "8192m")
	@Import(TestRegionConfiguration.class)
	static class EnableOffHeapForAllRegionsConfiguration { }

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableOffHeap(memorySize = "1024m", regionNames = { "People", "ExamplePartitionRegion" })
	@Import(TestRegionConfiguration.class)
	static class EnableOffHeapForSelectRegionsConfiguration { }

	@EnableGemFireMockObjects
	@PeerCacheApplication(
		criticalHeapPercentage = 95.55f,
		criticalOffHeapPercentage = 90.5f,
		evictionHeapPercentage = 85.75f,
		evictionOffHeapPercentage = 75.25f
	)
	@EnableOffHeap(memorySize = "1024g")
	static class OffHeapCriticalAndEvictionMemoryPercentagesConfiguration { }

}
