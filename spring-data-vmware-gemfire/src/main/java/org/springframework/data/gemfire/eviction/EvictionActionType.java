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

import org.springframework.data.gemfire.gud.api.GudEvictionAction;

/**
 * The EvictionActionType enum is an enumeration of all the GemFire EvictionAction values.
 *
 * @author John Blum
 * @see GudEvictionAction
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum EvictionActionType {

	LOCAL_DESTROY(GudEvictionAction.LOCAL_DESTROY),
	NONE(GudEvictionAction.NONE),
	OVERFLOW_TO_DISK(GudEvictionAction.OVERFLOW_TO_DISK);

	public static final EvictionActionType DEFAULT = EvictionActionType.valueOf(GudEvictionAction.DEFAULT);

	private final GudEvictionAction evictionAction;

	/**
	 * Constructs an instance of the EvictionActionType enum initialized with the matching GemFire EvictionAction.
	 *
	 * @param evictionAction the matching GemFire EvictionAction value for this enumerated value.
	 * @see GudEvictionAction
	 */
	EvictionActionType(final GudEvictionAction evictionAction) {
		this.evictionAction = evictionAction;
	}

	/**
	 * A null-safe operation to extract the GemFire EvictionAction from the EvictionActionType enumerated value.
	 *
	 * @param evictionActionType the EvictionActionType enumerated value from which to extract
	 * the matching GemFire EvictionAction value.
	 * @return a GemFire EvictionAction given a EvictionActionType enumerated value.
	 * @see #getEvictionAction()
	 */
	public static GudEvictionAction getEvictionAction(final EvictionActionType evictionActionType) {
		return evictionActionType != null ? evictionActionType.getEvictionAction() : null;
	}

	/**
	 * Returns an EvictionActionType enumerated value matching the given GemFire EvictionAction.
	 *
	 * @param evictionAction the GemFire EvictionAction used to lookup and match the appropriate EvictionActionType.
	 * @return an EvictionActionType enumerated value matching the given GemFire EvictionAction
	 * or null if no match was found.
	 * @see GudEvictionAction
	 * @see #getEvictionAction()
	 */
	public static EvictionActionType valueOf(final GudEvictionAction evictionAction) {

		for (EvictionActionType evictionActionType : values()) {
			if (evictionActionType.getEvictionAction().equals(evictionAction)) {
				return evictionActionType;
			}
		}

		return null;
	}

	/**
	 * Returns an EvictionActionType enumerated value given the named, case-insensitive eviction action.
	 *
	 * @param name a String value indicating the name the eviction action used to match EvictionActionType.
	 * @return an EvictionActionType enumerated value matching the given named, case-insensitive eviction action
	 * or null if not match was found.
	 * @see String#equalsIgnoreCase(String)
	 * @see #name()
	 */
	public static EvictionActionType valueOfIgnoreCase(final String name) {

		for (EvictionActionType evictionActionType : values()) {
			if (evictionActionType.name().equalsIgnoreCase(name)) {
				return evictionActionType;
			}
		}

		return null;
	}

	/**
	 * Gets the matching GemFire EvictionAction represented by this enumerated value.
	 *
	 * @return the GemFire EvictionAction represented by this enum.
	 * @see GudEvictionAction
	 */
	public GudEvictionAction getEvictionAction() {
		return this.evictionAction;
	}
}
