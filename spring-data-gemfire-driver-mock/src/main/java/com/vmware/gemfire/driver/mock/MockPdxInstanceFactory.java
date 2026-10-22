/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

final class MockPdxInstanceFactory implements DriverPdxInstanceFactory {
	private final String className;
	private final Map<String, MockPdxField> fields = new LinkedHashMap<>();
	private boolean deserializable = true;
	private boolean created;
	MockPdxInstanceFactory(String className) {
		if (className == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "PDX class name is required", null);
		this.className = className;
		this.deserializable = !className.isEmpty();
	}
	@Override public DriverPdxInstance create() {
		if (created) throw new DriverOperationException(DriverFailureKind.INVALID_STATE, "PDX factory already used", null);
		created = true;
		return new MockPdxInstance(className, fields, deserializable, false);
	}
	private DriverPdxInstanceFactory write(String name, Object value, Class<?> type) {
		if (name == null || fields.containsKey(name)) throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "Invalid or duplicate PDX field", null);
		if (value == null && type.isPrimitive()) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "Null primitive PDX field", null);
		fields.put(name, new MockPdxField(type, null, false).withValue(value));
		return this;
	}
	@Override public DriverPdxInstanceFactory writeChar(String name, char value) { return write(name, value, char.class); }
	@Override public DriverPdxInstanceFactory writeBoolean(String name, boolean value) { return write(name, value, boolean.class); }
	@Override public DriverPdxInstanceFactory writeByte(String name, byte value) { return write(name, value, byte.class); }
	@Override public DriverPdxInstanceFactory writeShort(String name, short value) { return write(name, value, short.class); }
	@Override public DriverPdxInstanceFactory writeInt(String name, int value) { return write(name, value, int.class); }
	@Override public DriverPdxInstanceFactory writeLong(String name, long value) { return write(name, value, long.class); }
	@Override public DriverPdxInstanceFactory writeFloat(String name, float value) { return write(name, value, float.class); }
	@Override public DriverPdxInstanceFactory writeDouble(String name, double value) { return write(name, value, double.class); }
	@Override public DriverPdxInstanceFactory writeDate(String name, java.util.Date value) { return write(name, value, java.util.Date.class); }
	@Override public DriverPdxInstanceFactory writeString(String name, String value) { return write(name, value, String.class); }
	@Override public DriverPdxInstanceFactory writeObject(String name, Object value) { return write(name, value, Object.class); }
	@Override public DriverPdxInstanceFactory writeBooleanArray(String name, boolean[] value) { return write(name, value, boolean[].class); }
	@Override public DriverPdxInstanceFactory writeCharArray(String name, char[] value) { return write(name, value, char[].class); }
	@Override public DriverPdxInstanceFactory writeByteArray(String name, byte[] value) { return write(name, value, byte[].class); }
	@Override public DriverPdxInstanceFactory writeShortArray(String name, short[] value) { return write(name, value, short[].class); }
	@Override public DriverPdxInstanceFactory writeIntArray(String name, int[] value) { return write(name, value, int[].class); }
	@Override public DriverPdxInstanceFactory writeLongArray(String name, long[] value) { return write(name, value, long[].class); }
	@Override public DriverPdxInstanceFactory writeFloatArray(String name, float[] value) { return write(name, value, float[].class); }
	@Override public DriverPdxInstanceFactory writeDoubleArray(String name, double[] value) { return write(name, value, double[].class); }
	@Override public DriverPdxInstanceFactory writeStringArray(String name, String[] value) { return write(name, value, String[].class); }
	@Override public DriverPdxInstanceFactory writeObjectArray(String name, Object[] value) { return write(name, value, Object[].class); }
	@Override public DriverPdxInstanceFactory writeArrayOfByteArrays(String name, byte[][] value) { return write(name, value, byte[][].class); }
	@Override public DriverPdxInstanceFactory writeObject(String name, Object value, boolean checkPortability) { return writeObject(name, value); }
	@Override public DriverPdxInstanceFactory writeObjectArray(String name, Object[] value, boolean checkPortability) { return writeObjectArray(name, value); }
	@Override public <CT, VT extends CT> DriverPdxInstanceFactory writeField(String name, VT value, Class<CT> type) { return writeField(name, value, type, false); }
	@Override public <CT, VT extends CT> DriverPdxInstanceFactory writeField(String name, VT value, Class<CT> type, boolean checkPortability) {
		if (type == null) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "PDX field type is required", null);
		Class<?> kind = type;
		if (!type.isPrimitive() && type != String.class && type != java.util.Date.class && !type.isArray()) kind = Object.class;
		if (type.isArray() && !type.getComponentType().isPrimitive() && type != String[].class && type != byte[][].class) kind = Object[].class;
		return write(name, value, kind);
	}
	@Override public DriverPdxInstanceFactory markIdentityField(String name) {
		if (!fields.containsKey(name)) throw new DriverOperationException(DriverFailureKind.SYSTEM_FAILURE, "Unknown PDX identity field", null);
		fields.put(name, fields.get(name).withIdentity()); return this;
	}
	@Override public DriverPdxInstanceFactory neverDeserialize() { deserializable = false; return this; }
}
