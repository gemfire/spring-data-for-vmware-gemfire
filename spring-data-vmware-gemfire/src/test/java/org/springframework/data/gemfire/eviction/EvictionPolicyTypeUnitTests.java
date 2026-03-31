/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.eviction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;

/**
 * Unit Tests for {@link EvictionPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudEvictionAlgorithm
 * @see org.springframework.data.gemfire.eviction.EvictionPolicyType
 * @since 1.6.0
 */
public class EvictionPolicyTypeUnitTests {

	@Test
	public void testStaticGetEvictionAlgorithm() {

		assertThat(EvictionPolicyType.getEvictionAlgorithm(EvictionPolicyType.HEAP_PERCENTAGE)).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
		assertThat(EvictionPolicyType.getEvictionAlgorithm(EvictionPolicyType.MEMORY_SIZE)).isEqualTo(GudEvictionAlgorithm.LRU_MEMORY);
	}

	@Test
	public void testStaticGetEvictionAlgorithmWithNull() {
		assertThat(EvictionPolicyType.getEvictionAlgorithm(null)).isNull();
	}

	@Test
	public void testGetEvictionAlgorithm() {

		assertThat(EvictionPolicyType.ENTRY_COUNT.getEvictionAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_ENTRY);
		assertThat(EvictionPolicyType.HEAP_PERCENTAGE.getEvictionAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_HEAP);
		assertThat(EvictionPolicyType.MEMORY_SIZE.getEvictionAlgorithm()).isEqualTo(GudEvictionAlgorithm.LRU_MEMORY);
		assertThat(EvictionPolicyType.NONE.getEvictionAlgorithm()).isEqualTo(GudEvictionAlgorithm.NONE);
	}

	@Test
	public void testValueOf() {

		assertThat(EvictionPolicyType.valueOf(GudEvictionAlgorithm.LRU_ENTRY)).isEqualTo(EvictionPolicyType.ENTRY_COUNT);
		assertThat(EvictionPolicyType.valueOf(GudEvictionAlgorithm.LRU_HEAP)).isEqualTo(EvictionPolicyType.HEAP_PERCENTAGE);
		assertThat(EvictionPolicyType.valueOf(GudEvictionAlgorithm.LRU_MEMORY)).isEqualTo(EvictionPolicyType.MEMORY_SIZE);
		assertThat(EvictionPolicyType.valueOf(GudEvictionAlgorithm.NONE)).isEqualTo(EvictionPolicyType.NONE);
	}

	@Test
	@SuppressWarnings("deprecation")
	public void testValueOfInvalidEvictionAlgorithms() {

		assertThat(EvictionPolicyType.valueOf(GudEvictionAlgorithm.LIFO_ENTRY)).isNull();
		assertThat(EvictionPolicyType.valueOf(GudEvictionAlgorithm.LIFO_MEMORY)).isNull();
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(EvictionPolicyType.valueOf((GudEvictionAlgorithm) null)).isNull();
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
