// Copyright 2026 Broadcom. All Rights Reserved.
package com.vmware.gemfire.driver.spi;

/** Provider-neutral PDX reader handed to {@link DriverPdxSerializer#fromData} and {@link DriverPdxSerializable#fromData}.
 * Reading a field that is absent from the serialized form yields the type's default; a type mismatch fails.
 * {@link #readUnreadFields()} selects manual preservation of fields this class version does not know.
 */
public interface DriverPdxReader {
	char readChar(String fieldName);
	boolean readBoolean(String fieldName);
	byte readByte(String fieldName);
	short readShort(String fieldName);
	int readInt(String fieldName);
	long readLong(String fieldName);
	float readFloat(String fieldName);
	double readDouble(String fieldName);
	String readString(String fieldName);
	Object readObject(String fieldName);
	java.util.Date readDate(String fieldName);
	boolean[] readBooleanArray(String fieldName);
	char[] readCharArray(String fieldName);
	byte[] readByteArray(String fieldName);
	short[] readShortArray(String fieldName);
	int[] readIntArray(String fieldName);
	long[] readLongArray(String fieldName);
	float[] readFloatArray(String fieldName);
	double[] readDoubleArray(String fieldName);
	String[] readStringArray(String fieldName);
	Object[] readObjectArray(String fieldName);
	byte[][] readArrayOfByteArrays(String fieldName);
	boolean hasField(String fieldName);
	boolean isIdentityField(String fieldName);
	Object readField(String fieldName);
	DriverPdxUnreadFields readUnreadFields();
}
