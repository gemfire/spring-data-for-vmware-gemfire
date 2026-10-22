/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/** Single-use PDX instance construction, with provider-owned schema validation and serialization. */
public interface DriverPdxInstanceFactory {
	DriverPdxInstance create();
	DriverPdxInstanceFactory writeChar(String name, char value);
	DriverPdxInstanceFactory writeBoolean(String name, boolean value);
	DriverPdxInstanceFactory writeByte(String name, byte value);
	DriverPdxInstanceFactory writeShort(String name, short value);
	DriverPdxInstanceFactory writeInt(String name, int value);
	DriverPdxInstanceFactory writeLong(String name, long value);
	DriverPdxInstanceFactory writeFloat(String name, float value);
	DriverPdxInstanceFactory writeDouble(String name, double value);
	DriverPdxInstanceFactory writeDate(String name, java.util.Date value);
	DriverPdxInstanceFactory writeString(String name, String value);
	DriverPdxInstanceFactory writeObject(String name, Object value);
	DriverPdxInstanceFactory writeBooleanArray(String name, boolean[] value);
	DriverPdxInstanceFactory writeCharArray(String name, char[] value);
	DriverPdxInstanceFactory writeByteArray(String name, byte[] value);
	DriverPdxInstanceFactory writeShortArray(String name, short[] value);
	DriverPdxInstanceFactory writeIntArray(String name, int[] value);
	DriverPdxInstanceFactory writeLongArray(String name, long[] value);
	DriverPdxInstanceFactory writeFloatArray(String name, float[] value);
	DriverPdxInstanceFactory writeDoubleArray(String name, double[] value);
	DriverPdxInstanceFactory writeStringArray(String name, String[] value);
	DriverPdxInstanceFactory writeObjectArray(String name, Object[] value);
	DriverPdxInstanceFactory writeArrayOfByteArrays(String name, byte[][] value);
	DriverPdxInstanceFactory writeObject(String name, Object value, boolean checkPortability);
	DriverPdxInstanceFactory writeObjectArray(String name, Object[] value, boolean checkPortability);
	<CT, VT extends CT> DriverPdxInstanceFactory writeField(String name, VT value, Class<CT> type);
	<CT, VT extends CT> DriverPdxInstanceFactory writeField(String name, VT value, Class<CT> type, boolean checkPortability);
	DriverPdxInstanceFactory markIdentityField(String name);
	DriverPdxInstanceFactory neverDeserialize();
}
