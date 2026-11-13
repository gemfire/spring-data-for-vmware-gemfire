/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.Collections;
import java.util.List;

import org.apache.geode.cache.query.Struct;

import com.vmware.gemfire.driver.spi.DriverStruct;
import com.vmware.gemfire.driver.spi.DriverStructType;

/** A detached copy of a native struct row; field values are converted eagerly. */
final class GemFireStruct implements DriverStruct {

	private final List<String> names;
	private final List<Object> values;
	private final DriverStructType type;

	GemFireStruct(Struct struct) {
		this.type = GemFireObjectTypes.struct(struct.getStructType());
		this.names = type.fieldNames();
		this.values = Collections.unmodifiableList(GemFireQueryValues.convertAll(java.util.Arrays.asList(struct.getFieldValues())));
	}

	@Override public Object get(String fieldName) {
		int index = names.indexOf(java.util.Objects.requireNonNull(fieldName, "fieldName"));
		if (index < 0) throw new IllegalArgumentException("Unknown struct field");
		return values.get(index);
	}

	@Override public List<Object> fieldValues() { return values; }

	@Override public DriverStructType structType() { return type; }
}
