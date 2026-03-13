/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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

package org.springframework.data.gemfire.expiration;

import org.springframework.data.gemfire.gud.api.GudExpirationAction;

/**
 * The ExpirationActionType enum is a enumeration of GemFire ExpirationActions on expired Cache Region entries.
 *
 * @author John Blum
 * @see GudExpirationAction
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum ExpirationActionType {

	DESTROY(GudExpirationAction.DESTROY),
	INVALIDATE(GudExpirationAction.INVALIDATE),
	LOCAL_DESTROY(GudExpirationAction.LOCAL_DESTROY),
	LOCAL_INVALIDATE(GudExpirationAction.LOCAL_INVALIDATE);

	public static final ExpirationActionType DEFAULT = ExpirationActionType.INVALIDATE;

	private final GudExpirationAction expirationAction;

	/**
	 * Constructs an instance of the ExpirationActionType enum initialized with the matching GemFire ExpirationAction.
	 *
	 * @param expirationAction the matching GemFire ExpirationAction for this enumerated value.
	 * @see GudExpirationAction
	 */
	ExpirationActionType(final GudExpirationAction expirationAction) {
		this.expirationAction = expirationAction;
	}

	/**
	 * A null-safe operation to extract the corresponding GemFire ExpirationAction for the ExpirationActionType.
	 *
	 * @param expirationActionType the ExpirationActionType enumerated value from which to extract
	 * the corresponding GemFire ExpirationAction.
	 * @return a GemFire ExpirationAction given the ExpirationActionType enumerated value.
	 * @see GudExpirationAction
	 */
	public static GudExpirationAction getExpirationAction(final ExpirationActionType expirationActionType) {
		return expirationActionType != null ? expirationActionType.getExpirationAction() : null;
	}

	/**
	 * Returns the ExpirationActionType enumerated value matching the given GemFire ExpirationAction.
	 *
	 * @param expirationAction the GemFire ExpirationAction used to match the ExpirationActionType.
	 * @return a matching ExpirationActionType enumerated value given a GemFire ExpirationAction
	 * or null if no match was found.
	 * @see GudExpirationAction
	 * @see #getExpirationAction()
	 */
	public static ExpirationActionType valueOf(final GudExpirationAction expirationAction) {

		for (ExpirationActionType expirationActionType : values()) {
			if (expirationActionType.getExpirationAction().equals(expirationAction)) {
				return expirationActionType;
			}
		}

		return null;
	}

	/**
	 * Returns an ExpirationActionType enumerated value given a named, case-insensitive expiration action.
	 *
	 * @param name a String name for the expiration action matching the ExpirationActionType.
	 * @return a matching ExpirationActionType for the named, case-insensitive expiration action
	 * or null if no match could be found.
	 * @see String#equalsIgnoreCase(String)
	 * @see #name()
	 */
	public static ExpirationActionType valueOfIgnoreCase(final String name) {

		for (ExpirationActionType expirationActionType : values()) {
			if (expirationActionType.name().equalsIgnoreCase(name)) {
				return expirationActionType;
			}
		}

		return null;
	}

	/**
	 * Gets the matching GemFire ExpirationAction for this enumerated value.
	 *
	 * @return the GemFire ExpirationAction instance corresponding to this enumerated value.
	 * @see GudExpirationAction
	 */
	public GudExpirationAction getExpirationAction() {
		return this.expirationAction;
	}

	/**
	 * Gets the GUD API ExpirationAction for this enumerated value.
	 *
	 * @return the GudExpirationAction instance corresponding to this enumerated value.
	 * @see GudExpirationAction
	 */
	public GudExpirationAction getGudExpirationAction() {
		return this.expirationAction;
	}
}
