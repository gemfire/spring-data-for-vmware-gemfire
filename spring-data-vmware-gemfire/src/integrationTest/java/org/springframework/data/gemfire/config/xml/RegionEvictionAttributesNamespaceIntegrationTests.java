/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAction;
import org.apache.geode.cache.EvictionAlgorithm;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
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

		Assertions.assertThat(one).isNotNull();
		Assertions.assertThat(one.getAttributes()).isNotNull();
		Assertions.assertThat(one.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(one.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(one.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		Assertions.assertThat(one.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		Assertions.assertThat(one.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(4096);

		Assertions.assertThat(two).isNotNull();
		Assertions.assertThat(two.getAttributes()).isNotNull();
		Assertions.assertThat(two.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(two.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(two.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		Assertions.assertThat(two.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		Assertions.assertThat(two.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(EvictionAttributes.DEFAULT_ENTRIES_MAXIMUM);
	}

	@Test
	public void testHeapPercentageRegionEvictionAttributes() {

		Assertions.assertThat(three).isNotNull();
		Assertions.assertThat(three.getAttributes()).isNotNull();
		Assertions.assertThat(three.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(three.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(three.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		Assertions.assertThat(three.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);

		Assertions.assertThat(four).isNotNull();
		Assertions.assertThat(four.getAttributes()).isNotNull();
		Assertions.assertThat(four.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(four.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(four.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.OVERFLOW_TO_DISK);
		Assertions.assertThat(three.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);
		Assertions.assertThat(four.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(0);
	}

	@Test
	public void testMemorySizeRegionEvictionAttributes() {

		Assertions.assertThat(five).isNotNull();
		Assertions.assertThat(five.getAttributes()).isNotNull();
		Assertions.assertThat(five.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(five.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(five.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		Assertions.assertThat(five.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);
		Assertions.assertThat(five.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(128);

		Assertions.assertThat(six).isNotNull();
		Assertions.assertThat(six.getAttributes()).isNotNull();
		Assertions.assertThat(six.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(six.getAttributes().getEvictionAttributes()).isNotNull();
		Assertions.assertThat(six.getAttributes().getEvictionAttributes().getAction()).isEqualTo(EvictionAction.LOCAL_DESTROY);
		Assertions.assertThat(six.getAttributes().getEvictionAttributes().getAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);

		int expectedMaximum = Boolean.getBoolean("org.springframework.data.gemfire.test.GemfireTestRunner.nomock")
			? 512 : 256;

		Assertions.assertThat(six.getAttributes().getEvictionAttributes().getMaximum()).isEqualTo(expectedMaximum);
	}
}
