/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import java.lang.reflect.Method;
import java.util.Map;

import com.vmware.gemfire.driver.contract.GemFireDriverContract;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;

class MockDriverContractTests extends GemFireDriverContract {

	@Override protected int sampleStatistics(com.vmware.gemfire.driver.spi.DriverConnection connection,
			com.vmware.gemfire.driver.spi.DriverStatistics statistics) {
		return ((MockStatisticsFactory) connection.getRuntime().getStatisticsFactory()).sampleSuppliers();
	}

	@Override
	protected String expectedNativeVersion() { return "mock"; }

	@Override
	protected Map<Method, Reason> expectedUnsupportedMethods() {
		try {
			Map<Method, Reason> unsupported = new java.util.HashMap<>();
			unsupported.put(com.vmware.gemfire.driver.spi.DriverRegion.class.getMethod("containsKeyOnServer", Object.class), Reason.NOT_INTRODUCED);
			unsupported.put(com.vmware.gemfire.driver.spi.DriverRegion.class.getMethod("isEmptyOnServer"), Reason.NOT_INTRODUCED);
			unsupported.put(com.vmware.gemfire.driver.spi.DriverRegion.class.getMethod("keySetOnServer"), Reason.NOT_INTRODUCED);
			unsupported.put(com.vmware.gemfire.driver.spi.DriverRegion.class.getMethod("sizeLongOnServer"), Reason.NOT_INTRODUCED);
			unsupported.put(com.vmware.gemfire.driver.spi.DriverRegion.class.getMethod("sizeOnServer"), Reason.NOT_INTRODUCED);
			for (Method method : com.vmware.gemfire.driver.spi.DriverTransactionManager.class.getDeclaredMethods()) {
				if (method.isDefault()) unsupported.put(method, Reason.NOT_INTRODUCED);
			}
			return unsupported;
		} catch (ReflectiveOperationException ex) { throw new AssertionError(ex); }
	}
}
