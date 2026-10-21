/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

public interface DriverRegionRoleListener<K, V> extends DriverCacheListener<K, V> {
	void afterRoleGain(DriverRoleEvent<K, V> event);
	void afterRoleLoss(DriverRoleEvent<K, V> event);
}
