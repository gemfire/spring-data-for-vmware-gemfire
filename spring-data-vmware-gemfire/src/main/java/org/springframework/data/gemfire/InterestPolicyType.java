/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to GUD API types
 */

package org.springframework.data.gemfire;

import org.springframework.data.gemfire.gud.api.GudInterestPolicy;

/**
 * The InterestPolicyType enum is an enumeration of all the GemFire Subscription, InterestPolicy values.
 *
 * @author Lyndon Adams
 * @author John Blum
 * @see GudInterestPolicy
 * @since 1.3.0
 */
@SuppressWarnings("unused")
public enum InterestPolicyType {
	ALL(GudInterestPolicy.ALL),
	CACHE_CONTENT(GudInterestPolicy.CACHE_CONTENT);

	public static final InterestPolicyType DEFAULT = InterestPolicyType.valueOf(GudInterestPolicy.DEFAULT);

	private final GudInterestPolicy interestPolicy;

	/**
	 * Constructs an instance of the SubscriptionType enum initialized with the matching GemFire InterestPolicy.
	 *
	 * @param interestPolicy a GemFire InterestPolicy corresponding to this SubscriptionType.
	 * @see GudInterestPolicy
	 */
	InterestPolicyType(final GudInterestPolicy interestPolicy) {
		this.interestPolicy = interestPolicy;
	}

	/**
	 * Null-safe operation to extract the GemFire InterestPolicy from the InterPolicyType enumerated value.
	 *
	 * @param interestPolicyType the InterestPolicyType enum from which to extract GemFire's InterestPolicy
	 * @return a GemFire InterestPolicy for the given InterestPolicyType enumerated value
	 * or null if InterestPolicyType is null.
	 * @see GudInterestPolicy
	 */
	public static GudInterestPolicy getInterestPolicy(final InterestPolicyType interestPolicyType) {
		return (interestPolicyType != null ? interestPolicyType.getInterestPolicy() : null);
	}

	/**
	 * Returns a SubscriptionType enumerated value for the given GemFire InterestPolicy.
	 *
	 * @param interestPolicy the GemFire InterestPolicy used to lookup and match a SubscriptionType.
	 * @return a SubscriptionType enumerated value matching the given GemFire InterestPolicy
	 * or null if no matching value was found.
	 * @see GudInterestPolicy
	 * @see #getInterestPolicy()
	 */
	public static InterestPolicyType valueOf(final GudInterestPolicy interestPolicy) {
		for (InterestPolicyType interestPolicyType : values()) {
			if (interestPolicyType.getInterestPolicy().equals(interestPolicy)) {
				return interestPolicyType;
			}
		}

		return null;
	}

	/**
	 * Returns a SubscriptionType enumerated value for the case-insensitive, named Subscription (InterestsPolicy).
	 *
	 * @param value a String name used to look and match the SubscriptionType.
	 * @return a SubscriptionType enumerated value for the given case-insensitive named Subscription
	 * or null if no match was found.
	 * @see String#equalsIgnoreCase(String)
	 * @see #name()
	 */
	public static InterestPolicyType valueOfIgnoreCase(final String value) {
		for (InterestPolicyType interestPolicyType : values()) {
			if (interestPolicyType.name().equalsIgnoreCase(value)) {
				return interestPolicyType;
			}
		}

		return null;
	}

	/**
	 * Returns the GemFire InterestPolicy corresponding to this SubscriptionType enumerated value.
	 *
	 * @return the GemFire InterestPolicy corresponding to this SubscriptionType.
	 * @see GudInterestPolicy
	 */
	public GudInterestPolicy getInterestPolicy() {
		return interestPolicy;
	}

}
