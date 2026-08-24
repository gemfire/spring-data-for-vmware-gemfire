/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public class NoDriverFoundException extends DriverBindException {

	public NoDriverFoundException() {
		super("No GemFireDriver implementation found on the classpath via ServiceLoader.");
	}

}
