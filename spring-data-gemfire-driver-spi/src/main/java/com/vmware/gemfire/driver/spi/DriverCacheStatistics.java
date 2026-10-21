/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Live statistics. Providers report disabled statistics through typed failures. */
public interface DriverCacheStatistics {
	long getLastModifiedTime();
	long getLastAccessedTime();
	long getMissCount();
	long getHitCount();
	float getHitRatio();
	void resetCounts();
}
