/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Provider-neutral description of a query result element type. */
public interface DriverObjectType {
	/** The element class' simple name; never a native type name for native-only element kinds. */
	String simpleClassName();
	boolean isCollectionType();
	boolean isMapType();
	boolean isStructType();
}
