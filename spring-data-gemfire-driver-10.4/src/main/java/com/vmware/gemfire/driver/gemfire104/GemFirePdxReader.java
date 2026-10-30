// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.gemfire104;

import com.vmware.gemfire.driver.spi.*;
import java.util.function.Supplier;
import org.apache.geode.pdx.*;

/** Wraps a native {@link PdxReader}; values are converted to provider-neutral types. */
final class GemFirePdxReader implements DriverPdxReader {
	private final PdxReader reader;
	private final GemFireExceptionMapper exceptions = new GemFireExceptionMapper();
	private final GemFirePdxValues values = new GemFirePdxValues();
	GemFirePdxReader(PdxReader reader) { this.reader = reader; }
	private <T> T invoke(Supplier<T> action) {
		try { return action.get(); }
		catch (RuntimeException ex) { throw exceptions.translate("pdxReader", ex); }
	}
	@Override public char readChar(String name) { return invoke(() -> reader.readChar(name)); }
	@Override public boolean readBoolean(String name) { return invoke(() -> reader.readBoolean(name)); }
	@Override public byte readByte(String name) { return invoke(() -> reader.readByte(name)); }
	@Override public short readShort(String name) { return invoke(() -> reader.readShort(name)); }
	@Override public int readInt(String name) { return invoke(() -> reader.readInt(name)); }
	@Override public long readLong(String name) { return invoke(() -> reader.readLong(name)); }
	@Override public float readFloat(String name) { return invoke(() -> reader.readFloat(name)); }
	@Override public double readDouble(String name) { return invoke(() -> reader.readDouble(name)); }
	@Override public java.util.Date readDate(String name) { return invoke(() -> reader.readDate(name)); }
	@Override public String readString(String name) { return invoke(() -> reader.readString(name)); }
	@Override public boolean[] readBooleanArray(String name) { return invoke(() -> reader.readBooleanArray(name)); }
	@Override public char[] readCharArray(String name) { return invoke(() -> reader.readCharArray(name)); }
	@Override public byte[] readByteArray(String name) { return invoke(() -> reader.readByteArray(name)); }
	@Override public short[] readShortArray(String name) { return invoke(() -> reader.readShortArray(name)); }
	@Override public int[] readIntArray(String name) { return invoke(() -> reader.readIntArray(name)); }
	@Override public long[] readLongArray(String name) { return invoke(() -> reader.readLongArray(name)); }
	@Override public float[] readFloatArray(String name) { return invoke(() -> reader.readFloatArray(name)); }
	@Override public double[] readDoubleArray(String name) { return invoke(() -> reader.readDoubleArray(name)); }
	@Override public String[] readStringArray(String name) { return invoke(() -> reader.readStringArray(name)); }
	@Override public byte[][] readArrayOfByteArrays(String name) { return invoke(() -> reader.readArrayOfByteArrays(name)); }
	@Override public Object readObject(String name) { return invoke(() -> values.fromNative(reader.readObject(name))); }
	@Override public Object[] readObjectArray(String name) { return invoke(() -> (Object[]) values.fromNative(reader.readObjectArray(name))); }
	@Override public boolean hasField(String name) { return invoke(() -> reader.hasField(name)); }
	@Override public boolean isIdentityField(String name) { return invoke(() -> reader.isIdentityField(name)); }
	@Override public Object readField(String name) { return invoke(() -> values.fromNative(reader.readField(name))); }
	@Override public DriverPdxUnreadFields readUnreadFields() { return invoke(() -> new GemFirePdxUnreadFields(reader.readUnreadFields())); }
}
