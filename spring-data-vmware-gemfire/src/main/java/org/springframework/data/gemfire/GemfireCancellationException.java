/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

/** Cache or distributed-system cancellation. Native causes are diagnostic only, never a public type contract. */
@SuppressWarnings("serial")
public class GemfireCancellationException extends InvalidDataAccessResourceUsageException {

	public GemfireCancellationException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireCancellationException(String message, Throwable cause) {
		super(message, cause);
	}
}
