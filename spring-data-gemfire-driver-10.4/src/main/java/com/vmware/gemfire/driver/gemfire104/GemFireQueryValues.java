/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.ArrayList;
import java.util.List;

import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;
import org.apache.geode.cache.query.Struct;

/** Converts native query results to SPI values so no native type escapes the driver. */
final class GemFireQueryValues {

	private GemFireQueryValues() {
	}

	static Object convert(Object value) {
		if (value == QueryService.UNDEFINED) return com.vmware.gemfire.driver.spi.DriverUndefined.INSTANCE;
		if (value instanceof SelectResults<?> results) return new GemFireSelectResults(results);
		if (value instanceof Struct struct) return new GemFireStruct(struct);
		return value;
	}

	static List<Object> convertAll(Iterable<?> values) {
		List<Object> converted = new ArrayList<>();
		for (Object value : values) converted.add(convert(value));
		return converted;
	}
}
