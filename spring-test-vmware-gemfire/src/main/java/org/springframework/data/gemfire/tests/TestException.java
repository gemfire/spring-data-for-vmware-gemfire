/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests;

/**
 * {@link RuntimeException} used to capture a problem during testing.
 *
 * @author John Blum
 * @see RuntimeException
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class TestException extends RuntimeException {

	public TestException() { }

	public TestException(String message) {
		super(message);
	}

	public TestException(Throwable cause) {
		super(cause);
	}

	public TestException(String message, Throwable cause) {
		super(message, cause);
	}
}
