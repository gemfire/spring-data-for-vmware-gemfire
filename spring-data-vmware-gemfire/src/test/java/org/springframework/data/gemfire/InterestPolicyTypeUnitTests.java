/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.junit.Test;
import org.springframework.data.gemfire.gud.api.GudInterestPolicy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit Tests for {@link InterestPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see GudInterestPolicy
 * @see org.springframework.data.gemfire.InterestPolicyType
 * @since 1.6.0
 */
public class InterestPolicyTypeUnitTests {

	@Test
	public void testStaticGetInterestPolicy() {

		assertThat(InterestPolicyType.getInterestPolicy(InterestPolicyType.ALL)).isEqualTo(GudInterestPolicy.ALL);
		assertThat(InterestPolicyType.getInterestPolicy(InterestPolicyType.CACHE_CONTENT)).isEqualTo(GudInterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void testStaticGetInterestPolicyWithNull() {
		assertThat(InterestPolicyType.getInterestPolicy(null)).isNull();
	}

	@Test
	public void testGetInterestPolicy() {

		assertThat(InterestPolicyType.ALL.getInterestPolicy()).isEqualTo(GudInterestPolicy.ALL);
		assertThat(InterestPolicyType.CACHE_CONTENT.getInterestPolicy()).isEqualTo(GudInterestPolicy.CACHE_CONTENT);
	}

	@Test
	public void testDefault() {

		assertThat(InterestPolicyType.DEFAULT.getInterestPolicy()).isEqualTo(GudInterestPolicy.DEFAULT);
		assertThat(InterestPolicyType.DEFAULT.getInterestPolicy()).isEqualTo(InterestPolicyType.CACHE_CONTENT.getInterestPolicy());
	}

	@Test
	public void testValueOf() {

		try {
			for (byte ordinal = 0; ordinal < GudInterestPolicy.values().length; ordinal++) {
				GudInterestPolicy interestPolicy = GudInterestPolicy.fromOrdinal(ordinal);
				InterestPolicyType interestPolicyType = InterestPolicyType.valueOf(interestPolicy);

				assertThat(interestPolicyType).isNotNull();
				assertThat(interestPolicyType.getInterestPolicy()).isEqualTo(interestPolicy);
			}
		}
		catch (ArrayIndexOutOfBoundsException ignore) { }
	}

	@Test
	public void testValueOfWithNull() {
		assertThat(InterestPolicyType.valueOf((GudInterestPolicy) null)).isNull();
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
