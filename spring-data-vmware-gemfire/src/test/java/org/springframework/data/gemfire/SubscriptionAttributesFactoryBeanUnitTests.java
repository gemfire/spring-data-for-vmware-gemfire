/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.SubscriptionAttributes;

/**
 * Unit Tests for {@link SubscriptionAttributesFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.SubscriptionAttributes
 * @see org.springframework.data.gemfire.SubscriptionAttributesFactoryBean
 * @since 1.6.0
 */
public class SubscriptionAttributesFactoryBeanUnitTests {

	@Test
	public void testIsSingleton() {
		assertThat(new SubscriptionAttributesFactoryBean().isSingleton()).isTrue();
	}

	@Test
	public void testSetAndGetInterestPolicy() {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);

		factoryBean.setInterestPolicy(InterestPolicy.CACHE_CONTENT);

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.CACHE_CONTENT);

		factoryBean.setInterestPolicy(null);

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);
	}

	@Test
	public void testGetObjectAndObjectTypeForAllInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.setInterestPolicy(InterestPolicy.ALL);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.ALL);

		SubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.ALL);
		assertThat(SubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}

	@Test
	public void testGetObjectAndObjectTypeForCacheContentInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.setInterestPolicy(InterestPolicy.CACHE_CONTENT);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.CACHE_CONTENT);

		SubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.CACHE_CONTENT);
		assertThat(SubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}

	@Test
	public void testGetObjectAndObjectTypeForDefaultInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);

		SubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);
		assertThat(SubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}

	@Test
	public void testGetObjectAndObjectTypeForNullInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.setInterestPolicy(null);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);

		SubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);
		assertThat(SubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}
}
