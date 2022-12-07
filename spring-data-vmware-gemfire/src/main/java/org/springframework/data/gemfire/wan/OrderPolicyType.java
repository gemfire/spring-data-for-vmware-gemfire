/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.wan;

import org.apache.geode.cache.wan.GatewaySender;

/**
 * The OrderPolicyType class is an enumeration of GemFire Gateway Order Policies.
 *
 * @author John Blum
 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public enum OrderPolicyType {
	KEY(GatewaySender.OrderPolicy.KEY),
	PARTITION(GatewaySender.OrderPolicy.PARTITION),
	THREAD(GatewaySender.OrderPolicy.THREAD);

	private final GatewaySender.OrderPolicy orderPolicy;

	/**
	 * Constructs an instance of the OrderPolicyType enum initialized with the matching GemFire Gateway.OrderPolicy
	 * enumerated value.
	 *
	 * @param orderPolicy the matching GemFire Gateway.OrderPolicy enumerated value.
	 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
	 */
	OrderPolicyType(final GatewaySender.OrderPolicy orderPolicy) {
		this.orderPolicy = orderPolicy;
	}

	/**
	 * Null-safe operation to extract the matching GemFire Gateway.OrderPolicy enumerated value from
	 * the specified OrderPolicyType.
	 *
	 * @param orderPolicyType the OrderPolicyType enum from which to extract the GemFire-based
	 * Gateway.OrderPolicy enumerated value.
	 * @return the GemFire Gateway.OrderPolicy enumerated value for the given OrderPolicyType.
	 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
	 * @see #getOrderPolicy()
	 */
	public static GatewaySender.OrderPolicy getOrderPolicy(final OrderPolicyType orderPolicyType) {
		return (orderPolicyType != null ? orderPolicyType.getOrderPolicy() : null);
	}

	/**
	 * Returns the matching OrderPolicyType given a GemFire Gateway.OrderPolicy enumerated value.
	 *
	 * @param orderPolicy the GemFire Gateway.OrderPolicy enumerated value used to match
	 * the desired OrderPolicyType.
	 * @return a OrderPolicyType matching the given GemFire Gateway.OrderPolicy enumerated value.
	 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
	 * @see #getOrderPolicy()
	 */
	public static OrderPolicyType valueOf(final GatewaySender.OrderPolicy orderPolicy) {
		for (OrderPolicyType orderPolicyType : values()) {
			if (orderPolicyType.getOrderPolicy().equals(orderPolicy)) {
				return orderPolicyType;
			}
		}

		return null;
	}

	/**
	 * Returns a matching OrderPolicyType given the case-insensitive, name of the GemFire Gateway OrderPolicy.
	 *
	 * @param name a String name used to match the desired OrderPolicyType.
	 * @return a OrderPolicyType enumerated value for the given name.
	 * @see java.lang.String#equalsIgnoreCase(String)
	 * @see #name()
	 */
	public static OrderPolicyType valueOfIgnoreCase(final String name) {
		for (OrderPolicyType orderPolicy : values()) {
			if (orderPolicy.name().equalsIgnoreCase(name)) {
				return orderPolicy;
			}
		}

		return null;
	}

	/**
	 * Gets the GemFire Gateway.OrderPolicy corresponding to this OrderPolicyType enum.
	 *
	 * @return a GemFire Gateway.OrderPolicy for this enum.
	 * @see org.apache.geode.cache.wan.GatewaySender.OrderPolicy
	 */
	public GatewaySender.OrderPolicy getOrderPolicy() {
		return orderPolicy;
	}

}
