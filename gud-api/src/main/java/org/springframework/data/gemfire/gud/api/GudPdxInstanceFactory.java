/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPdxInstanceFactory interface as 1:1 mapping of GemFire PdxInstanceFactory
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Date;

/**
 * GUD API abstraction for GemFire PdxInstanceFactory interface.
 * Factory for creating PdxInstance objects.
 */
public interface GudPdxInstanceFactory {

    GudPdxInstanceFactory writeChar(String fieldName, char value);
    GudPdxInstanceFactory writeBoolean(String fieldName, boolean value);
    GudPdxInstanceFactory writeByte(String fieldName, byte value);
    GudPdxInstanceFactory writeShort(String fieldName, short value);
    GudPdxInstanceFactory writeInt(String fieldName, int value);
    GudPdxInstanceFactory writeLong(String fieldName, long value);
    GudPdxInstanceFactory writeFloat(String fieldName, float value);
    GudPdxInstanceFactory writeDouble(String fieldName, double value);
    GudPdxInstanceFactory writeString(String fieldName, String value);
    GudPdxInstanceFactory writeObject(String fieldName, Object value);
    GudPdxInstanceFactory writeObject(String fieldName, Object value, boolean checkPortability);
    GudPdxInstanceFactory writeCharArray(String fieldName, char[] value);
    GudPdxInstanceFactory writeBooleanArray(String fieldName, boolean[] value);
    GudPdxInstanceFactory writeByteArray(String fieldName, byte[] value);
    GudPdxInstanceFactory writeShortArray(String fieldName, short[] value);
    GudPdxInstanceFactory writeIntArray(String fieldName, int[] value);
    GudPdxInstanceFactory writeLongArray(String fieldName, long[] value);
    GudPdxInstanceFactory writeFloatArray(String fieldName, float[] value);
    GudPdxInstanceFactory writeDoubleArray(String fieldName, double[] value);
    GudPdxInstanceFactory writeStringArray(String fieldName, String[] value);
    GudPdxInstanceFactory writeObjectArray(String fieldName, Object[] value);
    GudPdxInstanceFactory writeObjectArray(String fieldName, Object[] value, boolean checkPortability);
    GudPdxInstanceFactory writeArrayOfByteArrays(String fieldName, byte[][] value);
    GudPdxInstanceFactory writeDate(String fieldName, Date value);

    GudPdxInstanceFactory markIdentityField(String fieldName);

    GudPdxInstanceFactory writeField(String fieldName, Object fieldValue, Class<?> fieldType);
    GudPdxInstanceFactory writeField(String fieldName, Object fieldValue, Class<?> fieldType, boolean checkPortability);

    GudPdxInstanceFactory neverDeserialize();

    GudPdxInstance create();
}
