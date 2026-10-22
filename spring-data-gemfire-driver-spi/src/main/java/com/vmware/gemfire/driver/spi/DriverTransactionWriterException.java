/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Application veto; the commit boundary reports TRANSACTION_VETO and preserves the original cause. */
public class DriverTransactionWriterException extends Exception {
	public DriverTransactionWriterException(String message) { super(message); }
	public DriverTransactionWriterException(Throwable cause) { super("Transaction writer rejected commit", cause); }
}
