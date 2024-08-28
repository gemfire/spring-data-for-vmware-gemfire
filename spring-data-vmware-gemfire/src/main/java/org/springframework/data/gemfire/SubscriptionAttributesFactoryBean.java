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
 * The SubscriptionAttributesFactoryBean class is a Spring FactoryBean used for defining and constructing
 * a GemFire SubscriptionAttributes object, which determines the Subscription policy used by Regions to
 * declared their data interests.
 *
 * @author Lyndon Adams
 * @author John Blum
 * @see FactoryBean
 * @see InitializingBean
 * @see InterestPolicy
 * @see SubscriptionAttributes
 * @since 1.3.0
 */
public class SubscriptionAttributesFactoryBean implements FactoryBean<SubscriptionAttributes>, InitializingBean {

	private InterestPolicy interestPolicy;

	private SubscriptionAttributes subscriptionAttributes;

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		this.subscriptionAttributes = new SubscriptionAttributes(getInterestPolicy());
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public SubscriptionAttributes getObject() throws Exception {
		return this.subscriptionAttributes;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {

		return this.subscriptionAttributes != null
			? this.subscriptionAttributes.getClass()
			: SubscriptionAttributes.class;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
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
