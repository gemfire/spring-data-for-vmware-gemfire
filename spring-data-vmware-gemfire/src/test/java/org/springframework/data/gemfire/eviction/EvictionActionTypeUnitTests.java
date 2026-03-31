/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.eviction;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudEvictionAction;

/**
 * Unit Tests for {@link EvictionActionType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudEvictionAction
 * @see org.springframework.data.gemfire.eviction.EvictionActionType
 * @since 1.6.0
 */
public class EvictionActionTypeUnitTests {

	@Test
	public void testStaticGetEvictionAction() {

		assertThat(EvictionActionType.getEvictionAction(
			EvictionActionType.LOCAL_DESTROY)).isEqualTo(GudEvictionAction.LOCAL_DESTROY);

		assertThat(EvictionActionType.getEvictionAction(
			EvictionActionType.OVERFLOW_TO_DISK)).isEqualTo(GudEvictionAction.OVERFLOW_TO_DISK);
	}

	@Test
	public void testStaticGetEvictionActionWithNull() {
		assertThat(EvictionActionType.getEvictionAction(null)).isNull();
	}

	@Test
	public void testGetEvictionAction() {

		assertThat(EvictionActionType.LOCAL_DESTROY.getEvictionAction()).isEqualTo(GudEvictionAction.LOCAL_DESTROY);
		assertThat(EvictionActionType.NONE.getEvictionAction()).isEqualTo(GudEvictionAction.NONE);
		assertThat(EvictionActionType.OVERFLOW_TO_DISK.getEvictionAction()).isEqualTo(GudEvictionAction.OVERFLOW_TO_DISK);
		assertThat(EvictionActionType.DEFAULT.getEvictionAction()).isEqualTo(GudEvictionAction.DEFAULT_EVICTION_ACTION);
	}

	@Test
	public void testDefault() {

		assertThat(EvictionActionType.DEFAULT.getEvictionAction()).isEqualTo(GudEvictionAction.DEFAULT_EVICTION_ACTION);
		assertThat(EvictionActionType.DEFAULT).isSameAs(EvictionActionType.LOCAL_DESTROY);
	}

	@Test
	public void testValueOf() {

		assertThat(EvictionActionType.valueOf(GudEvictionAction.LOCAL_DESTROY)).isEqualTo(EvictionActionType.LOCAL_DESTROY);
		assertThat(EvictionActionType.valueOf(GudEvictionAction.NONE)).isEqualTo(EvictionActionType.NONE);
		assertThat(EvictionActionType.valueOf(GudEvictionAction.OVERFLOW_TO_DISK)).isEqualTo(EvictionActionType.OVERFLOW_TO_DISK);
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(EvictionActionType.valueOf((GudEvictionAction) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(EvictionActionType.valueOfIgnoreCase("Local_Destroy")).isEqualTo(EvictionActionType.LOCAL_DESTROY);
		assertThat(EvictionActionType.valueOfIgnoreCase("none")).isEqualTo(EvictionActionType.NONE);
		assertThat(EvictionActionType.valueOfIgnoreCase("NONE")).isEqualTo(EvictionActionType.NONE);
		assertThat(EvictionActionType.valueOfIgnoreCase("OverFlow_TO_DiSk")).isEqualTo(EvictionActionType.OVERFLOW_TO_DISK);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {

		assertThat(EvictionActionType.valueOfIgnoreCase("REMOTE_DESTROY")).isNull();
		assertThat(EvictionActionType.valueOfIgnoreCase("All")).isNull();
		assertThat(EvictionActionType.valueOfIgnoreCase(" none  ")).isNull();
		assertThat(EvictionActionType.valueOfIgnoreCase("underflow_from_disk")).isNull();
		assertThat(EvictionActionType.valueOfIgnoreCase("  ")).isNull();
		assertThat(EvictionActionType.valueOfIgnoreCase("")).isNull();
		assertThat(EvictionActionType.valueOfIgnoreCase(null)).isNull();
	}
}
