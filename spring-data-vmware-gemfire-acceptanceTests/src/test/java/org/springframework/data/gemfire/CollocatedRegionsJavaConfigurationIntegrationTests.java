/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode {@literal collocated} {@link DataPolicy#PARTITION} {@link Region Regions}
 * using SDG Java configuration.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.PartitionedRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.PeerCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@GemFireUnitTest
@SuppressWarnings("unused")
public class CollocatedRegionsJavaConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("A")
	private Region<?, ?> a;

	@Autowired
	@Qualifier("B")
	private Region<?, ?> b;

	private void assertRegion(Region<?, ?> region, String name, int redundantCopies) {
		assertRegion(region, name, null, redundantCopies);
	}

	private void assertRegion(Region<?, ?> region, String name, String collocatedWith, int redundantCopies) {

		Assertions.assertThat(region).isNotNull();
		Assertions.assertThat(region.getName()).isEqualTo(name);
		Assertions.assertThat(region.getFullPath()).isEqualTo(RegionUtils.toRegionPath(name));
		Assertions.assertThat(region.getAttributes()).isNotNull();
		Assertions.assertThat(region.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		Assertions.assertThat(region.getAttributes().getPartitionAttributes()).isNotNull();
		Assertions.assertThat(region.getAttributes().getPartitionAttributes().getColocatedWith()).isEqualTo(collocatedWith);
		Assertions.assertThat(region.getAttributes().getPartitionAttributes().getRedundantCopies()).isEqualTo(redundantCopies);
	}

	@Test
	public void collocatedPartitionRegionConfigurationIsCorrect() {

		assertRegion(this.a, "A", 0);
		assertRegion(this.b, "B", "A", 0);
	}

	@PeerCacheApplication
	static class TestConfiguration {

		@Bean("B")
		@DependsOn("A")
		PartitionedRegionFactoryBean<Object, Object> partTwoRegion(GemFireCache cache,
				@Qualifier("partTwoRegionAttributes") RegionAttributes<Object, Object> partTwoRegionAttributes) {

			PartitionedRegionFactoryBean<Object, Object> partTwoRegion = new PartitionedRegionFactoryBean<>();

			partTwoRegion.setCache(cache);
			partTwoRegion.setPersistent(false);
			partTwoRegion.setAttributes(partTwoRegionAttributes);

			return partTwoRegion;
		}

		@Bean
		RegionAttributesFactoryBean<Object, Object> partTwoRegionAttributes(
				@Qualifier("partTwoPartitionAttributes") PartitionAttributes<Object, Object> partTwoPartitionAttributes) {

			RegionAttributesFactoryBean<Object, Object> partTwoRegionAttributes = new RegionAttributesFactoryBean<>();

			partTwoRegionAttributes.setPartitionAttributes(partTwoPartitionAttributes);

			return partTwoRegionAttributes;
		}

		@Bean
		PartitionAttributesFactoryBean<Object, Object> partTwoPartitionAttributes() {

			PartitionAttributesFactoryBean<Object, Object> partTwoPartitionAttributes =
				new PartitionAttributesFactoryBean<>();

			partTwoPartitionAttributes.setColocatedWith("A");

			return partTwoPartitionAttributes;
		}

		@Bean("A")
		public PartitionedRegionFactoryBean<Object, Object> partOneRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<Object, Object> partOneRegion = new PartitionedRegionFactoryBean<>();

			partOneRegion.setCache(gemfireCache);
			partOneRegion.setPersistent(false);

			return partOneRegion;
		}
	}
}
