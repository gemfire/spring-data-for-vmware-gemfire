/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * A typed PDX value owned by its provider. Field values recursively expose typed PDX handles.
 * Equality and identity fields follow that provider's PDX semantics. This is not a wire codec;
 * application deserialization may be unavailable on local mock providers.
 */
public interface DriverPdxInstance extends DriverDocument {
	String getClassName();
	boolean isEnum();
	Object getObject();
	boolean isIdentityField(String name);
	boolean isDeserializable();
	DriverWritablePdxInstance createWriter();
	String toString();
}
