/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.springframework.dao.DataIntegrityViolationException;

/** Index creation or maintenance failure. Native causes are diagnostic only, never a public type contract. */
@SuppressWarnings("serial")
public class GemfireIndexException extends DataIntegrityViolationException {

	public GemfireIndexException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireIndexException(String message, Throwable cause) {
		super(message, cause);
	}
}
