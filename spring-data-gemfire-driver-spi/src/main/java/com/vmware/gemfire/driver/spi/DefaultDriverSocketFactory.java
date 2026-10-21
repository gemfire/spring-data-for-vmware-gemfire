/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.net.Socket;

/** Stateless default socket creation; callers own each factory instance. */
public final class DefaultDriverSocketFactory implements DriverSocketFactory {
	@Override public Socket createSocket() { return new Socket(); }
}
