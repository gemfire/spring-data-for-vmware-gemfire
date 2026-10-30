/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.lang.reflect.Method;
import java.util.Map;

import com.vmware.gemfire.driver.contract.GemFireDriverContract;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;

@org.junit.jupiter.api.TestInstance(org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS)
class GemFire104DriverContractTests extends GemFireDriverContract {

	private NativeServerFixture server;

	@org.junit.jupiter.api.BeforeAll void startServer(@org.junit.jupiter.api.io.TempDir java.nio.file.Path directory) throws Exception {
		server = new NativeServerFixture(directory);
	}
	@org.junit.jupiter.api.AfterAll void stopServer() throws Exception {
		if (server != null) server.close();
	}
	@Override protected com.vmware.gemfire.driver.spi.DriverConnection connect() {
		var connection = super.connect();
		try {
			connection.createPool("contract-pdx", new com.vmware.gemfire.driver.spi.PoolConfiguration(
				java.util.List.of(new com.vmware.gemfire.driver.spi.PoolConfiguration.Endpoint("localhost", server.port)),
				java.util.List.of(), 0));
			return connection;
		} catch (RuntimeException failure) { connection.close(); throw failure; }
	}

	@Override protected int sampleStatistics(com.vmware.gemfire.driver.spi.DriverConnection connection,
			com.vmware.gemfire.driver.spi.DriverStatistics statistics) {
		var system = ((GemFireConnection) connection).cacheForTransactionCallback().getDistributedSystem();
		return java.util.Arrays.stream(system.findStatisticsByType(system.findType(statistics.getType().getName())))
			.filter(s -> s.getUniqueId() == statistics.getUniqueId())
			.mapToInt(s -> ((org.apache.geode.internal.statistics.StatisticsImpl) s).updateSuppliedValues()).sum();
	}

	@Override
	protected String expectedNativeVersion() { return System.getProperty("expectedGemFireVersion"); }

	@Override
	protected Map<Method, Reason> expectedUnsupportedMethods() { return Map.of(); }
}
