/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.wan;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import org.apache.geode.cache.wan.GatewaySender;

/**
 * Unit Tests for {@link OrderPolicyType} enum.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
 * @see org.springframework.data.gemfire.wan.OrderPolicyType
 * @since 1.7.0
 */
public class OrderPolicyTypeUnitTests {

	@Test
	public void testStaticGetOrderPolicy() {

		assertThat(OrderPolicyType.getOrderPolicy(OrderPolicyType.KEY)).isEqualTo(GatewaySender.OrderPolicy.KEY);
		assertThat(OrderPolicyType.getOrderPolicy(OrderPolicyType.PARTITION))
			.isEqualTo(GatewaySender.OrderPolicy.PARTITION);
	}

	@Test
	public void testStaticGetOrderPolicyWithNull() {
		assertThat(OrderPolicyType.getOrderPolicy(null)).isNull();
	}

	@Test
	public void testValueOfGemFireOrderPolicies() {

		for (GatewaySender.OrderPolicy orderPolicy : GatewaySender.OrderPolicy.values()) {

			OrderPolicyType orderPolicyType = OrderPolicyType.valueOf(orderPolicy);

			assertThat(orderPolicyType).isNotNull();
			assertThat(orderPolicyType.getOrderPolicy()).isEqualTo(orderPolicy);
		}
	}

	@Test
	public void testValueOfNullGemFireOrderPolicy() {
		assertThat(OrderPolicyType.valueOf((GatewaySender.OrderPolicy) null)).isNull();
	}

	@Test
	public void testValueOfIgnoreCase() {

		assertThat(OrderPolicyType.valueOfIgnoreCase("KEY")).isEqualTo(OrderPolicyType.KEY);
		assertThat(OrderPolicyType.valueOfIgnoreCase("Partition")).isEqualTo(OrderPolicyType.PARTITION);
		assertThat(OrderPolicyType.valueOfIgnoreCase("PARTition")).isEqualTo(OrderPolicyType.PARTITION);
		assertThat(OrderPolicyType.valueOfIgnoreCase("PartItIon")).isEqualTo(OrderPolicyType.PARTITION);
		assertThat(OrderPolicyType.valueOfIgnoreCase("thread")).isEqualTo(OrderPolicyType.THREAD);
	}

	@Test
	public void testValueOfIgnoreCaseWithInvalidValues() {

		assertThat(OrderPolicyType.valueOfIgnoreCase("KEYZ")).isNull();
		assertThat(OrderPolicyType.valueOfIgnoreCase("Values")).isNull();
		assertThat(OrderPolicyType.valueOfIgnoreCase("invalid")).isNull();
	}
}
