/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Distributed role view; runtime availability is evaluated by the native engine. */
public interface DriverRole extends Comparable<DriverRole> {
	String getName();
	boolean isPresent();
	int getCount();
}
