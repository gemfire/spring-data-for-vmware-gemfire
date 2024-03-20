/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.util.Arrays;

import org.apache.geode.cache.AttributesFactory;
import org.apache.geode.cache.RegionAttributes;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

/**
 * Spring {@link FactoryBean} used to create {@link RegionAttributes}.
 *
 * Eliminates the need of using a XML bean 'factory-method' tag.
 *
 * @author Costin Leau
 * @author John Blum
 * @see AttributesFactory
 * @see RegionAttributes
 * @see FactoryBean
 * @see InitializingBean
 */
@SuppressWarnings({ "unused" })
// TODO: Refactor RegionAttributesFactoryBean to no longer directly extend AttributesFactory
public class RegionAttributesFactoryBean<K, V> extends AttributesFactory<K, V>
		implements FactoryBean<RegionAttributes<K, V>>, InitializingBean {

	private RegionAttributes<K, V> regionAttributes;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.regionAttributes = super.create();
	}

	@Override
	public RegionAttributes<K, V> getObject() throws Exception {
		return this.regionAttributes;
	}

	@Override
	public Class<?> getObjectType() {

		return this.regionAttributes != null
			? this.regionAttributes.getClass()
			: RegionAttributes.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setAsyncEventQueueIds(@NonNull String[] asyncEventQueueIds) {

		Arrays.stream(ArrayUtils.nullSafeArray(asyncEventQueueIds, String.class))
			.filter(StringUtils::hasText)
			.map(String::trim)
			.forEach(this::addAsyncEventQueueId);
	}

	public void setIndexUpdateType(@NonNull IndexMaintenancePolicyType indexUpdateType) {
		indexUpdateType.setIndexMaintenance(this);
	}

	public void setGatewaySenderIds(@NonNull String[] gatewaySenderIds) {

		Arrays.stream(ArrayUtils.nullSafeArray(gatewaySenderIds, String.class))
			.filter(StringUtils::hasText)
			.map(String::trim)
			.forEach(this::addGatewaySenderId);
	}
}
