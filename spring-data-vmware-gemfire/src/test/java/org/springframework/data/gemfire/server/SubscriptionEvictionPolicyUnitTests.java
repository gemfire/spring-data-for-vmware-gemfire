/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;

import org.apache.geode.cache.server.ClientSubscriptionConfig;

/**
 * Unit Tests for {@link SubscriptionEvictionPolicy} enum.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see SubscriptionEvictionPolicy
 * @since 1.6.0
 */
public class SubscriptionEvictionPolicyUnitTests {

	@Test
	public void testDefault() {

		assertThat(SubscriptionEvictionPolicy.DEFAULT.name().toLowerCase())
			.isEqualTo(ClientSubscriptionConfig.DEFAULT_EVICTION_POLICY.toLowerCase());

		assertThat(SubscriptionEvictionPolicy.DEFAULT).isSameAs(SubscriptionEvictionPolicy.NONE);
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("entry")).isEqualTo(SubscriptionEvictionPolicy.ENTRY);
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("Mem")).isEqualTo(SubscriptionEvictionPolicy.MEM);
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("NOne")).isEqualTo(SubscriptionEvictionPolicy.NONE);
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("nONE")).isEqualTo(SubscriptionEvictionPolicy.NONE);
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("NONE")).isEqualTo(SubscriptionEvictionPolicy.NONE);
	}

	@Test
	public void testValueOfIgnoreCaseWithIllegalValue() {

		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("KEYS")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("Memory")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("all")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("no")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("one")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("  ")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase("")).isNull();
		assertThat(SubscriptionEvictionPolicy.valueOfIgnoreCase(null)).isNull();
	}

	@Test
	public void testSetEvictionPolicy() {

		ClientSubscriptionConfig mockConfig =
			mock(ClientSubscriptionConfig.class,"testSetEvictionPolicy.ClientSubscriptionConfig");

		ClientSubscriptionConfig returnedConfig = SubscriptionEvictionPolicy.MEM.setEvictionPolicy(mockConfig);

		assertThat(returnedConfig).isSameAs(mockConfig);

		verify(mockConfig, times(1)).setEvictionPolicy(eq("mem"));
	}

	@Test
	public void testSetEvictionPolicyWithNull() {
		assertThat(SubscriptionEvictionPolicy.ENTRY.setEvictionPolicy(null)).isNull();
	}
}
