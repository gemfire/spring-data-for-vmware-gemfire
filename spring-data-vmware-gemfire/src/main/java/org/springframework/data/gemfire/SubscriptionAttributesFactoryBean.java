/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudInterestPolicy;
import org.springframework.data.gemfire.gud.api.GudSubscriptionAttributes;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * Spring {@link FactoryBean} used for defining and constructing an Apache Geode {@link GudSubscriptionAttributes} object,
 * which determines the subscription policy used by cache Regions declaring their data interests.
 *
 * @author Lyndon Adams
 * @author John Blum
 * @see GudInterestPolicy
 * @see GudSubscriptionAttributes
 * @see FactoryBean
 * @see InitializingBean
 * @since 1.3.0
 */
public class SubscriptionAttributesFactoryBean implements FactoryBean<GudSubscriptionAttributes>, InitializingBean {

	private GudInterestPolicy interestPolicy;

	private GudSubscriptionAttributes subscriptionAttributes;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.subscriptionAttributes = GudSubscriptionAttributes.create(getInterestPolicy());
	}

	@Override
	public GudSubscriptionAttributes getObject() throws Exception {
		return this.subscriptionAttributes;
	}

	@Override
	public Class<?> getObjectType() {

		return this.subscriptionAttributes != null
			? this.subscriptionAttributes.getClass()
			: GudSubscriptionAttributes.class;
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
	 * @see GudInterestPolicy
	 * @see GudSubscriptionAttributes
	 */
	public void setInterestPolicy(GudInterestPolicy interestPolicy) {
		this.interestPolicy = interestPolicy;
	}

	/**
	 * Gets GemFire's InterestPolicy specified on the SubscriptionAttributes which defines data interests
	 * and distribution of changes.
	 *
	 * @return the GemFire InterestsPolicy set for Subscription.
	 * @see GudInterestPolicy
	 * @see GudSubscriptionAttributes#getInterestPolicy()
	 */
	public GudInterestPolicy getInterestPolicy() {
		return this.interestPolicy != null ? this.interestPolicy : GudInterestPolicy.DEFAULT;
	}
}
