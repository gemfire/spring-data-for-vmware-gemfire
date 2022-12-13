/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.SubscriptionAttributes;

import org.springframework.data.gemfire.PeerRegionFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests ensuring subscription policy can be applied to server {@link Region Regions}.
 *
 * @author Lyndon Adams
 * @author John Blum
 * @see org.junit.Test
 * @see Region
 * @see SubscriptionAttributes
 * @see org.springframework.data.gemfire.SubscriptionAttributesFactoryBean
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.3.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings({ "rawtypes", "unused" })
public class CacheSubscriptionNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void replicateRegionSubscriptionAllPolicy() {

		assertThat(requireApplicationContext().containsBean("replicALL")).isTrue();

		PeerRegionFactoryBean regionFactoryBean =
			requireApplicationContext().getBean("&replicALL", PeerRegionFactoryBean.class);

		RegionAttributes regionAttributes = regionFactoryBean.getAttributes();

		assertThat(regionAttributes).isNotNull();

		SubscriptionAttributes subscriptionAttributes = regionAttributes.getSubscriptionAttributes();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.ALL);
	}

	@Test
	public void partitionRegionSubscriptionCacheContentPolicy() {

		assertThat(requireApplicationContext().containsBean("partCACHE_CONTENT")).isTrue();

		PeerRegionFactoryBean regionFactoryBean =
			requireApplicationContext().getBean("&partCACHE_CONTENT", PeerRegionFactoryBean.class);

		RegionAttributes regionAttributes = regionFactoryBean.getAttributes();

		assertThat(regionAttributes).isNotNull();

		SubscriptionAttributes subscriptionAttributes = regionAttributes.getSubscriptionAttributes();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void partitionRegionSubscriptionDefaultPolicy() {

		assertThat(requireApplicationContext().containsBean("partDEFAULT")).isTrue();

		PeerRegionFactoryBean regionFactoryBean =
			requireApplicationContext().getBean("&partDEFAULT", PeerRegionFactoryBean.class);

		RegionAttributes regionAttributes = regionFactoryBean.getAttributes();

		assertThat(regionAttributes).isNotNull();

		SubscriptionAttributes subscriptionAttributes = regionAttributes.getSubscriptionAttributes();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);
	}
}
