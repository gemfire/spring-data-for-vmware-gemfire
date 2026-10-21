/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.io.IOException;
import java.net.Socket;

/** Creates an unconnected socket. The engine owns connecting and closing the returned socket. */
@FunctionalInterface
public interface DriverSocketFactory {
	Socket createSocket() throws IOException;
}
