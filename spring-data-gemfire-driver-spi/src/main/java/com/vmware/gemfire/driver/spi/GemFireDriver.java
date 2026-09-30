/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface GemFireDriver {

	DriverMetadata metadata();

	/**
	 * The GemFire product version actually present on the classpath this Driver Impl is co-located
	 * with, read locally from the native jars' build metadata (§6.3). Exposed so driver-core, which
	 * has no native GemFire dependency, can compare it with the GemFire version stamped into the
	 * driver at build time (exact match) and report it in a failure.
	 */
	String runtimeGemFireVersion();

	/**
	 * Establishes the runtime connection this driver mediates. Thread-safety is whatever the
	 * underlying GemFire client/cache naturally provides — the SPI adds no additional
	 * guarantee (§5.6).
	 */
	DriverConnection connect(DriverConfiguration configuration) throws DriverBindException;

}
