/*
 * Copyright (c) VMware, Inc. 2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.util;

/**
 * {@link ObjectUtils} is a utility class for performing different opeations on {@link Object objects}.
 *
 * @author John Blum
 * @see Object
 * @since 1.0.0
 */
@SuppressWarnings("all")
public abstract class ObjectUtils {

	public static <T> T doOperationSafely(ExceptionThrowingOperation<T> operation) {
		return doOperationSafely(operation, null);
	}

	public static <T> T doOperationSafely(ExceptionThrowingOperation<T> operation, T defaultValue) {

		try {
			return operation.doExceptionThrowingOperation();
		}
		catch (Exception ignore) {
			return defaultValue;
		}
	}

	public static <T> T rethrowAsRuntimeException(ExceptionThrowingOperation<T> operation) {

		try {
			return operation.doExceptionThrowingOperation();
		}
		catch (RuntimeException cause) {
			throw cause;
		}
		catch (Throwable cause) {
			throw new RuntimeException(cause);
		}
	}

	@FunctionalInterface
	public interface ExceptionThrowingOperation<T> {
		T doExceptionThrowingOperation() throws Exception;
	}
}
