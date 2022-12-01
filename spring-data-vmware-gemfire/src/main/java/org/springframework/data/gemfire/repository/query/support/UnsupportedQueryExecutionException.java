/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.query.support;

/**
 * A Java {@link RuntimeException} indicating that the Apache Geode OQL query could not be executed (i.e. handled)
 * by the {@link OqlQueryExecutor}.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 2.4.0
 */
@SuppressWarnings("unused")
public class UnsupportedQueryExecutionException extends RuntimeException {

	/**
	 * Constructs a new, uninitialized instance of {@link UnsupportedQueryExecutionException}.
	 */
	public UnsupportedQueryExecutionException() { }

	/**
	 * Constructs a new instance of {@link UnsupportedQueryExecutionException} initialized with
	 * the given {@link String message} describing the exception.
	 *
	 * @param message {@link String} containing a description of the exception.
	 * @see String
	 */
	public UnsupportedQueryExecutionException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of {@link UnsupportedQueryExecutionException} initialized with
	 * the given {@link Throwable} as the underlying {@literal cause} of this exception.
	 *
	 * @param cause {@link Throwable} used as the cause of this exception.
	 * @see Throwable
	 */
	public UnsupportedQueryExecutionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of {@link UnsupportedQueryExecutionException} initialized with
	 * the given {@link String message} describing the exception and given {@link Throwable}
	 * as the underlying {@literal cause} of this exception.
	 *
	 * @param message {@link String} containing a description of the exception.
	 * @param cause {@link Throwable} used as the cause of this exception.
	 * @see String
	 * @see Throwable
	 */
	public UnsupportedQueryExecutionException(String message, Throwable cause) {
		super(message, cause);
	}
}
