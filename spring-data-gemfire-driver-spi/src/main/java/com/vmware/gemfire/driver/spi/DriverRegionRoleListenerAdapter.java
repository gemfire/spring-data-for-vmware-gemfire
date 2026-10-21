/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public abstract class DriverRegionRoleListenerAdapter<K, V> extends DriverRegionMembershipListenerAdapter<K, V>
	implements DriverRegionRoleListener<K, V> {
	@Override public void afterRoleGain(DriverRoleEvent<K, V> event) { }
	@Override public void afterRoleLoss(DriverRoleEvent<K, V> event) { }
}
