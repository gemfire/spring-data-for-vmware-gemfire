/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Properties;

/** Application initialization hooks. Programmatic registration does not initialize callbacks.
 * Invoke each hook explicitly with injected configuration when assembling declarative objects.
 * The supplied connection is borrowed, and must not be closed by the callback.
 */
public interface DriverDeclarable {
	default void init(Properties properties) { }
	default void initialize(DriverConnection connection, Properties properties) { }
}
