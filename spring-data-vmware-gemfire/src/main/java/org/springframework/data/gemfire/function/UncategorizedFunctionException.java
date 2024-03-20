/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionException;

/**
 * An {@link FunctionException} indicating a {@link Function} {@link Execution} {@link RuntimeException}
 * that has not be categorized, or identified by the framework.
 *
 * This {@link RuntimeException} was inspired by the {@link org.springframework.dao.UncategorizedDataAccessException}.
 *
 * @author John Blum
 * @see Execution
 * @see Function
 * @see FunctionException
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public class UncategorizedFunctionException extends FunctionException {

	public UncategorizedFunctionException() {
		super(null, null);
	}

	public UncategorizedFunctionException(String message) {
		super(message, null);
	}

	public UncategorizedFunctionException(Throwable cause) {
		super(null, cause);
	}

	public UncategorizedFunctionException(String message, Throwable cause) {
		super(message, cause);
	}
}
