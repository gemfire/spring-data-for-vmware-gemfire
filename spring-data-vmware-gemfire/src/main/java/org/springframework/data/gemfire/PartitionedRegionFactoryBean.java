/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.util.Assert;

/**
 * Spring {@link FactoryBean} used to create an Apache Geode {@literal PARTITION} {@link Region}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionFactory
 * @see org.springframework.beans.factory.BeanFactory
 */
public class PartitionedRegionFactoryBean<K, V> extends PeerRegionFactoryBean<K, V> {

	@Override
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, DataPolicy dataPolicy) {

		if (dataPolicy == null) {
			dataPolicy = isPersistent() ? DataPolicy.PERSISTENT_PARTITION : DataPolicy.PARTITION;
		}
		else {
			// Validate that the user-defined Data Policy matches the appropriate Spring GemFire XML namespace
			// configuration meta-data element for Region (i.e. <gfe:partitioned-region .../>)!
			Assert.isTrue(dataPolicy.withPartitioning(),
				String.format("Data Policy [%s] is not supported in Partitioned Regions.", dataPolicy));
		}

		// Validate the data-policy and persistent attributes are compatible when specified!
		RegionUtils.assertDataPolicyAndPersistentAttributeAreCompatible(dataPolicy, persistent);

		regionFactory.setDataPolicy(dataPolicy);
		setDataPolicy(dataPolicy);
	}

	@Override
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, String dataPolicy) {

		DataPolicy resolvedDataPolicy = null;

		if (dataPolicy != null) {
			resolvedDataPolicy = new DataPolicyConverter().convert(dataPolicy);
			Assert.notNull(resolvedDataPolicy, String.format("Data Policy [%s] is invalid.", dataPolicy));
		}

		resolveDataPolicy(regionFactory, persistent, resolvedDataPolicy);
	}
}
