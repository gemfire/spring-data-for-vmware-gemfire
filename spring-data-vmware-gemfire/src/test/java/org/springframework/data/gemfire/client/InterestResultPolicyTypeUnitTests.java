/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudInterestResultPolicy;

/**
 * Unit Tests for {@link InterestResultPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GudInterestResultPolicy
 * @see org.springframework.data.gemfire.client.InterestResultPolicyTypeUnitTests
 * @since 1.6.0
 */
public class InterestResultPolicyTypeUnitTests {

	@Test
	public void testStaticGetInterestResultPolicy() {

		assertThat(InterestResultPolicyType.getInterestResultPolicy(InterestResultPolicyType.KEYS)).isEqualTo(GudInterestResultPolicy.KEYS);
		assertThat(InterestResultPolicyType.getInterestResultPolicy(InterestResultPolicyType.KEYS_VALUES)).isEqualTo(GudInterestResultPolicy.KEYS_VALUES);
	}

	@Test
	public void testStaticGetInterestResultPolicyWithNull() {
		assertThat(InterestResultPolicyType.getInterestResultPolicy(null)).isNull();
	}

	@Test
	public void testDefault() {

		assertThat(InterestResultPolicyType.valueOf(GudInterestResultPolicy.DEFAULT)).isEqualTo(InterestResultPolicyType.DEFAULT);
		assertThat(InterestResultPolicyType.DEFAULT.getInterestResultPolicy()).isEqualTo(GudInterestResultPolicy.DEFAULT);
		assertThat(InterestResultPolicyType.DEFAULT).isSameAs(InterestResultPolicyType.KEYS_VALUES);
	}

	@Test
	public void testValueOf() {

		try {
			for (byte ordinal = 0; ordinal < Byte.MAX_VALUE; ordinal++) {

				GudInterestResultPolicy interestResultPolicy = GudInterestResultPolicy.fromOrdinal(ordinal);

				InterestResultPolicyType interestResultPolicyType =
					InterestResultPolicyType.valueOf(interestResultPolicy);

				assertThat(interestResultPolicyType).isNotNull();
				assertThat(interestResultPolicyType.getInterestResultPolicy()).isEqualTo(interestResultPolicy);
			}
		}
		catch (ArrayIndexOutOfBoundsException ignore) {
		}
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(InterestResultPolicyType.valueOf((GudInterestResultPolicy) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(InterestResultPolicyType.valueOfIgnoreCase("KEYS")).isEqualTo(InterestResultPolicyType.KEYS);
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("Keys_Values")).isEqualTo(InterestResultPolicyType.KEYS_VALUES);
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("none")).isEqualTo(InterestResultPolicyType.NONE);
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("nONE")).isEqualTo(InterestResultPolicyType.NONE);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("keyz")).isNull();

		assertThat(InterestResultPolicyType.valueOfIgnoreCase("KEY_VALUE")).isNull();
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("all")).isNull();
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("  ")).isNull();
		assertThat(InterestResultPolicyType.valueOfIgnoreCase("")).isNull();
		assertThat(InterestResultPolicyType.valueOfIgnoreCase(null)).isNull();
	}
}
