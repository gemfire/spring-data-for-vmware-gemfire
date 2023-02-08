/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.eviction;

import org.apache.geode.cache.EvictionAction;

/**
 * The EvictionActionType enum is an enumeration of all the GemFire EvictionAction values.
 *
 * @author John Blum
 * @see EvictionAction
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum EvictionActionType {

	LOCAL_DESTROY(EvictionAction.LOCAL_DESTROY),
	NONE(EvictionAction.NONE),
	OVERFLOW_TO_DISK(EvictionAction.OVERFLOW_TO_DISK);

	public static final EvictionActionType DEFAULT = EvictionActionType.valueOf(EvictionAction.DEFAULT_EVICTION_ACTION);

	private final EvictionAction evictionAction;

	/**
	 * Constructs an instance of the EvictionActionType enum initialized with the matching GemFire EvictionAction.
	 *
	 * @param evictionAction the matching GemFire EvictionAction value for this enumerated value.
	 * @see EvictionAction
	 */
	EvictionActionType(final EvictionAction evictionAction) {
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
	public static EvictionAction getEvictionAction(final EvictionActionType evictionActionType) {
		return evictionActionType != null ? evictionActionType.getEvictionAction() : null;
	}

	/**
	 * Returns an EvictionActionType enumerated value matching the given GemFire EvictionAction.
	 *
	 * @param evictionAction the GemFire EvictionAction used to lookup and match the appropriate EvictionActionType.
	 * @return an EvictionActionType enumerated value matching the given GemFire EvictionAction
	 * or null if no match was found.
	 * @see EvictionAction
	 * @see #getEvictionAction()
	 */
	public static EvictionActionType valueOf(final EvictionAction evictionAction) {

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
	 * @see EvictionAction
	 */
	public EvictionAction getEvictionAction() {
		return this.evictionAction;
	}
}
