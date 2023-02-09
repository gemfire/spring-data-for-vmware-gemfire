/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.eviction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.EvictionAlgorithm;

/**
 * Unit Tests for {@link EvictionPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.EvictionAlgorithm
 * @see org.springframework.data.gemfire.eviction.EvictionPolicyType
 * @since 1.6.0
 */
public class EvictionPolicyTypeUnitTests {

	@Test
	public void testStaticGetEvictionAlgorithm() {

		assertThat(EvictionPolicyType.getEvictionAlgorithm(EvictionPolicyType.HEAP_PERCENTAGE)).isEqualTo(EvictionAlgorithm.LRU_HEAP);
		assertThat(EvictionPolicyType.getEvictionAlgorithm(EvictionPolicyType.MEMORY_SIZE)).isEqualTo(EvictionAlgorithm.LRU_MEMORY);
	}

	@Test
	public void testStaticGetEvictionAlgorithmWithNull() {
		assertThat(EvictionPolicyType.getEvictionAlgorithm(null)).isNull();
	}

	@Test
	public void testGetEvictionAlgorithm() {

		assertThat(EvictionPolicyType.ENTRY_COUNT.getEvictionAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_ENTRY);
		assertThat(EvictionPolicyType.HEAP_PERCENTAGE.getEvictionAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_HEAP);
		assertThat(EvictionPolicyType.MEMORY_SIZE.getEvictionAlgorithm()).isEqualTo(EvictionAlgorithm.LRU_MEMORY);
		assertThat(EvictionPolicyType.NONE.getEvictionAlgorithm()).isEqualTo(EvictionAlgorithm.NONE);
	}

	@Test
	public void testValueOf() {

		assertThat(EvictionPolicyType.valueOf(EvictionAlgorithm.LRU_ENTRY)).isEqualTo(EvictionPolicyType.ENTRY_COUNT);
		assertThat(EvictionPolicyType.valueOf(EvictionAlgorithm.LRU_HEAP)).isEqualTo(EvictionPolicyType.HEAP_PERCENTAGE);
		assertThat(EvictionPolicyType.valueOf(EvictionAlgorithm.LRU_MEMORY)).isEqualTo(EvictionPolicyType.MEMORY_SIZE);
		assertThat(EvictionPolicyType.valueOf(EvictionAlgorithm.NONE)).isEqualTo(EvictionPolicyType.NONE);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testValueOfInvalidEvictionAlgorithms() {

		assertThat(EvictionPolicyType.valueOf(EvictionAlgorithm.LIFO_ENTRY)).isNull();
		assertThat(EvictionPolicyType.valueOf(EvictionAlgorithm.LIFO_MEMORY)).isNull();
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(EvictionPolicyType.valueOf((EvictionAlgorithm) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(EvictionPolicyType.valueOfIgnoreCase("entry_count")).isEqualTo(EvictionPolicyType.ENTRY_COUNT);
		assertThat(EvictionPolicyType.valueOfIgnoreCase("Heap_Percentage")).isEqualTo(EvictionPolicyType.HEAP_PERCENTAGE);
		assertThat(EvictionPolicyType.valueOfIgnoreCase("MEMorY_SiZe")).isEqualTo(EvictionPolicyType.MEMORY_SIZE);
		assertThat(EvictionPolicyType.valueOfIgnoreCase("NONE")).isEqualTo(EvictionPolicyType.NONE);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {

		assertThat(EvictionPolicyType.valueOfIgnoreCase("number_of_entries")).isNull();
		assertThat(EvictionPolicyType.valueOfIgnoreCase("heap_%")).isNull();
		assertThat(EvictionPolicyType.valueOfIgnoreCase("mem_size")).isNull();
		assertThat(EvictionPolicyType.valueOfIgnoreCase("memory_space")).isNull();
		assertThat(EvictionPolicyType.valueOfIgnoreCase("  ")).isNull();
		assertThat(EvictionPolicyType.valueOfIgnoreCase("")).isNull();
		assertThat(EvictionPolicyType.valueOfIgnoreCase(null)).isNull();
	}
}
