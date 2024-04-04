/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Repository;

/**
 * {@link AutoRegionLookupDao} is a Data Access Object (DAO) encapsulating references to several cache
 * {@link Region Regions} defined in native Apache Geode {@literal cache.xml} and registered as beans in the Spring
 * context using Spring Data for Apache Geode's auto {@link Region} lookup functionality.
 *
 * This class is used by the {@link AutoRegionLookupWithComponentScanningIntegrationTests} class to ensure
 * this {@link Repository @Repository} component is auto-wired properly.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.springframework.dao.support.DaoSupport
 * @see org.springframework.stereotype.Repository
 * @since 1.5.0
 */
//@Lazy
@DependsOn("gemfireCache")
@Repository("autoRegionLookupDao")
@SuppressWarnings("unused")
public class AutoRegionLookupDao extends DaoSupport {

	@Autowired
	@Qualifier("NativeClientRegion")
	private Region<?, ?> nativeClientRegion;

	@Autowired
	@Qualifier("NativeClientParent")
	private Region<?, ?> nativeClientParent;

	@Autowired
	@Qualifier("/NativeClientParent/NativeClientChild")
	private Region<?, ?> nativeClientChild;

	@Autowired
	@Qualifier("/NativeClientParent/NativeClientChild/NativeClientGrandchild")
	private Region<?, ?> nativeClientGrandchild;

	protected static void assertRegionMetaData(Region<?, ?> region, String expectedName, DataPolicy expectedDataPolicy) {
		assertRegionMetaData(region, expectedName, Region.SEPARATOR + expectedName, expectedDataPolicy);
	}

	protected static void assertRegionMetaData(Region<?, ?> region, String expectedName, String expectedFullPath,
			DataPolicy expectedDataPolicy) {

		assertThat(region)
			.describedAs("Region [%s] was not properly configured and initialized", expectedName)
			.isNotNull();

		assertThat(region.getName()).isEqualTo(expectedName);
		assertThat(region.getFullPath()).isEqualTo(expectedFullPath);

		assertThat(region.getAttributes())
			.describedAs("Region [%s] must have RegionAttributes defined", expectedName)
			.isNotNull();

		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(expectedDataPolicy);
		assertThat(region.getAttributes().getDataPolicy().withPersistence()).isFalse();
	}

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {

		assertRegionMetaData(nativeClientRegion, "NativeClientRegion", DataPolicy.NORMAL);
		assertRegionMetaData(nativeClientParent, "NativeClientParent", DataPolicy.NORMAL);
		assertRegionMetaData(nativeClientChild, "NativeClientChild",
			"/NativeClientParent/NativeClientChild", DataPolicy.NORMAL);
		assertRegionMetaData(nativeClientGrandchild, "NativeClientGrandchild",
			"/NativeClientParent/NativeClientChild/NativeClientGrandchild", DataPolicy.NORMAL);
	}
}
