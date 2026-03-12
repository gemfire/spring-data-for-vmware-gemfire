/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated to use generic exception type instead of GemFire-specific CancelException
 */

package org.springframework.data.gemfire;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * GemFire-specific class for exceptions caused by system cancellations.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireCancellationException extends InvalidDataAccessResourceUsageException {

	public GemfireCancellationException(RuntimeException ex) {
		super(ex.getMessage(), ex);
	}
}
