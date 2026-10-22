/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.lang.reflect.Array;
import java.util.*;

/** Defensive snapshots of supported local PDX values. Unknown application serialization fails explicitly. */
final class MockPdxValues {
	Object copy(Object value) { return copy(value, new IdentityHashMap<>()); }
	private Class<?> componentType(Class<?> type) {
		if (type.isArray()) return Array.newInstance(componentType(type.getComponentType()), 0).getClass();
		return DriverPdxInstance.class.isAssignableFrom(type) ? DriverPdxInstance.class : type;
	}
	private Object copy(Object value, IdentityHashMap<Object, Object> seen) {
		if (value == null || value instanceof String || value instanceof Boolean || value instanceof Character
			|| value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long
			|| value instanceof Float || value instanceof Double) return value;
		if (seen.containsKey(value)) throw UnsupportedFeatureException.notIntroduced("Pdx.cyclicMockValue");
		seen.put(value, value);
		try {
			if (value instanceof MockPdxInstance pdx) return pdx.snapshot();
			if (value instanceof java.util.Date date) return new java.util.Date(date.getTime());
			if (value.getClass().isArray()) {
				int length = Array.getLength(value);
				Object result = Array.newInstance(componentType(value.getClass().getComponentType()), length);
				for (int i = 0; i < length; i++) Array.set(result, i, copy(Array.get(value, i), seen));
				return result;
			}
			if ((value instanceof Collection<?> || value instanceof Map<?, ?>) && !value.getClass().getName().startsWith("java.util."))
				throw UnsupportedFeatureException.notIntroduced("Pdx.applicationObjectSerialization");
			if (value instanceof List<?> list) { List<Object> result = new ArrayList<>(); for (Object item : list) result.add(copy(item, seen)); return result; }
			if (value instanceof Set<?> set) { Set<Object> result = new LinkedHashSet<>(); for (Object item : set) result.add(copy(item, seen)); return result; }
			if (value instanceof Map<?, ?> map) { Map<Object, Object> result = new LinkedHashMap<>(); map.forEach((key, item) -> result.put(copy(key, seen), copy(item, seen))); return result; }
			throw UnsupportedFeatureException.notIntroduced("Pdx.applicationObjectSerialization");
		} finally { seen.remove(value); }
	}
	int hash(Object value) {
		if (value == null) return 0;
		if (value.getClass().isArray()) { int hash = 1; for (int i = 0; i < Array.getLength(value); i++) hash = 31 * hash + hash(Array.get(value, i)); return hash; }
		return value.hashCode();
	}
}
