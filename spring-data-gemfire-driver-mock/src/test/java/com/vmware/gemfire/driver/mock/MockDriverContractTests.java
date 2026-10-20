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

	@Override
	protected String expectedNativeVersion() { return "mock"; }

	@Override
	protected Map<Method, Reason> expectedUnsupportedMethods() { return Map.of(); }
}
