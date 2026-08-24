/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class DriverBindException extends GemFireDriverException {

	public DriverBindException(String message) {
		super(message);
	}

	public DriverBindException(String message, Throwable cause) {
		super(message, cause);
	}

}
