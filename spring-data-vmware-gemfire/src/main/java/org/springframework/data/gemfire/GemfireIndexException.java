/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to use generic exception types instead of GemFire-specific ones
 */

package org.springframework.data.gemfire;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * Gemfire-specific subclass thrown on Index management.
 *
 * @author Costin Leau
 * @author John Blum
 */
@SuppressWarnings({ "serial", "unused" })
public class GemfireIndexException extends DataIntegrityViolationException {

	public GemfireIndexException(Exception cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, Exception cause) {
		super(message, cause);
	}
}
