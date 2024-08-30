/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.InterestPolicy;
import org.apache.geode.cache.SubscriptionAttributes;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Spring {@link FactoryBean} used for defining and constructing an Apache Geode {@link SubscriptionAttributes} object,
 * which determines the subscription policy used by cache Regions declaring their data interests.
 *
 * @author Lyndon Adams
 * @author John Blum
 * @see InterestPolicy
 * @see SubscriptionAttributes
 * @see FactoryBean
 * @see InitializingBean
 * @since 1.3.0
 */
public class SubscriptionAttributesFactoryBean implements FactoryBean<SubscriptionAttributes>, InitializingBean {

	private InterestPolicy interestPolicy;

	private SubscriptionAttributes subscriptionAttributes;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.subscriptionAttributes = new SubscriptionAttributes(getInterestPolicy());
	}

	@Override
	public SubscriptionAttributes getObject() throws Exception {
		return this.subscriptionAttributes;
	}

	@Override
	public Class<?> getObjectType() {

		return this.subscriptionAttributes != null
			? this.subscriptionAttributes.getClass()
			: SubscriptionAttributes.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	/**
	 * Sets GemFire's InterestPolicy specified on the SubscriptionAttributes in order to define/declare
	 * the data interests and distribution of changes.
	 *
	 * @param interestPolicy the GemFire InterestsPolicy to set for Subscription.
	 * @see InterestPolicy
	 * @see SubscriptionAttributes#SubscriptionAttributes(InterestPolicy)
	 */
	public void setInterestPolicy(InterestPolicy interestPolicy) {
		this.interestPolicy = interestPolicy;
	}

	/**
	 * Gets GemFire's InterestPolicy specified on the SubscriptionAttributes which defines data interests
	 * and distribution of changes.
	 *
	 * @return the GemFire InterestsPolicy set for Subscription.
	 * @see InterestPolicy
	 * @see SubscriptionAttributes#getInterestPolicy()
	 */
	public InterestPolicy getInterestPolicy() {
		return this.interestPolicy != null ? this.interestPolicy : InterestPolicy.DEFAULT;
	}
}
