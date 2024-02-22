/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.FunctionException;

/**
 * A {@link FunctionException} indicating a timeout during execution.
 *
 * @author John Blum
 * @see FunctionException
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public class ExecutionTimeoutFunctionException extends FunctionException {

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
