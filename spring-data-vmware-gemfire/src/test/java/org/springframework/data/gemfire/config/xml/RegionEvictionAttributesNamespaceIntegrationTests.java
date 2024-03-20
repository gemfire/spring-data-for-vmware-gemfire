/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link Region} Eviction configuration settings ({@link EvictionAttributes})
 * using SDG XML namespace configuration metadata.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.EvictionAttributes
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringJUnit4ClassRunner
 * @since 1.3.4
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionEvictionAttributesNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("One")
	private Region<?, ?> one;

	@Autowired
	@Qualifier("Two")
	private Region<?, ?> two;

	@Autowired
	@Qualifier("Three")
	private Region<?, ?> three;

	@Autowired
	@Qualifier("Four")
	private Region<?, ?> four;

	@Autowired
	@Qualifier("Five")
	private Region<?, ?> five;

	@Autowired
	@Qualifier("Six")
	private Region<?, ?> six;

	@Test
	public void testEntryCountRegionEvictionAttributes() {

		assertThat(one).isNotNull();
		assertThat(one.getAttributes()).isNotNull();
		assertThat(one.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
		assertThat(one.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(one.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(one.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		assertThat(one.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(4096);

		assertThat(two).isNotNull();
		assertThat(two.getAttributes()).isNotNull();
		assertThat(two.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		assertThat(two.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(two.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		assertThat(two.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		assertThat(two.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(EvictionAttributes.DEFAULT_ENTRIES_MAXIMUM);
	}

	@Test
	public void testHeapPercentageRegionEvictionAttributes() {

		assertThat(three).isNotNull();
		assertThat(three.getAttributes()).isNotNull();
		assertThat(three.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
		assertThat(three.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(three.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(three.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);

		assertThat(four).isNotNull();
		assertThat(four.getAttributes()).isNotNull();
		assertThat(four.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		assertThat(four.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(four.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(three.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);
		assertThat(four.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(0);
	}

	@Test
	public void testMemorySizeRegionEvictionAttributes() {

		assertThat(five).isNotNull();
		assertThat(five.getAttributes()).isNotNull();
		assertThat(five.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.REPLICATE);
		assertThat(five.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(five.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(five.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);
		assertThat(five.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(128);

		assertThat(six).isNotNull();
		assertThat(six.getAttributes()).isNotNull();
		assertThat(six.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.PARTITION);
		assertThat(six.getAttributes().getEvictionAttributes()).isNotNull();
		assertThat(six.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		assertThat(six.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);

		int expectedMaximum = Boolean.getBoolean("org.springframework.data.gemfire.test.GemfireTestRunner.nomock")
			? 512 : 256;

		assertThat(six.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(expectedMaximum);
	}
}
