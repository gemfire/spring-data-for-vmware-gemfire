/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public abstract class DriverRegionMembershipListenerAdapter<K, V> extends DriverCacheListenerAdapter<K, V>
	implements DriverRegionMembershipListener<K, V> {
	@Override public void initialMembers(DriverRegion<K, V> region, DriverMember[] members) { }
	@Override public void afterRemoteRegionCreate(DriverRegionEvent<K, V> event) { }
	@Override public void afterRemoteRegionDeparture(DriverRegionEvent<K, V> event) { }
	@Override public void afterRemoteRegionCrash(DriverRegionEvent<K, V> event) { }
}
