// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.InterestPolicy;

/**
 * Unit Tests for {@link InterestPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.InterestPolicy
 * @see org.springframework.data.gemfire.InterestPolicyType
 * @since 1.6.0
 */
public class InterestPolicyTypeUnitTests {

	@Test
	public void testStaticGetInterestPolicy() {

		assertThat(InterestPolicyType.getInterestPolicy(InterestPolicyType.ALL)).isEqualTo(InterestPolicy.ALL);
		assertThat(InterestPolicyType.getInterestPolicy(InterestPolicyType.CACHE_CONTENT)).isEqualTo(InterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void testStaticGetInterestPolicyWithNull() {
		assertThat(InterestPolicyType.getInterestPolicy(null)).isNull();
	}

	@Test
	public void testGetInterestPolicy() {

		assertThat(InterestPolicyType.ALL.getInterestPolicy()).isEqualTo(InterestPolicy.ALL);
		assertThat(InterestPolicyType.CACHE_CONTENT.getInterestPolicy()).isEqualTo(InterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void testDefault() {

		assertThat(InterestPolicyType.DEFAULT.getInterestPolicy()).isEqualTo(InterestPolicy.DEFAULT);
		assertThat(InterestPolicyType.DEFAULT).isSameAs(InterestPolicyType.CACHE_CONTENT);
	}

	@Test
	public void testValueOf() {

		try {
			for (byte ordinal = 0; ordinal < Byte.MAX_VALUE; ordinal++) {
				InterestPolicy interestPolicy = InterestPolicy.fromOrdinal(ordinal);
				InterestPolicyType interestPolicyType = InterestPolicyType.valueOf(interestPolicy);

				assertThat(interestPolicyType).isNotNull();
				assertThat(interestPolicyType.getInterestPolicy()).isEqualTo(interestPolicy);
			}
		}
		catch (ArrayIndexOutOfBoundsException ignore) { }
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(InterestPolicyType.valueOf((InterestPolicy) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(InterestPolicyType.valueOfIgnoreCase("all")).isEqualTo(InterestPolicyType.ALL);
		assertThat(InterestPolicyType.valueOfIgnoreCase("Cache_Content")).isEqualTo(InterestPolicyType.CACHE_CONTENT);
		assertThat(InterestPolicyType.valueOfIgnoreCase("ALL")).isEqualTo(InterestPolicyType.ALL);
		assertThat(InterestPolicyType.valueOfIgnoreCase("CACHE_ConTent")).isEqualTo(InterestPolicyType.CACHE_CONTENT);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {

		assertThat(InterestPolicyType.valueOfIgnoreCase("@11")).isNull();
		assertThat(InterestPolicyType.valueOfIgnoreCase("CACHE_KEYS")).isNull();
		assertThat(InterestPolicyType.valueOfIgnoreCase("invalid")).isNull();
		assertThat(InterestPolicyType.valueOfIgnoreCase("test")).isNull();
		assertThat(InterestPolicyType.valueOfIgnoreCase("  ")).isNull();
		assertThat(InterestPolicyType.valueOfIgnoreCase("")).isNull();
		assertThat(InterestPolicyType.valueOfIgnoreCase(null)).isNull();
	}
}
