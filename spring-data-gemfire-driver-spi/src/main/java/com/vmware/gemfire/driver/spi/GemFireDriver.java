/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface GemFireDriver {

	DriverMetadata metadata();

	/**
	 * Axis B self-check (§6.3): does this Driver Impl accept the GemFire version actually
	 * present on the classpath it's co-located with? A verdict, never a throw — driver-core
	 * alone throws on a negative verdict (§5.2).
	 */
	boolean acceptsRuntimeGemFireVersion(String gemFireVersion);

	/**
	 * Establishes the runtime connection this driver mediates. Thread-safety is whatever the
	 * underlying GemFire client/cache naturally provides — the SPI adds no additional
	 * guarantee (§5.6).
	 */
	DriverConnection connect(DriverConfiguration configuration) throws DriverBindException;

}
