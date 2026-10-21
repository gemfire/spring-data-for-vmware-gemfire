/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.io.IOException;
import java.net.Socket;
import org.apache.geode.cache.client.SocketFactory;
import com.vmware.gemfire.driver.spi.DriverSocketFactory;
import com.vmware.gemfire.driver.spi.DefaultDriverSocketFactory;

/** Retains callback identity without any global callback registry. IOException passes through. */
record GemFireSocketFactory(DriverSocketFactory delegate) implements SocketFactory {
	@Override public Socket createSocket() throws IOException { return delegate.createSocket(); }
	static DriverSocketFactory adapt(SocketFactory factory) {
		if (factory instanceof GemFireSocketFactory adapter) return adapter.delegate();
		if (factory == SocketFactory.DEFAULT) return new DefaultDriverSocketFactory();
		return factory == null ? null : factory::createSocket;
	}
}
