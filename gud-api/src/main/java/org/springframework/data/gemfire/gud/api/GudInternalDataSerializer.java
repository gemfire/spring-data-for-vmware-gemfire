/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GUD API abstraction for InternalDataSerializer
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for internal data serializer operations.
 *
 * @author Cursor AI
 * @since 1.0.0
 */
public abstract class GudInternalDataSerializer {

	/**
	 * Gets a registered serializer by ID.
	 *
	 * @param id the serializer ID
	 * @return the serializer, or null if not found
	 */
	public static GudDataSerializer getSerializer(int id) {
		throw new UnsupportedOperationException("Must be implemented by driver");
	}

	/**
	 * Unregisters a serializer by ID.
	 *
	 * @param id the serializer ID to unregister
	 */
	public static void unregister(int id) {
		throw new UnsupportedOperationException("Must be implemented by driver");
	}
}
