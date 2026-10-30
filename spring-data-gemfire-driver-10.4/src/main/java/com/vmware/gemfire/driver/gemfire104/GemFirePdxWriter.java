// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import java.util.function.Supplier;
import org.apache.geode.pdx.*;

/** Wraps a native {@link PdxWriter}; native failures are translated, driver failures raised by nested callbacks pass through. */
final class GemFirePdxWriter implements DriverPdxWriter {
	private final PdxWriter writer;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	private final GemFirePdxValues values = new GemFirePdxValues();
	GemFirePdxWriter(PdxWriter writer) { this.writer = writer; }
	private DriverPdxWriter invoke(Runnable action) {
		try { action.run(); return this; }
		catch (RuntimeException ex) { throw exceptions.translate("pdxWriter", ex); }
	}
	@Override public DriverPdxWriter writeChar(String name, char value) { return invoke(() -> writer.writeChar(name, value)); }
	@Override public DriverPdxWriter writeBoolean(String name, boolean value) { return invoke(() -> writer.writeBoolean(name, value)); }
	@Override public DriverPdxWriter writeByte(String name, byte value) { return invoke(() -> writer.writeByte(name, value)); }
	@Override public DriverPdxWriter writeShort(String name, short value) { return invoke(() -> writer.writeShort(name, value)); }
	@Override public DriverPdxWriter writeInt(String name, int value) { return invoke(() -> writer.writeInt(name, value)); }
	@Override public DriverPdxWriter writeLong(String name, long value) { return invoke(() -> writer.writeLong(name, value)); }
	@Override public DriverPdxWriter writeFloat(String name, float value) { return invoke(() -> writer.writeFloat(name, value)); }
	@Override public DriverPdxWriter writeDouble(String name, double value) { return invoke(() -> writer.writeDouble(name, value)); }
	@Override public DriverPdxWriter writeDate(String name, java.util.Date value) { return invoke(() -> writer.writeDate(name, value)); }
	@Override public DriverPdxWriter writeString(String name, String value) { return invoke(() -> writer.writeString(name, value)); }
	@Override public DriverPdxWriter writeObject(String name, Object value) { return invoke(() -> writer.writeObject(name, values.toNative(value))); }
	@Override public DriverPdxWriter writeObject(String name, Object value, boolean checkPortability) { return invoke(() -> writer.writeObject(name, values.toNative(value), checkPortability)); }
	@Override public DriverPdxWriter writeBooleanArray(String name, boolean[] value) { return invoke(() -> writer.writeBooleanArray(name, value)); }
	@Override public DriverPdxWriter writeCharArray(String name, char[] value) { return invoke(() -> writer.writeCharArray(name, value)); }
	@Override public DriverPdxWriter writeByteArray(String name, byte[] value) { return invoke(() -> writer.writeByteArray(name, value)); }
	@Override public DriverPdxWriter writeShortArray(String name, short[] value) { return invoke(() -> writer.writeShortArray(name, value)); }
	@Override public DriverPdxWriter writeIntArray(String name, int[] value) { return invoke(() -> writer.writeIntArray(name, value)); }
	@Override public DriverPdxWriter writeLongArray(String name, long[] value) { return invoke(() -> writer.writeLongArray(name, value)); }
	@Override public DriverPdxWriter writeFloatArray(String name, float[] value) { return invoke(() -> writer.writeFloatArray(name, value)); }
	@Override public DriverPdxWriter writeDoubleArray(String name, double[] value) { return invoke(() -> writer.writeDoubleArray(name, value)); }
	@Override public DriverPdxWriter writeStringArray(String name, String[] value) { return invoke(() -> writer.writeStringArray(name, value)); }
	@Override public DriverPdxWriter writeObjectArray(String name, Object[] value) { return invoke(() -> writer.writeObjectArray(name, (Object[]) values.toNative(value))); }
	@Override public DriverPdxWriter writeObjectArray(String name, Object[] value, boolean checkPortability) { return invoke(() -> writer.writeObjectArray(name, (Object[]) values.toNative(value), checkPortability)); }
	@Override public DriverPdxWriter writeArrayOfByteArrays(String name, byte[][] value) { return invoke(() -> writer.writeArrayOfByteArrays(name, value)); }
	@Override public <CT, VT extends CT> DriverPdxWriter writeField(String name, VT value, Class<CT> type) { return invoke(() -> writer.writeField(name, (VT) values.toNative(value), type)); }
	@Override public <CT, VT extends CT> DriverPdxWriter writeField(String name, VT value, Class<CT> type, boolean checkPortability) { return invoke(() -> writer.writeField(name, (VT) values.toNative(value), type, checkPortability)); }
	@Override public DriverPdxWriter writeUnreadFields(DriverPdxUnreadFields unread) {
		if (!(unread instanceof GemFirePdxUnreadFields own)) throw new IllegalArgumentException("Unread fields were not produced by this provider");
		return invoke(() -> writer.writeUnreadFields(own.value));
	}
	@Override public DriverPdxWriter markIdentityField(String name) { return invoke(() -> writer.markIdentityField(name)); }
}
