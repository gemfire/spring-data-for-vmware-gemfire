/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.DriverBindException;
import com.vmware.gemfire.driver.spi.DriverConfiguration;
import com.vmware.gemfire.driver.spi.DriverConnection;
import com.vmware.gemfire.driver.spi.DriverMetadata;
import com.vmware.gemfire.driver.spi.DriverPropertyFile;
import com.vmware.gemfire.driver.spi.GemFireDriver;

/**
 * Native-free in-memory test provider. Each connection has independent state. The synthetic
 * native version "mock" identifies this stand-in; it does not claim compatibility with a native engine.
 * Named pools are per-connection configuration records with default minimum connections 1;
 * they open no sockets and do not simulate native registry sharing or region attachment.
 * Only LOCAL regions are implemented. Distribution, persistence and eviction are not simulated.
 */
public final class MockGemFireDriver implements GemFireDriver {

	private final boolean statisticsEnabled;

	public MockGemFireDriver() { this(true); }

	/**
	 * Explicit test control for local statistics. False creates inert, unregistered
	 * handles; it does not change sampling configuration or read JVM properties.
	 */
	public MockGemFireDriver(boolean statisticsEnabled) { this.statisticsEnabled = statisticsEnabled; }

	@Override
	public DriverPropertyFile resolvePropertiesFile() { return new DriverPropertyFile("gemfire.properties", null); }

	@Override
	public DriverPropertyFile resolveSecurityPropertiesFile() { return new DriverPropertyFile("gfsecurity.properties", null); }

	@Override
	public DriverMetadata metadata() { return () -> "In-memory GemFire mock driver"; }

	@Override
	public String runtimeGemFireVersion() { return "mock"; }

	@Override
	public DriverConnection connect(DriverConfiguration configuration) {
		if (!(configuration instanceof CacheConfiguration cacheConfiguration)) {
			throw new DriverBindException("The mock driver requires CacheConfiguration.");
		}
		return new MockConnection(cacheConfiguration.properties(), statisticsEnabled);
	}
}
