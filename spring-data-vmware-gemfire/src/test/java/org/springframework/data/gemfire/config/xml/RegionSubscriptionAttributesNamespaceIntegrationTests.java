/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.SubscriptionAttributes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the contract and functionality of declaring and defining {@link SubscriptionAttributes}
 * for a {@link Region} in the SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see Region
 * @see SubscriptionAttributes
 * @see org.springframework.data.gemfire.SubscriptionAttributesFactoryBean
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.6.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionSubscriptionAttributesNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("NoSubscriptionRegion")
	private Region<?, ?> noSubscriptionRegion;

	@Autowired
	@Qualifier("AllSubscriptionRegion")
	private Region<?, ?> allSubscriptionRegion;

	@Autowired
	@Qualifier("CacheContentSubscriptionRegion")
	private Region<?, ?> cacheContentSubscriptionRegion;

	@Autowired
	@Qualifier("DefaultSubscriptionRegion")
	private Region<?, ?> defaultSubscriptionRegion;

	private void assertSubscription(Region<?, ?> region, String expectedRegionName,
			DataPolicy expectedDataPolicy, InterestPolicy expectedInterestedPolicy) {

		assertThat(region).describedAs(String.format("The '%1$s' Region was not properly configured an initialized", expectedRegionName)).isNotNull();
		assertThat(region.getName()).isEqualTo(expectedRegionName);
		assertThat(region.getAttributes()).isNotNull();
		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(expectedDataPolicy);
		assertThat(region.getAttributes().getSubscriptionAttributes()).isNotNull();
		assertThat(region.getAttributes().getSubscriptionAttributes().getInterestPolicy()).isEqualTo(expectedInterestedPolicy);
	}

	@Test
	public void testNoSubscriptionRegion() {
		assertSubscription(noSubscriptionRegion, "NoSubscriptionRegion", DataPolicy.REPLICATE, InterestPolicy.DEFAULT);
	}

	@Test
	public void testAllSubscriptionRegion() {
		assertSubscription(allSubscriptionRegion, "AllSubscriptionRegion", DataPolicy.REPLICATE, InterestPolicy.ALL);
	}

	@Test
	public void testCacheContentSubscriptionRegion() {
		assertSubscription(cacheContentSubscriptionRegion, "CacheContentSubscriptionRegion", DataPolicy.PARTITION,
			InterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void testDefaultSubscriptionRegion() {
		assertSubscription(defaultSubscriptionRegion, "DefaultSubscriptionRegion", DataPolicy.PARTITION,
			InterestPolicy.DEFAULT);
	}
}
