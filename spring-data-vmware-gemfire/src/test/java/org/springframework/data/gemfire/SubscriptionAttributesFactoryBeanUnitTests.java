/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudInterestPolicy;
import org.springframework.data.gemfire.gud.api.GudSubscriptionAttributes;

/**
 * Unit Tests for {@link SubscriptionAttributesFactoryBean}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudSubscriptionAttributes
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

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);

		factoryBean.setInterestPolicy(GudInterestPolicy.CACHE_CONTENT);

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.CACHE_CONTENT);

		factoryBean.setInterestPolicy(null);

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);
	}

	@Test
	public void testGetObjectAndObjectTypeForAllInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.setInterestPolicy(GudInterestPolicy.ALL);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.ALL);

		GudSubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(GudInterestPolicy.ALL);
		assertThat(GudSubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}

	@Test
	public void testGetObjectAndObjectTypeForCacheContentInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.setInterestPolicy(GudInterestPolicy.CACHE_CONTENT);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.CACHE_CONTENT);

		GudSubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(GudInterestPolicy.CACHE_CONTENT);
		assertThat(GudSubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}

	@Test
	public void testGetObjectAndObjectTypeForDefaultInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);

		GudSubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);
		assertThat(GudSubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}

	@Test
	public void testGetObjectAndObjectTypeForNullInterestPolicy() throws Exception {

		SubscriptionAttributesFactoryBean factoryBean = new SubscriptionAttributesFactoryBean();

		factoryBean.setInterestPolicy(null);
		factoryBean.afterPropertiesSet();

		assertThat(factoryBean.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);

		GudSubscriptionAttributes subscriptionAttributes = factoryBean.getObject();

		assertThat(subscriptionAttributes).isNotNull();
		assertThat(subscriptionAttributes.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);
		assertThat(GudSubscriptionAttributes.class.isAssignableFrom(factoryBean.getObjectType())).isTrue();
	}
}
