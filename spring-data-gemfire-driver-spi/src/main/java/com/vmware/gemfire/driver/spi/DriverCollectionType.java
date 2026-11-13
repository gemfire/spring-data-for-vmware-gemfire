/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Describes the collection holding query results. */
public interface DriverCollectionType extends DriverObjectType {
	DriverObjectType elementType();
	boolean allowsDuplicates();
	boolean isOrdered();
}
