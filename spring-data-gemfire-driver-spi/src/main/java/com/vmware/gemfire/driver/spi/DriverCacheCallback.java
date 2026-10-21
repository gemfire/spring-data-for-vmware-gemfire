/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Region-owned callback lifecycle; cleanup timing and failure handling follow the engine. */
public interface DriverCacheCallback extends DriverDeclarable, AutoCloseable {
	@Override void close();
}
