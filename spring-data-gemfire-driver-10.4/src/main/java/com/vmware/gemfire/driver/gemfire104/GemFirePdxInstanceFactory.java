/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import org.apache.geode.pdx.PdxInstanceFactory;
import java.util.function.Supplier;

final class GemFirePdxInstanceFactory implements DriverPdxInstanceFactory {
	private final PdxInstanceFactory factory;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	GemFirePdxInstanceFactory(PdxInstanceFactory factory) { this.factory = factory; }
	@Override public DriverPdxInstance create() { return invoke(() -> new GemFirePdxInstance(factory.create())); }
	@Override public DriverPdxInstanceFactory writeChar(String name, char value) { return invoke(() -> { factory.writeChar(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeBoolean(String name, boolean value) { return invoke(() -> { factory.writeBoolean(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeByte(String name, byte value) { return invoke(() -> { factory.writeByte(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeShort(String name, short value) { return invoke(() -> { factory.writeShort(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeInt(String name, int value) { return invoke(() -> { factory.writeInt(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeLong(String name, long value) { return invoke(() -> { factory.writeLong(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeFloat(String name, float value) { return invoke(() -> { factory.writeFloat(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeDouble(String name, double value) { return invoke(() -> { factory.writeDouble(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeDate(String name, java.util.Date value) { return invoke(() -> { factory.writeDate(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeString(String name, String value) { return invoke(() -> { factory.writeString(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeObject(String name, Object value) { return invoke(() -> { factory.writeObject(name, new GemFirePdxValues().toNative(value)); return this; }); }
	@Override public DriverPdxInstanceFactory writeBooleanArray(String name, boolean[] value) { return invoke(() -> { factory.writeBooleanArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeCharArray(String name, char[] value) { return invoke(() -> { factory.writeCharArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeByteArray(String name, byte[] value) { return invoke(() -> { factory.writeByteArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeShortArray(String name, short[] value) { return invoke(() -> { factory.writeShortArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeIntArray(String name, int[] value) { return invoke(() -> { factory.writeIntArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeLongArray(String name, long[] value) { return invoke(() -> { factory.writeLongArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeFloatArray(String name, float[] value) { return invoke(() -> { factory.writeFloatArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeDoubleArray(String name, double[] value) { return invoke(() -> { factory.writeDoubleArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeStringArray(String name, String[] value) { return invoke(() -> { factory.writeStringArray(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeObjectArray(String name, Object[] value) { return invoke(() -> { factory.writeObjectArray(name, (Object[]) new GemFirePdxValues().toNative(value)); return this; }); }
	@Override public DriverPdxInstanceFactory writeArrayOfByteArrays(String name, byte[][] value) { return invoke(() -> { factory.writeArrayOfByteArrays(name, value); return this; }); }
	@Override public DriverPdxInstanceFactory writeObject(String name, Object value, boolean checkPortability) { return invoke(() -> { factory.writeObject(name, new GemFirePdxValues().toNative(value), checkPortability); return this; }); }
	@Override public DriverPdxInstanceFactory writeObjectArray(String name, Object[] value, boolean checkPortability) { return invoke(() -> { factory.writeObjectArray(name, (Object[]) new GemFirePdxValues().toNative(value), checkPortability); return this; }); }
	@Override public <CT, VT extends CT> DriverPdxInstanceFactory writeField(String name, VT value, Class<CT> type) {
		return writeField(name, value, type, false);
	}
	@Override @SuppressWarnings({"rawtypes", "unchecked"})
	public <CT, VT extends CT> DriverPdxInstanceFactory writeField(String name, VT value, Class<CT> type, boolean checkPortability) {
		return invoke(() -> { factory.writeField(name, new GemFirePdxValues().toNative(value), (Class) type, checkPortability); return this; });
	}
	@Override public DriverPdxInstanceFactory markIdentityField(String name) { return invoke(() -> { factory.markIdentityField(name); return this; }); }
	@Override public DriverPdxInstanceFactory neverDeserialize() { return invoke(() -> { factory.neverDeserialize(); return this; }); }
	private <T> T invoke(Supplier<T> action) {
		try { return action.get(); } catch (RuntimeException ex) { throw exceptions.translate("pdxInstanceFactory", ex); }
	}
}
