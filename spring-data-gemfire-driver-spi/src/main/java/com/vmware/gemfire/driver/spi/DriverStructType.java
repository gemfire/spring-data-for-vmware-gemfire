/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Describes the fields of a {@link DriverStruct}. */
public interface DriverStructType extends DriverObjectType {
	/** Immutable, in projection order. */
	java.util.List<String> fieldNames();
	/** Immutable, in projection order, parallel to {@link #fieldNames()}. */
	java.util.List<DriverObjectType> fieldTypes();
}
