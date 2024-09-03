/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.Region;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the functionality of cache {@link Region Regions} when persistent/non-persistent with
 * and without Eviction settings when specifying a {@link DiskStore}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0.RC1
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionsWithDiskStoreAndPersistenceEvictionSettingsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("NotPersistentNoOverflowRegion")
	private Region<?, ?> notPersistentNoOverflowRegion;

	@Autowired
	@Qualifier("NotPersistentOverflowRegion")
	private Region<?, ?> notPersistentOverflowRegion;

	@Autowired
	@Qualifier("PersistentNoOverflowRegion")
	private Region<?, ?> persistentNoOverflowRegion;

	@Autowired
	@Qualifier("PersistentOverflowRegion")
	private Region<?, ?> persistentOverflowRegion;

	@Test
	public void testNotPersistentNoOverflowRegion() {

		assertThat(notPersistentNoOverflowRegion)
			.describedAs("The Not Persistent, No Overflow Region was not properly configured and initialized")
			.isNotNull();

		assertThat(notPersistentNoOverflowRegion.getAttributes()).isNotNull();
		assertThat(notPersistentNoOverflowRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(notPersistentNoOverflowRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(notPersistentNoOverflowRegion.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.NONE);
		assertThat(notPersistentNoOverflowRegion.getAttributes().getDiskStoreName()).isEqualTo("DiskStoreOne");
	}

	@Test
	public void testNotPersistentOverflowRegion() {

		assertThat(notPersistentOverflowRegion)
			.describedAs("The Not Persistent, Overflow Region was not properly configured and initialized")
			.isNotNull();

		assertThat(notPersistentOverflowRegion.getAttributes()).isNotNull();
		assertThat(notPersistentOverflowRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(notPersistentOverflowRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(notPersistentOverflowRegion.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(notPersistentOverflowRegion.getAttributes().getDiskStoreName()).isEqualTo("DiskStoreOne");
	}

	@Test
	public void testPersistentNoOverflowRegion() {

		assertThat(persistentNoOverflowRegion)
			.describedAs("The Persistent, No Overflow Region was not properly configured and initialized")
			.isNotNull();

		assertThat(persistentNoOverflowRegion.getAttributes()).isNotNull();
		assertThat(persistentNoOverflowRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(persistentNoOverflowRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(persistentNoOverflowRegion.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(persistentNoOverflowRegion.getAttributes().getDiskStoreName()).isEqualTo("DiskStoreOne");
	}

	@Test
	public void testPersistentOverflowRegion() {

		assertThat(persistentOverflowRegion)
			.describedAs("The Persistent, Overflow Region was not properly configured and initialized")
			.isNotNull();

		assertThat(persistentOverflowRegion.getAttributes()).isNotNull();
		assertThat(persistentOverflowRegion.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		assertThat(persistentOverflowRegion.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(persistentOverflowRegion.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(persistentOverflowRegion.getAttributes().getDiskStoreName()).isEqualTo("DiskStoreOne");
	}
}
