/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.mock.support;

/**
 * The {@link MockObjectsException} class is a {@link RuntimeException} indicating a general problem
 * with the Mock Objects infrastructure.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 0.0.1
 */
@SuppressWarnings("unused")
public class MockObjectsException extends RuntimeException {

	/**
	 * Constructs a new instance of the {@link MockObjectsException} class with no message or underlying cause.
	 */
	public MockObjectsException() {
	}

	/**
	 * Constructs a new instance of the {@link MockObjectsException} class initialized with
	 * the given {@link String message} describing the problem.
	 *
	 * @param message {@link String} describing the problem.
	 * @see String
	 */
	public MockObjectsException(String message) {
		super(message);
	}

	/**
	 * Constructs a new instance of the {@link MockObjectsException} class initialized with
	 * the given {@link Throwable cause} of the underlying problem.
	 *
	 * @param cause {@link Throwable} object containing the cause of this exception.
	 * @see Throwable
	 */
	public MockObjectsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a new instance of the {@link MockObjectsException} class initialized with
	 * the given {@link String message} describing the underlying problem as well as the {@link Throwable cause}
	 * of the underlying problem.
	 *
	 * @param message {@link String} describing the problem.
	 * @param cause {@link Throwable} object containing the cause of this exception.
	 * @see Throwable
	 * @see String
	 */
	public MockObjectsException(String message, Throwable cause) {
		super(message, cause);
	}
}
