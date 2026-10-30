// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

/** Local reader over a {@link MockPdxInstance}. Missing fields read as defaults; a field of another type fails. */
final class MockPdxReader implements DriverPdxReader {
	private final MockPdxInstance source;
	private final boolean ignoreUnreadFields;
	private final Set<String> read = new HashSet<>();
	MockPdxReader(MockPdxInstance source, boolean ignoreUnreadFields) { this.source = source; this.ignoreUnreadFields = ignoreUnreadFields; }
	private Object value(String name, Class<?> type) {
		MockPdxField field = source.fields.get(name);
		if (field == null) return type.isPrimitive() ? java.lang.reflect.Array.get(java.lang.reflect.Array.newInstance(type, 1), 0) : null;
		if (type != Object.class && field.type() != type) throw new DriverOperationException(DriverFailureKind.INVALID_ARGUMENT, "PDX field " + name + " is " + field.type().getSimpleName() + ", not " + type.getSimpleName(), null);
		read.add(name);
		return new MockPdxValues().copy(field.value());
	}
	@Override public char readChar(String name) { return (char) value(name, char.class); }
	@Override public boolean readBoolean(String name) { return (boolean) value(name, boolean.class); }
	@Override public byte readByte(String name) { return (byte) value(name, byte.class); }
	@Override public short readShort(String name) { return (short) value(name, short.class); }
	@Override public int readInt(String name) { return (int) value(name, int.class); }
	@Override public long readLong(String name) { return (long) value(name, long.class); }
	@Override public float readFloat(String name) { return (float) value(name, float.class); }
	@Override public double readDouble(String name) { return (double) value(name, double.class); }
	@Override public java.util.Date readDate(String name) { return (java.util.Date) value(name, java.util.Date.class); }
	@Override public String readString(String name) { return (String) value(name, String.class); }
	@Override public boolean[] readBooleanArray(String name) { return (boolean[]) value(name, boolean[].class); }
	@Override public char[] readCharArray(String name) { return (char[]) value(name, char[].class); }
	@Override public byte[] readByteArray(String name) { return (byte[]) value(name, byte[].class); }
	@Override public short[] readShortArray(String name) { return (short[]) value(name, short[].class); }
	@Override public int[] readIntArray(String name) { return (int[]) value(name, int[].class); }
	@Override public long[] readLongArray(String name) { return (long[]) value(name, long[].class); }
	@Override public float[] readFloatArray(String name) { return (float[]) value(name, float[].class); }
	@Override public double[] readDoubleArray(String name) { return (double[]) value(name, double[].class); }
	@Override public String[] readStringArray(String name) { return (String[]) value(name, String[].class); }
	@Override public byte[][] readArrayOfByteArrays(String name) { return (byte[][]) value(name, byte[][].class); }
	@Override public Object readObject(String name) { return value(name, Object.class); }
	@Override public Object[] readObjectArray(String name) { return (Object[]) value(name, Object.class); }
	@Override public boolean hasField(String name) { return source.hasField(name); }
	@Override public boolean isIdentityField(String name) { return source.isIdentityField(name); }
	@Override public Object readField(String name) { return value(name, Object.class); }
	@Override public DriverPdxUnreadFields readUnreadFields() {
		Map<String, MockPdxField> unread = new LinkedHashMap<>();
		if (!ignoreUnreadFields) source.fields.forEach((name, field) -> { if (!read.contains(name)) unread.put(name, field); });
		return new MockPdxUnreadFields(unread);
	}
}
