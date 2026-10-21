/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.springframework.dao.UncategorizedDataAccessException;

/** An otherwise unclassified GemFire system failure. Native causes are diagnostic only, never a public type contract. */
@SuppressWarnings("serial")
public class GemfireSystemException extends UncategorizedDataAccessException {

	public GemfireSystemException(Throwable cause) {
		this(cause.getMessage(), cause);
	}

	public GemfireSystemException(String message, Throwable cause) {
		super(message, cause);
	}
}
