/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.lang.reflect.Method;
import java.util.Map;

import com.vmware.gemfire.driver.contract.GemFireDriverContract;
import com.vmware.gemfire.driver.spi.UnsupportedFeatureException.Reason;

class GemFire104DriverContractTests extends GemFireDriverContract {

	@Override
	protected String expectedNativeVersion() { return System.getProperty("expectedGemFireVersion"); }

	@Override
	protected Map<Method, Reason> expectedUnsupportedMethods() { return Map.of(); }
}
