/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.RegionShortcut;
import org.apache.geode.cache.Scope;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

/**
 * Spring {@link FactoryBean} used to create a {@link RegionShortcut#LOCAL} {@literal peer} {@link GemFireCache}
 * {@link Region}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.DataPolicy
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionFactory
 * @see org.apache.geode.cache.RegionShortcut#LOCAL
 * @see org.springframework.data.gemfire.PeerRegionFactoryBean
 */
public class LocalRegionFactoryBean<K, V> extends PeerRegionFactoryBean<K, V> {

	@Override
	public void setScope(Scope scope) {
		throw newUnsupportedOperationException("Setting the Scope on Local Regions is not allowed");
	}

	@Override
	public void afterPropertiesSet() throws Exception {

		super.setScope(Scope.LOCAL);
		super.afterPropertiesSet();
	}

	@Override
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, DataPolicy dataPolicy) {

		if (dataPolicy == null || DataPolicy.NORMAL.equals(dataPolicy)) {

			// NOTE this is safe since a LOCAL Scoped NORMAL Region requiring persistence can be satisfied with
			// PERSISTENT_REPLICATE, per the RegionShortcut.LOCAL_PERSISTENT
			DataPolicy resolvedDataPolicy = (isPersistent() ? DataPolicy.PERSISTENT_REPLICATE : DataPolicy.NORMAL);

			regionFactory.setDataPolicy(resolvedDataPolicy);
			setDataPolicy(resolvedDataPolicy);
		}
		else if (DataPolicy.PRELOADED.equals(dataPolicy)) {

			// NOTE this is safe since a LOCAL Scoped PRELOADED Region requiring persistence can be satisfied with
			// PERSISTENT_REPLICATE, per the RegionShortcut.LOCAL_PERSISTENT
			DataPolicy resolvedDataPolicy = (isPersistent() ? DataPolicy.PERSISTENT_REPLICATE : DataPolicy.PRELOADED);

			regionFactory.setDataPolicy(resolvedDataPolicy);
			setDataPolicy(resolvedDataPolicy);
		}
		else if (DataPolicy.PERSISTENT_REPLICATE.equals(dataPolicy)
				&& RegionShortcutWrapper.valueOf(getShortcut()).isPersistent()) {

			regionFactory.setDataPolicy(dataPolicy);
			setDataPolicy(dataPolicy);
		}
		else {
			throw newIllegalArgumentException("Data Policy [%s] is not supported for Local Regions", dataPolicy);
		}
	}

	/**
	 * Resolves the Data Policy used by this "local" GemFire Region (i.e. locally Scoped; Scope.LOCAL) based on the
	 * enumerated value from org.apache.geode.cache.RegionShortcuts (LOCAL, LOCAL_PERSISTENT, LOCAL_HEAP_LRU,
	 * LOCAL_OVERFLOW, and LOCAL_PERSISTENT_OVERFLOW), but without consideration of the Eviction settings.
	 *
	 * @param regionFactory the GemFire RegionFactory used to created the Local Region.
	 * @param persistent a boolean value indicating whether the Local Region should persist it's data.
	 * @param dataPolicy requested Data Policy as set by the user in the Spring GemFire configuration meta-data.
	 * @see org.apache.geode.cache.DataPolicy
	 * @see org.apache.geode.cache.RegionFactory
	 * @see org.apache.geode.cache.RegionShortcut
	 */
	@Override
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, String dataPolicy) {

		DataPolicy resolvedDataPolicy = null;

		if (dataPolicy != null) {
			resolvedDataPolicy = new DataPolicyConverter().convert(dataPolicy);
			Assert.notNull(resolvedDataPolicy, String.format("Data Policy [%s] is invalid", dataPolicy));
		}

		resolveDataPolicy(regionFactory, persistent, resolvedDataPolicy);
	}
}
