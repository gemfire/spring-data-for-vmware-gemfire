/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

/**
 * Unit tests for {@link SubscriptionEvictionPolicyConverter}.
 *
 * @author John Blum
 * @see Test
 * @see SubscriptionEvictionPolicy
 * @see SubscriptionEvictionPolicyConverter
 * @since 1.6.0
 */
public class SubscriptionEvictionPolicyConverterUnitTests {

	private final SubscriptionEvictionPolicyConverter converter = new SubscriptionEvictionPolicyConverter();

	@After
	public void tearDown() {
		converter.setValue(null);
	}

	@Test
	public void convert() {

		assertThat(converter.convert("EnTry")).isEqualTo(SubscriptionEvictionPolicy.ENTRY);
		assertThat(converter.convert("MEM")).isEqualTo(SubscriptionEvictionPolicy.MEM);
		assertThat(converter.convert("nONE")).isEqualTo(SubscriptionEvictionPolicy.NONE);
		assertThat(converter.convert("NOne")).isEqualTo(SubscriptionEvictionPolicy.NONE);
	}

	@Test(expected = IllegalArgumentException.class)
	public void convertIllegalValue() {

		try {
			converter.setAsText("memory");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[memory] is not a valid SubscriptionEvictionPolicy");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void setAsText() {

		assertThat(converter.getValue()).isNull();

		converter.setAsText("enTRY");

		assertThat(converter.getValue()).isEqualTo(SubscriptionEvictionPolicy.ENTRY);

		converter.setAsText("MEm");

		assertThat(converter.getValue()).isEqualTo(SubscriptionEvictionPolicy.MEM);
	}

	@Test(expected = IllegalArgumentException.class)
	public void setAsTextWithIllegalValue() {

		try {
			converter.setAsText("KEYS");
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("[KEYS] is not a valid SubscriptionEvictionPolicy");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}
}
