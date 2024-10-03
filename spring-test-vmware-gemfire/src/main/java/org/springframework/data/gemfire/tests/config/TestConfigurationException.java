/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.config;

import org.springframework.data.gemfire.tests.TestException;

/**
 * {@link RuntimeException} used to capture a problem during test configuration.
 *
 * @author John Blum
 * @see RuntimeException
 * @see TestException
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class TestConfigurationException extends TestException {

	public TestConfigurationException() { }

	public TestConfigurationException(String message) {
		super(message);
	}

	public TestConfigurationException(Throwable cause) {
		super(cause);
	}

	public TestConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
