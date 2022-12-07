/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.transaction;

import org.springframework.transaction.TransactionException;

/**
 * Gemfire-specific subclass of {@link org.springframework.transaction.TransactionException}, indicating a transaction failure at commit time.
 *
 * @author Costin Leau
 */
@SuppressWarnings("serial")
public class GemfireTransactionCommitException extends TransactionException {

	public GemfireTransactionCommitException(String message, Throwable cause) {
		super(message, cause);
	}

	public GemfireTransactionCommitException(String message) {
		super(message);
	}
}
