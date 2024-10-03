/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.process;

/**
 * The {@link PidNotFoundException} class is a {@link RuntimeException} indicating that the process ID (PID)
 * is unobtainable for the current {@link Process}.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public class PidNotFoundException extends RuntimeException {

	public PidNotFoundException() {
	}

	public PidNotFoundException(final String message) {
		super(message);
	}

	public PidNotFoundException(final Throwable cause) {
		super(cause);
	}

	public PidNotFoundException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
