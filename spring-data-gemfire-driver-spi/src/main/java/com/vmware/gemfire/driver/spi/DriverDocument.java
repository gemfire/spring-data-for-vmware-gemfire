/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import java.util.List;
import java.util.function.BiConsumer;

/** Field-oriented value. Missing fields and present null fields are distinct. */
public interface DriverDocument {
	/** Native-free replacement for the engine's FIELD_NOT_FOUND sentinel. */
	enum MissingField { INSTANCE }
	boolean hasField(String name);
	List<String> getFieldNames();
	Object getField(String name);
	/** Returns {@link MissingField#INSTANCE} only when the field is absent. */
	Object getAndCheckField(String name);
	void foreach(BiConsumer<String, Object> consumer);
	boolean equals(Object other);
	int hashCode();
}
