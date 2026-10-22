/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Copy-on-write PDX value; changing it does not change its source instance. */
public interface DriverWritablePdxInstance extends DriverPdxInstance {
	void setField(String name, Object value);
}
