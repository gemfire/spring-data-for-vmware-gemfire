/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudFunctionException;

/**
 * A {@link GudFunctionException} indicating a timeout during execution.
 *
 * @author John Blum
 * @see GudFunctionException
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public class ExecutionTimeoutFunctionException extends GudFunctionException {

	public ExecutionTimeoutFunctionException() { }

	public ExecutionTimeoutFunctionException(String message) {
		super(message);
	}

	public ExecutionTimeoutFunctionException(Throwable cause) {
		super(cause);
	}

	public ExecutionTimeoutFunctionException(String message, Throwable cause) {
		super(message, cause);
	}
}
