/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.Set;

public interface DriverRoleEvent<K, V> extends DriverRegionEvent<K, V> {
	Set<String> getRequiredRoles();
}
