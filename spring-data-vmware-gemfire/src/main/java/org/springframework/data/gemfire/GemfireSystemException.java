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

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * GemFire-specific subclass of UncategorizedDataAccessException, for GemFire system errors that do not match any concrete <code>org.springframework.dao</code> exceptions.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireSystemException extends UncategorizedDataAccessException {

	public GemfireSystemException(Exception ex) {
		super(ex.getMessage(), ex);
	}

	public GemfireSystemException(RuntimeException ex) {
		super(ex.getMessage(), ex);
	}
}
