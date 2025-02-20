/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientCache;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode {@link Region sub-Regions} defined in SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings({ "rawtypes", "unused" })
public class SubRegionIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ClientCache cache;

	@Autowired
	@Qualifier("Customers")
	private Region customers;

	@Autowired
	@Qualifier("/Customers/Accounts")
	private Region accounts;

	@Test
	@SuppressWarnings("unchecked")
	public void testGemFireAccountsSubRegionCreation() {

		Assertions.assertThat(cache).as("The GemFire Cache was not properly initialized").isNotNull();

		Region customers = cache.getRegion("Customers");

		Assertions.assertThat(customers).isNotNull();
		Assertions.assertThat(customers.getName()).isEqualTo("Customers");
		Assertions.assertThat(customers.getFullPath()).isEqualTo("/Customers");

		Region accounts = customers.getSubregion("Accounts");

		Assertions.assertThat(accounts).isNotNull();
		Assertions.assertThat(accounts.getName()).isEqualTo("Accounts");
		Assertions.assertThat(accounts.getFullPath()).isEqualTo("/Customers/Accounts");

		Region cacheAccounts = cache.getRegion("/Customers/Accounts");

		Assertions.assertThat(cacheAccounts).isSameAs(accounts);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testSpringSubRegionConfiguration() {

		Assertions.assertThat(accounts).as("The /Customers/Accounts SubRegion was not properly initialized").isNotNull();
		Assertions.assertThat(accounts.getName()).isEqualTo("Accounts");
		Assertions.assertThat(accounts.getFullPath()).isEqualTo("/Customers/Accounts");

		RegionAttributes regionAttributes = accounts.getAttributes();

		Assertions.assertThat(regionAttributes).isNotNull();
		Assertions.assertThat(regionAttributes.getDataPolicy()).isEqualTo(DataPolicy.PERSISTENT_REPLICATE);
		Assertions.assertThat(regionAttributes.getConcurrencyLevel()).isEqualTo(20);
		Assertions.assertThat(regionAttributes.isDiskSynchronous()).isTrue();
		Assertions.assertThat(regionAttributes.getInitialCapacity()).isEqualTo(1000);
		Assertions.assertThat(regionAttributes.getKeyConstraint()).isEqualTo(Long.class);
		Assertions.assertThat(regionAttributes.getStatisticsEnabled()).isTrue();
		Assertions.assertThat(regionAttributes.getValueConstraint()).isEqualTo(String.class);
		Assertions.assertThat(regionAttributes.getCacheListeners()).isNotNull();
		Assertions.assertThat(regionAttributes.getCacheListeners().length).isEqualTo(1);
		Assertions.assertThat(regionAttributes.getCacheListeners()[0] instanceof SimpleCacheListener).isTrue();
		Assertions.assertThat(regionAttributes.getCacheLoader() instanceof SimpleCacheLoader).isTrue();
		Assertions.assertThat(regionAttributes.getCacheWriter() instanceof SimpleCacheWriter).isTrue();

		EvictionAttributes evictionAttributes = regionAttributes.getEvictionAttributes();

		Assertions.assertThat(evictionAttributes).isNotNull();
		Assertions.assertThat(evictionAttributes.getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		Assertions.assertThat(evictionAttributes.getMaximum()).isEqualTo(10000);
	}
}
