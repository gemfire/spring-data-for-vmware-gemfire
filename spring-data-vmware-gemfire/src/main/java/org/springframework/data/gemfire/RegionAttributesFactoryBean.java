/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudAttributesFactory;
import org.springframework.data.gemfire.gud.api.GudAttributesFactoryImpl;
import org.springframework.data.gemfire.gud.api.GudCacheListener;
import org.springframework.data.gemfire.gud.api.GudCacheLoader;
import org.springframework.data.gemfire.gud.api.GudCacheWriter;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.data.gemfire.gud.api.GudScope;

/**
 * Spring {@link FactoryBean} used to create {@link GudRegionAttributes}.
 *
 * Eliminates the need of using a XML bean 'factory-method' tag.
 *
 * @author Costin Leau
 * @author John Blum
 * @see GudAttributesFactory
 * @see GudRegionAttributes
 * @see FactoryBean
 * @see InitializingBean
 */
@SuppressWarnings({ "unused" })
public class RegionAttributesFactoryBean<K, V>
		implements GudAttributesFactory<K, V>, FactoryBean<GudRegionAttributes<K, V>>, InitializingBean {

	private final GudAttributesFactory<K, V> delegate = GudAttributesFactoryImpl.create();

	private GudRegionAttributes<K, V> regionAttributes;

	@Override
	public void afterPropertiesSet() throws Exception {
		this.regionAttributes = delegate.create();
	}

	@Override
	public GudRegionAttributes<K, V> getObject() throws Exception {
		return this.regionAttributes;
	}

	@Override
	public Class<?> getObjectType() {

		return this.regionAttributes != null
			? this.regionAttributes.getClass()
			: GudRegionAttributes.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	@Override
	public GudAttributesFactory<K, V> setScope(GudScope scope) {
		return delegate.setScope(scope);
	}

	@Override
	public GudAttributesFactory<K, V> setDataPolicy(GudDataPolicy dataPolicy) {
		return delegate.setDataPolicy(dataPolicy);
	}

	@Override
	public GudAttributesFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout) {
		return delegate.setEntryIdleTimeout(idleTimeout);
	}

	@Override
	public GudAttributesFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive) {
		return delegate.setEntryTimeToLive(timeToLive);
	}

	@Override
	public GudAttributesFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout) {
		return delegate.setRegionIdleTimeout(idleTimeout);
	}

	@Override
	public GudAttributesFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive) {
		return delegate.setRegionTimeToLive(timeToLive);
	}

	@Override
	public GudAttributesFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes) {
		return delegate.setEvictionAttributes(evictionAttributes);
	}

	@Override
	public GudAttributesFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader) {
		return delegate.setCacheLoader(cacheLoader);
	}

	@Override
	public GudAttributesFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
		return delegate.setCacheWriter(cacheWriter);
	}

	@Override
	public GudAttributesFactory<K, V> addCacheListener(GudCacheListener<K, V> listener) {
		return delegate.addCacheListener(listener);
	}

	@Override
	public GudAttributesFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled) {
		return delegate.setStatisticsEnabled(statisticsEnabled);
	}

	@Override
	public GudAttributesFactory<K, V> setPoolName(String poolName) {
		return delegate.setPoolName(poolName);
	}

	@Override
	public GudAttributesFactory<K, V> setDiskStoreName(String diskStoreName) {
		return delegate.setDiskStoreName(diskStoreName);
	}

	@Override
	public GudAttributesFactory<K, V> setKeyConstraint(Class<K> keyConstraint) {
		return delegate.setKeyConstraint(keyConstraint);
	}

	@Override
	public GudAttributesFactory<K, V> setValueConstraint(Class<V> valueConstraint) {
		return delegate.setValueConstraint(valueConstraint);
	}

	@Override
	public GudRegionAttributes<K, V> create() {
		return delegate.create();
	}
}
