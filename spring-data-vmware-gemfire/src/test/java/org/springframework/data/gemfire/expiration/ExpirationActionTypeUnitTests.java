/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.expiration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudExpirationAction;

/**
 * Unit Tests for {@link ExpirationActionType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see ExpirationActionType
 * @see org.apache.geode.cache.GudExpirationAction
 * @since 1.6.0
 */
public class ExpirationActionTypeUnitTests {

	@Test
	public void testStaticGetExpirationAction() {

		assertThat(ExpirationActionType.getExpirationAction(ExpirationActionType.DESTROY))
			.isEqualTo(GudExpirationAction.DESTROY);

		assertThat(ExpirationActionType.getExpirationAction(
			ExpirationActionType.LOCAL_DESTROY)).isEqualTo(GudExpirationAction.LOCAL_DESTROY);
	}

	@Test
	public void testStaticGetExpirationActionWithNull() {
		assertThat(ExpirationActionType.getExpirationAction(null)).isNull();
	}

	@Test
	public void testGetExpirationAction() {

		assertThat(ExpirationActionType.DESTROY.getExpirationAction()).isEqualTo(GudExpirationAction.DESTROY);
		assertThat(ExpirationActionType.INVALIDATE.getExpirationAction()).isEqualTo(GudExpirationAction.INVALIDATE);
		assertThat(ExpirationActionType.LOCAL_DESTROY.getExpirationAction()).isEqualTo(GudExpirationAction.LOCAL_DESTROY);
		assertThat(ExpirationActionType.LOCAL_INVALIDATE.getExpirationAction()).isEqualTo(GudExpirationAction.LOCAL_INVALIDATE);
	}

	@Test
	public void testDefault() {
		assertThat(ExpirationActionType.DEFAULT.getExpirationAction()).isEqualTo(GudExpirationAction.INVALIDATE);

		assertThat(ExpirationActionType.DEFAULT).isSameAs(ExpirationActionType.INVALIDATE);
	}

	@Test
	public void testValueOf() {

		assertThat(ExpirationActionType.valueOf(GudExpirationAction.DESTROY)).isEqualTo(ExpirationActionType.DESTROY);
		assertThat(ExpirationActionType.valueOf(GudExpirationAction.INVALIDATE)).isEqualTo(ExpirationActionType.INVALIDATE);
		assertThat(ExpirationActionType.valueOf(GudExpirationAction.LOCAL_DESTROY)).isEqualTo(ExpirationActionType.LOCAL_DESTROY);
		assertThat(ExpirationActionType.valueOf(GudExpirationAction.LOCAL_INVALIDATE)).isEqualTo(ExpirationActionType.LOCAL_INVALIDATE);
	}

	@Test
	public void testValueOfExpirationActionOrdinalValues() {

		try {
			for (int ordinal = 0; ordinal < Integer.MAX_VALUE; ordinal++) {

				GudExpirationAction expirationAction = GudExpirationAction.fromOrdinal(ordinal);
				ExpirationActionType expirationActionType = ExpirationActionType.valueOf(expirationAction);

				assertThat(expirationActionType).isNotNull();
				assertThat(expirationActionType.getExpirationAction()).isEqualTo(expirationAction);
			}
		}
		catch (ArrayIndexOutOfBoundsException ignore) {
		}
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(ExpirationActionType.valueOf((GudExpirationAction) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(ExpirationActionType.valueOfIgnoreCase("destroy")).isEqualTo(ExpirationActionType.DESTROY);
		assertThat(ExpirationActionType.valueOfIgnoreCase("Invalidate")).isEqualTo(ExpirationActionType.INVALIDATE);
		assertThat(ExpirationActionType.valueOfIgnoreCase("LOCAL_DESTROY")).isEqualTo(ExpirationActionType.LOCAL_DESTROY);
		assertThat(ExpirationActionType.valueOfIgnoreCase("LocaL_InValidAte")).isEqualTo(ExpirationActionType.LOCAL_INVALIDATE);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {

		assertThat(ExpirationActionType.valueOfIgnoreCase("Invalid")).isNull();
		assertThat(ExpirationActionType.valueOfIgnoreCase("local destroy")).isNull();
		assertThat(ExpirationActionType.valueOfIgnoreCase("  ")).isNull();
		assertThat(ExpirationActionType.valueOfIgnoreCase("")).isNull();
		assertThat(ExpirationActionType.valueOfIgnoreCase(null)).isNull();
	}
}
