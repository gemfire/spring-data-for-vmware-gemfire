/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.util.Objects;
import java.util.Properties;
import java.util.function.BooleanSupplier;

import com.vmware.gemfire.driver.spi.DriverClientRuntime;
import com.vmware.gemfire.driver.spi.DriverCancellation;
import com.vmware.gemfire.driver.spi.DriverMember;
import com.vmware.gemfire.driver.spi.DriverPropertySnapshot;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.DriverStatisticsFactory;

/** Local identity, lifecycle and supplied configuration; no network, authentication or distributed membership. */
final class MockClientRuntime implements DriverClientRuntime {

	private final MockLog log;
	private final MockLog securityLog;
	private final DriverMember member;
	private final BooleanSupplier closed;
	private final DriverPropertySnapshot properties;
	private final DriverPropertySnapshot securityProperties;
	private final MockStatisticsTypes statisticsTypes = new MockStatisticsTypes();
	private final DriverStatisticsFactory statisticsFactory;
	// Native client system cancellation remains clear on ordinary cache closure.
	// The mock does not simulate JVM failure or abnormal system shutdown.
	private final DriverCancellation cancellation = new MockCancellation(() -> false);

	MockClientRuntime(DriverMember member, BooleanSupplier closed, Properties properties, boolean statisticsEnabled) {
		this.member = member;
		this.log = new MockLog(properties.getProperty("log-level", "config"));
		this.securityLog = new MockLog(properties.getProperty("security-log-level", "config"));
		this.statisticsFactory = new MockStatisticsFactory(statisticsTypes, member.getName(), statisticsEnabled);
		this.closed = closed;
		this.properties = new DriverPropertySnapshot(properties);
		Properties security = new Properties();
		for (String name : properties.stringPropertyNames()) {
			if (name.startsWith("security-")) security.setProperty(name, properties.getProperty(name));
		}
		this.securityProperties = new DriverPropertySnapshot(security);
	}

	@Override public MockLog getLog() { return log; }
	@Override public MockLog getSecurityLog() { return securityLog; }
	@Override public DriverPropertySnapshot getProperties() { return properties; }
	@Override public DriverStatisticsFactory getStatisticsFactory() { return statisticsFactory; }
	@Override public DriverStatisticsTypes getStatisticsTypes() { return statisticsTypes; }
	@Override public DriverPropertySnapshot getSecurityProperties() { return securityProperties; }
	@Override public boolean isConnected() { return !closed.getAsBoolean(); }
	@Override public DriverCancellation getCancellation() { return cancellation; }
	@Override public DriverCancellation noCancellation() { return new MockCancellation(() -> false); }
	@Override public DriverCancellation createCancellation(BooleanSupplier cancelled) {
		return new MockCancellation(Objects.requireNonNull(cancelled, "cancelled"));
	}
	@Override public String getName() { return member.getName(); }
	@Override public long getLegacyId() { return 0; }
	@Override public DriverMember getMember() { return member; }
}
