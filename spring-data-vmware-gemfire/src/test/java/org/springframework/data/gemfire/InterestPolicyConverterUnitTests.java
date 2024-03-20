/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.InterestPolicy;

/**
 * Unit Tests for {@link InterestPolicyConverter}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.InterestPolicy
 * @see org.springframework.data.gemfire.InterestPolicyConverter
 * @since 1.6.0
 */
public class InterestPolicyConverterUnitTests {

	private final InterestPolicyConverter converter = new InterestPolicyConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("all")).isEqualTo(InterestPolicy.ALL);
		assertThat(converter.convert("Cache_Content")).isEqualTo(InterestPolicy.CACHE_CONTENT);
		assertThat(converter.convert("CACHE_ConTent")).isEqualTo(InterestPolicy.CACHE_CONTENT);
		assertThat(converter.convert("ALL")).isEqualTo(InterestPolicy.ALL);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.convert("invalid_value");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[invalid_value] is not a valid InterestPolicy");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("aLl");

		assertThat(converter.getValue()).isEqualTo(InterestPolicy.ALL);

		converter.setAsText("Cache_CoNTeNT");

		assertThat(converter.getValue()).isEqualTo(InterestPolicy.CACHE_CONTENT);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithInvalidValue() {

		try {
			converter.setAsText("none");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[none] is not a valid InterestPolicy");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
