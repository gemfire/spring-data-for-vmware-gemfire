/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.function.BooleanSupplier;

import org.apache.geode.CancelCriterion;
import org.apache.geode.distributed.DistributedSystem;

import com.vmware.gemfire.driver.spi.DriverClientRuntime;
import com.vmware.gemfire.driver.spi.DriverCancellation;
import com.vmware.gemfire.driver.spi.DriverMember;
import com.vmware.gemfire.driver.spi.DriverLog;
import com.vmware.gemfire.driver.spi.DriverPropertySnapshot;
import com.vmware.gemfire.driver.spi.DriverStatisticsTypes;
import com.vmware.gemfire.driver.spi.DriverStatisticsFactory;

final class GemFireClientRuntime implements DriverClientRuntime {

	private final DistributedSystem system;
	private final DriverCancellation cancellation;
	private final GemFireStatisticsTypes statisticsTypes;
	private final DriverStatisticsFactory statisticsFactory;
	private final GemFireEventMapper events = new GemFireEventMapper();
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();

	GemFireClientRuntime(DistributedSystem system) {
		this.system = system;
		this.cancellation = new GemFireCancellation(system.getCancelCriterion());
		this.statisticsTypes = new GemFireStatisticsTypes(system);
		this.statisticsFactory = new GemFireStatisticsFactory(system, statisticsTypes);
	}

	@Override public DriverLog getLog() {
		try { return new GemFireLog(system.getLogWriter()); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getLog", failure); }
	}
	@Override public DriverLog getSecurityLog() {
		try { return new GemFireLog(system.getSecurityLogWriter()); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getSecurityLog", failure); }
	}

	@Override public DriverCancellation getCancellation() { return cancellation; }
	@Override public DriverStatisticsFactory getStatisticsFactory() { return statisticsFactory; }
	@Override public DriverStatisticsTypes getStatisticsTypes() { return statisticsTypes; }
	@Override public DriverPropertySnapshot getProperties() {
		try { return new DriverPropertySnapshot(system.getProperties()); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getProperties", failure); }
	}
	@Override public DriverPropertySnapshot getSecurityProperties() {
		try { return new DriverPropertySnapshot(system.getSecurityProperties()); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getSecurityProperties", failure); }
	}
	@Override public DriverCancellation noCancellation() { return new GemFireCancellation(CancelCriterion.NONE); }
	@Override public DriverCancellation createCancellation(BooleanSupplier cancelled) {
		return new GemFireCancellation(cancelled);
	}

	@Override public boolean isConnected() {
		try { return system.isConnected(); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.isConnected", failure); }
	}

	@Override public String getName() {
		try { return system.getName(); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getName", failure); }
	}
	@Override public long getLegacyId() {
		try { return system.getId(); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getLegacyId", failure); }
	}

	@Override public DriverMember getMember() {
		try { return events.member(system.getDistributedMember()); }
		catch (RuntimeException failure) { throw exceptions.translate("ClientRuntime.getMember", failure); }
	}
}
