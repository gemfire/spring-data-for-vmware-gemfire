/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** A multi-field query result row. Field values never expose native types. */
public interface DriverStruct {
	/** The value of the named field; an unknown name is rejected as an invalid argument. */
	Object get(String fieldName);
	/** Immutable, in projection order. */
	java.util.List<Object> fieldValues();
	DriverStructType structType();
}
