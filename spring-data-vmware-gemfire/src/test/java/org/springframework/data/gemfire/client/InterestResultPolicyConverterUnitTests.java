/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.InterestResultPolicy;

/**
 * Unit Tests for {@link InterestResultPolicyConverter}.
 *
 * @author John Blum
 * @see Test
 * @see InterestResultPolicy
 * @see InterestResultPolicyConverter
 * @see InterestResultPolicyType
 * @since 1.6.0
 */
public class InterestResultPolicyConverterUnitTests {

	private final InterestResultPolicyConverter converter = new InterestResultPolicyConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("NONE")).isEqualTo(InterestResultPolicy.NONE);
		assertThat(converter.convert("kEyS_ValUes")).isEqualTo(InterestResultPolicy.KEYS_VALUES);
		assertThat(converter.convert("nONe")).isEqualTo(InterestResultPolicy.NONE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.convert("illegal_value");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[illegal_value] is not a valid InterestResultPolicy");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("NOne");

		assertThat(converter.getValue()).isEqualTo(InterestResultPolicy.NONE);

		converter.setAsText("KeYs");

		assertThat(converter.getValue()).isEqualTo(InterestResultPolicy.KEYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("illegal_value");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[illegal_value] is not a valid InterestResultPolicy");
			assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			assertThat(converter.getValue()).isNull();
		}
	}
}
