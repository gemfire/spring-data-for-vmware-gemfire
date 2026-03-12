/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.eviction;

import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;

/**
 * The EvictionPolicyType enum is an enumeration of all GemFire Eviction policies, where the Eviction 'policy'
 * is a combination of the Eviction algorithm mixed with the monitored resource (e.g. such as JVM HEAP memory).
 *
 * @author Costin Leau
 * @author John Blum
 * @see GudEvictionAlgorithm
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public enum EvictionPolicyType {

	ENTRY_COUNT(GudEvictionAlgorithm.LRU_ENTRY),
	HEAP_PERCENTAGE(GudEvictionAlgorithm.LRU_HEAP),
	MEMORY_SIZE(GudEvictionAlgorithm.LRU_MEMORY),
	NONE(GudEvictionAlgorithm.NONE);

	private final GudEvictionAlgorithm evictionAlgorithm;

	/**
	 * Constructs an instance of the EvictionPolicyType enum initialized with the matching GemFire EvictionAlgorithm.
	 *
	 * @param evictionAlgorithm the GemFire EvictionAlgorithm represented by this EvictionPolicyType enumerated value.
	 * @see GudEvictionAlgorithm
	 */
	EvictionPolicyType(final GudEvictionAlgorithm evictionAlgorithm) {
		this.evictionAlgorithm = evictionAlgorithm;
	}

	/**
	 * A null-safe operation to extract the GemFire EvictionAlgorithm from the given EvictionPolicyType.
	 *
	 * @param evictionPolicyType the EvictionPolicyType from which to extract the GemFire EvictionAlgorithm.
	 * @return the GemFire EvictionAlgorithm for the corresponding EvictionPolicyType or null if evictionType is null.
	 * @see GudEvictionAlgorithm
	 * @see #getEvictionAlgorithm()
	 */
	public static GudEvictionAlgorithm getEvictionAlgorithm(final EvictionPolicyType evictionPolicyType) {
		return evictionPolicyType != null ? evictionPolicyType.getEvictionAlgorithm() : null;
	}

	/**
	 * Returns an EvictionPolicyType enumerated value matching the given GemFire EvictionAlgorithm.
	 *
	 * @param evictionAlgorithm the GemFire EvictionAlgorithm used to lookup and match the EvictionPolicyType.
	 * @return an EvictionPolicyType matching the specified GemFire EvictionAlgorithm or null if no match was found.
	 * @see GudEvictionAlgorithm
	 * @see #getEvictionAlgorithm()
	 */
	public static EvictionPolicyType valueOf(final GudEvictionAlgorithm evictionAlgorithm) {

		for (EvictionPolicyType evictionPolicyType : values()) {
			if (evictionPolicyType.getEvictionAlgorithm().equals(evictionAlgorithm)) {
				return evictionPolicyType;
			}
		}

		return null;
	}

	/**
	 * Returns an EvictionPolicyType enumerated value given the case-insensitive, named eviction policy.
	 *
	 * @param name a String indicating the name of the eviction policy used to match EvictionPolicyType.
	 * @return an EvictionPolicyType matching the given the case-insensitive, named eviction policy.
	 * @see String#equalsIgnoreCase(String)
	 * @see #name()
	 */
	public static EvictionPolicyType valueOfIgnoreCase(final String name) {

		for (EvictionPolicyType evictionPolicyType : values()) {
			if (evictionPolicyType.name().equalsIgnoreCase(name)) {
				return evictionPolicyType;
			}
		}

		return null;
	}

	/**
	 * Gets the GemFire EvictionAlgorithm represented by this enumerated value.
	 *
	 * @return the GemFire EvictionAlgorithm represented by this enum.
	 * @see GudEvictionAlgorithm
	 */
	public GudEvictionAlgorithm getEvictionAlgorithm() {
		return this.evictionAlgorithm;
	}
}
