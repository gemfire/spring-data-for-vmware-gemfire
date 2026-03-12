/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to use generic Exception types for GUD compatibility
 */

package org.springframework.data.gemfire;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * GemFire-specific subclass of {@link InvalidDataAccessResourceUsageException} thrown on invalid
 * OQL query syntax.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireQueryException extends InvalidDataAccessResourceUsageException {

	public GemfireQueryException(String message, Exception ex) {
		super(message, ex);
	}

	public GemfireQueryException(Exception ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireQueryException(String message, RuntimeException ex) {
		super(message, ex);
	}

	public GemfireQueryException(RuntimeException ex) {
		super(ex.getMessage(), ex);
	}
}
