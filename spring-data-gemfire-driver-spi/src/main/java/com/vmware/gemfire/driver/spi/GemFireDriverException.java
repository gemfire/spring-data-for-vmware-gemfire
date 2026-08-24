/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public abstract class GemFireDriverException extends RuntimeException {

	protected GemFireDriverException(String message) {
		super(message);
	}

	protected GemFireDriverException(String message, Throwable cause) {
		super(message, cause);
	}

}
