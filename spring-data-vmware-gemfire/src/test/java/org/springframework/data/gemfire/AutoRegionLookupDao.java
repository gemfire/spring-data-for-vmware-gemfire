/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
import org.springframework.dao.support.DaoSupport;
import org.springframework.stereotype.Repository;

/**
 * {@link AutoRegionLookupDao} is a Data Access Object (DAO) encapsulating references to several cache
 * {@link GudRegion Regions} defined in native Apache Geode {@literal cache.xml} and registered as beans in the Spring
 * context using Spring Data for Apache Geode's auto {@link GudRegion} lookup functionality.
 *
 * This class is used by the {@link AutoRegionLookupWithComponentScanningIntegrationTests} class to ensure
 * this {@link Repository @Repository} component is auto-wired properly.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GudRegion
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
	private GudRegion<?, ?> nativeClientRegion;

	@Autowired
	@Qualifier("NativeClientParent")
	private GudRegion<?, ?> nativeClientParent;

	@Autowired
	@Qualifier("/NativeClientParent/NativeClientChild")
	private GudRegion<?, ?> nativeClientChild;

	@Autowired
	@Qualifier("/NativeClientParent/NativeClientChild/NativeClientGrandchild")
	private GudRegion<?, ?> nativeClientGrandchild;

	protected static void assertRegionMetaData(GudRegion<?, ?> region, String expectedName, GudDataPolicy expectedDataPolicy) {
		assertRegionMetaData(region, expectedName, GudRegion.SEPARATOR + expectedName, expectedDataPolicy);
	}

	protected static void assertRegionMetaData(GudRegion<?, ?> region, String expectedName, String expectedFullPath,
			GudDataPolicy expectedDataPolicy) {

		assertThat(region)
			.describedAs("GudRegion [%s] was not properly configured and initialized", expectedName)
			.isNotNull();

		assertThat(region.getName()).isEqualTo(expectedName);
		assertThat(region.getFullPath()).isEqualTo(expectedFullPath);

		assertThat(region.getAttributes())
			.describedAs("GudRegion [%s] must have RegionAttributes defined", expectedName)
			.isNotNull();

		assertThat(region.getAttributes().getDataPolicy()).isEqualTo(expectedDataPolicy);
		assertThat(region.getAttributes().getDataPolicy().withPersistence()).isFalse();
	}

	@Override
	protected void checkDaoConfig() throws IllegalArgumentException {

		assertRegionMetaData(nativeClientRegion, "NativeClientRegion", GudDataPolicy.NORMAL);
		assertRegionMetaData(nativeClientParent, "NativeClientParent", GudDataPolicy.NORMAL);
		assertRegionMetaData(nativeClientChild, "NativeClientChild",
			"/NativeClientParent/NativeClientChild", GudDataPolicy.NORMAL);
		assertRegionMetaData(nativeClientGrandchild, "NativeClientGrandchild",
			"/NativeClientParent/NativeClientChild/NativeClientGrandchild", GudDataPolicy.NORMAL);
	}
}
