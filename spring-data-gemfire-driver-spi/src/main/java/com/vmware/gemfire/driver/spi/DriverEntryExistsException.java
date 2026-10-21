/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Duplicate-entry failure with the previous value, excluded from diagnostic messages. */
public final class DriverEntryExistsException extends DriverOperationException {
	private Object oldValue;
	public DriverEntryExistsException(Object oldValue, Throwable cause) {
		this("Entry already exists", oldValue, cause);
	}
	public DriverEntryExistsException(String message, Object oldValue, Throwable cause) {
		super(DriverFailureKind.DUPLICATE_ENTRY, message, cause); this.oldValue = oldValue;
	}
	public Object getOldValue() { return oldValue; }
	public void setOldValue(Object value) { oldValue = value; }
}
