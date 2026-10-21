/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/** Query parsing or execution failure. Native causes are diagnostic only, never a public type contract. */
@SuppressWarnings("serial")
public class GemfireQueryException extends InvalidDataAccessResourceUsageException {

	public GemfireQueryException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireQueryException(String message, Throwable cause) {
		super(message, cause);
	}
}
