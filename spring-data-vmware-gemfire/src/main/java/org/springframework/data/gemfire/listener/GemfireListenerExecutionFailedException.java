/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.listener;

import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.gemfire.listener.adapter.ContinuousQueryListenerAdapter;

/**
 * Exception thrown when the execution of a listener method failed.
 *
 * @author Costin Leau
 * @see ContinuousQueryListenerAdapter
 */
@SuppressWarnings({ "serial", "unused" })
public class GemfireListenerExecutionFailedException extends InvalidDataAccessApiUsageException {

	/**
	 * Constructs a new <code>GemfireListenerExecutionFailedException</code> instance.
	 *
	 * @param message a String describing the cause of the exception.
	 */
	public GemfireListenerExecutionFailedException(String message) {
		super(message);
	}

	/**
	 * Constructs a new <code>GemfireListenerExecutionFailedException</code> instance.
	 *
	 * @param message a String describing the cause of the exception.
	 * @param cause a Throwable that was underlying cause of this exception.
	 */
	public GemfireListenerExecutionFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
