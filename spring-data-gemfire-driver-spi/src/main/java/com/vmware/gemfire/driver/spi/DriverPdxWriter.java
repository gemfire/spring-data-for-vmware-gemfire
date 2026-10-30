// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Provider-neutral PDX writer handed to {@link DriverPdxSerializer} and {@link DriverPdxSerializable}.
 * Every write returns this writer. Failures from the provider surface as {@link GemFireDriverException}s;
 * unchecked exceptions thrown by nested callbacks propagate to the operation that triggered serialization.
 * {@link #writeUnreadFields} must be the first writer operation, as with the native writer.
 */
public interface DriverPdxWriter {
	DriverPdxWriter writeChar(String fieldName, char value);
	DriverPdxWriter writeBoolean(String fieldName, boolean value);
	DriverPdxWriter writeByte(String fieldName, byte value);
	DriverPdxWriter writeShort(String fieldName, short value);
	DriverPdxWriter writeInt(String fieldName, int value);
	DriverPdxWriter writeLong(String fieldName, long value);
	DriverPdxWriter writeFloat(String fieldName, float value);
	DriverPdxWriter writeDouble(String fieldName, double value);
	DriverPdxWriter writeDate(String fieldName, java.util.Date value);
	DriverPdxWriter writeString(String fieldName, String value);
	DriverPdxWriter writeObject(String fieldName, Object value);
	DriverPdxWriter writeObject(String fieldName, Object value, boolean checkPortability);
	DriverPdxWriter writeBooleanArray(String fieldName, boolean[] value);
	DriverPdxWriter writeCharArray(String fieldName, char[] value);
	DriverPdxWriter writeByteArray(String fieldName, byte[] value);
	DriverPdxWriter writeShortArray(String fieldName, short[] value);
	DriverPdxWriter writeIntArray(String fieldName, int[] value);
	DriverPdxWriter writeLongArray(String fieldName, long[] value);
	DriverPdxWriter writeFloatArray(String fieldName, float[] value);
	DriverPdxWriter writeDoubleArray(String fieldName, double[] value);
	DriverPdxWriter writeStringArray(String fieldName, String[] value);
	DriverPdxWriter writeObjectArray(String fieldName, Object[] value);
	DriverPdxWriter writeObjectArray(String fieldName, Object[] value, boolean checkPortability);
	DriverPdxWriter writeArrayOfByteArrays(String fieldName, byte[][] value);
	<CT, VT extends CT> DriverPdxWriter writeField(String fieldName, VT value, Class<CT> fieldType);
	<CT, VT extends CT> DriverPdxWriter writeField(String fieldName, VT value, Class<CT> fieldType, boolean checkPortability);
	DriverPdxWriter writeUnreadFields(DriverPdxUnreadFields unread);
	DriverPdxWriter markIdentityField(String fieldName);
}
