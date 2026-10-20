/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.List;

import com.vmware.gemfire.driver.spi.DriverPool;
import com.vmware.gemfire.driver.spi.PoolConfiguration;
import com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint;

/** Local configuration record only. No networking, discovery, subscriptions or region attachment. */
final class MockPool implements DriverPool {

	private final String name;
	private final PoolConfiguration configuration;
	private final MockConnection connection;
	private boolean destroyed;

	MockPool(String name, PoolConfiguration configuration, MockConnection connection) {
		this.name = name;
		this.configuration = configuration;
		this.connection = connection;
	}

	@Override public String name() { return name; }
	@Override public List<Endpoint> servers() { return configuration.servers(); }
	@Override public List<Endpoint> locators() { return configuration.locators(); }
	/** The mock's default is 1; the configured value remains metadata, not a connection count. */
	@Override public int minimumConnections() { return configuration.minimumConnections().orElse(1); }
	@Override public boolean isDestroyed() { return destroyed; }
	@Override public void destroy() {
		destroyed = true;
		connection.removePool(name, this);
	}
}
