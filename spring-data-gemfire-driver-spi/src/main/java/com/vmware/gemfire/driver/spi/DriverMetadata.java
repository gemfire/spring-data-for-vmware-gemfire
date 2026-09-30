/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface DriverMetadata {

	// Identity only, for bind-time logging and diagnostics (§5.1, §8).
	String driverName();

}
