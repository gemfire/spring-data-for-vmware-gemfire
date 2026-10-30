// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.mock;

import com.vmware.gemfire.driver.spi.*;
import java.util.*;

/** Local writer producing a {@link MockPdxInstance}; unread fields must be carried first, as natively. */
final class MockPdxWriter implements DriverPdxWriter {
	private final MockPdxInstanceFactory factory;
	private final String className;
	private boolean wrote;
	MockPdxWriter(String className) { this.className = className; this.factory = new MockPdxInstanceFactory(className); }
	MockPdxInstance create() { return (MockPdxInstance) factory.create(); }
	private DriverPdxWriter done(DriverPdxInstanceFactory ignored) { wrote = true; return this; }
	@Override public DriverPdxWriter writeChar(String name, char value) { return done(factory.writeChar(name, value)); }
	@Override public DriverPdxWriter writeBoolean(String name, boolean value) { return done(factory.writeBoolean(name, value)); }
	@Override public DriverPdxWriter writeByte(String name, byte value) { return done(factory.writeByte(name, value)); }
	@Override public DriverPdxWriter writeShort(String name, short value) { return done(factory.writeShort(name, value)); }
	@Override public DriverPdxWriter writeInt(String name, int value) { return done(factory.writeInt(name, value)); }
	@Override public DriverPdxWriter writeLong(String name, long value) { return done(factory.writeLong(name, value)); }
	@Override public DriverPdxWriter writeFloat(String name, float value) { return done(factory.writeFloat(name, value)); }
	@Override public DriverPdxWriter writeDouble(String name, double value) { return done(factory.writeDouble(name, value)); }
	@Override public DriverPdxWriter writeDate(String name, java.util.Date value) { return done(factory.writeDate(name, value)); }
	@Override public DriverPdxWriter writeString(String name, String value) { return done(factory.writeString(name, value)); }
	@Override public DriverPdxWriter writeBooleanArray(String name, boolean[] value) { return done(factory.writeBooleanArray(name, value)); }
	@Override public DriverPdxWriter writeCharArray(String name, char[] value) { return done(factory.writeCharArray(name, value)); }
	@Override public DriverPdxWriter writeByteArray(String name, byte[] value) { return done(factory.writeByteArray(name, value)); }
	@Override public DriverPdxWriter writeShortArray(String name, short[] value) { return done(factory.writeShortArray(name, value)); }
	@Override public DriverPdxWriter writeIntArray(String name, int[] value) { return done(factory.writeIntArray(name, value)); }
	@Override public DriverPdxWriter writeLongArray(String name, long[] value) { return done(factory.writeLongArray(name, value)); }
	@Override public DriverPdxWriter writeFloatArray(String name, float[] value) { return done(factory.writeFloatArray(name, value)); }
	@Override public DriverPdxWriter writeDoubleArray(String name, double[] value) { return done(factory.writeDoubleArray(name, value)); }
	@Override public DriverPdxWriter writeStringArray(String name, String[] value) { return done(factory.writeStringArray(name, value)); }
	@Override public DriverPdxWriter writeObject(String name, Object value) { return done(factory.writeObject(name, value)); }
	@Override public DriverPdxWriter writeObject(String name, Object value, boolean checkPortability) { return done(factory.writeObject(name, value, checkPortability)); }
	@Override public DriverPdxWriter writeObjectArray(String name, Object[] value) { return done(factory.writeObjectArray(name, value)); }
	@Override public DriverPdxWriter writeObjectArray(String name, Object[] value, boolean checkPortability) { return done(factory.writeObjectArray(name, value, checkPortability)); }
	@Override public DriverPdxWriter writeArrayOfByteArrays(String name, byte[][] value) { return done(factory.writeArrayOfByteArrays(name, value)); }
	@Override public <CT, VT extends CT> DriverPdxWriter writeField(String name, VT value, Class<CT> type) { return done(factory.writeField(name, value, type)); }
	@Override public <CT, VT extends CT> DriverPdxWriter writeField(String name, VT value, Class<CT> type, boolean checkPortability) { return done(factory.writeField(name, value, type, checkPortability)); }
	@Override public DriverPdxWriter writeUnreadFields(DriverPdxUnreadFields unread) {
		if (!(unread instanceof MockPdxUnreadFields own)) throw new IllegalArgumentException("Unread fields were not produced by this provider");
		if (wrote) throw new IllegalStateException("writeUnreadFields must be the first writer operation");
		own.fields().forEach(factory::putRaw);
		wrote = true;
		return this;
	}
	@Override public DriverPdxWriter markIdentityField(String name) { factory.markIdentityField(name); return this; }
}
