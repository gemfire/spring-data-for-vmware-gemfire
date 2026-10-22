/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

/** Local field kind and snapshot; never claims to encode PDX bytes. */
record MockPdxField(Class<?> type, Object value, boolean identity) {
	MockPdxField withIdentity() { return new MockPdxField(type, value, true); }
	MockPdxField withValue(Object replacement) {
		Class<?> accepted = type.isPrimitive() ? boxed(type) : type;
		if (replacement == null && type.isPrimitive()) replacement = java.lang.reflect.Array.get(java.lang.reflect.Array.newInstance(type, 1), 0);
		if (replacement != null && !accepted.isInstance(replacement))
			throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "PDX field type mismatch", null);
		return new MockPdxField(type, new MockPdxValues().copy(replacement), identity);
	}
	private Class<?> boxed(Class<?> type) {
		if (type == boolean.class) return Boolean.class;
		if (type == byte.class) return Byte.class;
		if (type == char.class) return Character.class;
		if (type == short.class) return Short.class;
		if (type == int.class) return Integer.class;
		if (type == long.class) return Long.class;
		if (type == float.class) return Float.class;
		return Double.class;
	}
	boolean hasDefaultValue() {
		if (!type.isPrimitive()) return value == null;
		if (type == boolean.class) return Boolean.FALSE.equals(value);
		if (type == char.class) return Character.valueOf('\0').equals(value);
		if (type == float.class) return Float.valueOf(0).equals(value);
		if (type == double.class) return Double.valueOf(0).equals(value);
		return value instanceof Number number && number.longValue() == 0;
	}
}
