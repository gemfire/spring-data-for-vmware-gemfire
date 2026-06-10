/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 * 2026-06-06: DEFAULT constant changed to direct alias of KEYS_VALUES (matches native InterestResultPolicy.DEFAULT)
 */

package org.springframework.data.gemfire.client;

import org.springframework.data.gemfire.gud.api.GudInterestResultPolicy;

/**
 * The InterestResultPolicyType enum is an enumeration of all client Register Interests (result) policy values.
 *
 * @author John Blum
 * @see GudInterestResultPolicy
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum InterestResultPolicyType {
	KEYS(GudInterestResultPolicy.KEYS),
	KEYS_VALUES(GudInterestResultPolicy.KEYS_VALUES),
	NONE(GudInterestResultPolicy.NONE);

	public static final InterestResultPolicyType DEFAULT = InterestResultPolicyType.KEYS_VALUES;

	private final GudInterestResultPolicy interestResultPolicy;

	InterestResultPolicyType(final GudInterestResultPolicy interestResultPolicy) {
		this.interestResultPolicy = interestResultPolicy;
	}

	public static GudInterestResultPolicy getInterestResultPolicy(final InterestResultPolicyType interestResultPolicyType) {
		return (interestResultPolicyType != null ? interestResultPolicyType.getInterestResultPolicy() : null);
	}

	public static InterestResultPolicyType valueOf(final GudInterestResultPolicy interestResultPolicy) {
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

	public GudInterestResultPolicy getInterestResultPolicy() {
		return interestResultPolicy;
	}

}
