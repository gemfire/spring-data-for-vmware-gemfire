// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** A JSON document could not be parsed or rendered. The message is a fixed diagnostic that never contains document content;
 * the provider's original failure is retained as the cause for trusted diagnostics only.
 */
public class DriverJsonParseException extends DriverOperationException {
	public DriverJsonParseException(String message, Throwable cause) {
		super(DriverFailureKind.INVALID_ARGUMENT, message, cause);
	}
}
