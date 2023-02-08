/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.InterestResultPolicy;

/**
 * Unit Tests for {@link InterestResultPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see InterestResultPolicy
 * @see InterestResultPolicyTypeUnitTests
 * @since 1.6.0
 */
public class InterestResultPolicyTypeUnitTests {

	@Test
	public void testStaticGetInterestResultPolicy() {

		assertThat(InterestResultPolicyType.getInterestResultPolicy(InterestResultPolicyType.KEYS)).isEqualTo(InterestResultPolicy.KEYS);
		assertThat(InterestResultPolicyType.getInterestResultPolicy(InterestResultPolicyType.KEYS_VALUES)).isEqualTo(InterestResultPolicy.KEYS_VALUES);
	}

	@Test
	public void testStaticGetInterestResultPolicyWithNull() {
		assertThat(InterestResultPolicyType.getInterestResultPolicy(null)).isNull();
	}

	@Test
	public void testDefault() {

		assertThat(InterestResultPolicyType.valueOf(InterestResultPolicy.DEFAULT)).isEqualTo(InterestResultPolicyType.DEFAULT);
		assertThat(InterestResultPolicyType.DEFAULT.getInterestResultPolicy()).isEqualTo(InterestResultPolicy.DEFAULT);
		assertThat(InterestResultPolicyType.DEFAULT).isSameAs(InterestResultPolicyType.KEYS_VALUES);
	}

	@Test
	public void testValueOf() {

		try {
			for (byte ordinal = 0; ordinal < Byte.MAX_VALUE; ordinal++) {

				InterestResultPolicy interestResultPolicy = InterestResultPolicy.fromOrdinal(ordinal);

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
		assertThat(InterestResultPolicyType.valueOf((InterestResultPolicy) null)).isNull();
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
