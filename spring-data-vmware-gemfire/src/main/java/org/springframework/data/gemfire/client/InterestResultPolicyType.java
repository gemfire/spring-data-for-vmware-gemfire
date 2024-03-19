/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.client;

import org.apache.geode.cache.InterestResultPolicy;

/**
 * The InterestResultPolicyType enum is an enumeration of all client Register Interests (result) policy values.
 *
 * @author John Blum
 * @see org.apache.geode.cache.InterestResultPolicy
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum InterestResultPolicyType {
	KEYS(InterestResultPolicy.KEYS),
	KEYS_VALUES(InterestResultPolicy.KEYS_VALUES),
	NONE(InterestResultPolicy.NONE);

	public static final InterestResultPolicyType DEFAULT = InterestResultPolicyType.valueOf(
		InterestResultPolicy.DEFAULT);

	private final InterestResultPolicy interestResultPolicy;

	InterestResultPolicyType(final InterestResultPolicy interestResultPolicy) {
		this.interestResultPolicy = interestResultPolicy;
	}

	public static InterestResultPolicy getInterestResultPolicy(final InterestResultPolicyType interestResultPolicyType) {
		return (interestResultPolicyType != null ? interestResultPolicyType.getInterestResultPolicy() : null);
	}

	public static InterestResultPolicyType valueOf(final InterestResultPolicy interestResultPolicy) {
		for (InterestResultPolicyType interestResultPolicyType : values()) {
			if (interestResultPolicyType.getInterestResultPolicy().equals(interestResultPolicy)) {
				return interestResultPolicyType;
			}
		}

		return null;
	}

	public static InterestResultPolicyType valueOfIgnoreCase(final String name) {
		for (InterestResultPolicyType interestResultPolicyType : values()) {
			if (interestResultPolicyType.name().equalsIgnoreCase(name)) {
				return interestResultPolicyType;
			}
		}

		return null;
	}

	public InterestResultPolicy getInterestResultPolicy() {
		return interestResultPolicy;
	}

}
