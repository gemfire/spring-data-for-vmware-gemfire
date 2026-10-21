/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface DriverRegionMembershipListener<K, V> extends DriverCacheListener<K, V> {
	void initialMembers(DriverRegion<K, V> region, DriverMember[] members);
	void afterRemoteRegionCreate(DriverRegionEvent<K, V> event);
	void afterRemoteRegionDeparture(DriverRegionEvent<K, V> event);
	void afterRemoteRegionCrash(DriverRegionEvent<K, V> event);
}
