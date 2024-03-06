/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.RegionFactory;

import org.springframework.data.gemfire.util.RegionUtils;
import org.springframework.util.Assert;

/**
 * @author David Turanski
 * @author John Blum
 */
public class ReplicatedRegionFactoryBean<K, V> extends PeerRegionFactoryBean<K, V> {

	@Override
	protected void resolveDataPolicy(RegionFactory<K, V> regionFactory, Boolean persistent, DataPolicy dataPolicy) {

		if (dataPolicy == null) {
			dataPolicy = isPersistent() ? DataPolicy.PERSISTENT_REPLICATE : DataPolicy.REPLICATE;
		}
		else if (DataPolicy.EMPTY.equals(dataPolicy)) {
			dataPolicy = DataPolicy.EMPTY;
		}
		else {
			// Validate that the user-defined Data Policy matches the appropriate Spring GemFire XML namespace
			// configuration meta-data element for the Region (i.e. <gfe:replicated-region .../>)!
			Assert.isTrue(dataPolicy.withReplication(), String.format(
				"Data Policy [%s] is not supported in Replicated Regions.", dataPolicy));
		}

		// Validate that the data-policy and persistent attributes are compatible when both are specified!
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
