/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class DriverOperationException extends GemFireDriverException {

	public DriverOperationException(String message, Throwable cause) {
		super(message, cause);
	}

}
