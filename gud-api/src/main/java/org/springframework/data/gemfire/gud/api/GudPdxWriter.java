/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPdxWriter interface as 1:1 mapping of GemFire PdxWriter
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Date;

/**
 * GUD API abstraction for GemFire PdxWriter interface.
 * Writes PDX serialized data.
 */
public interface GudPdxWriter {

    GudPdxWriter writeChar(String fieldName, char value);
    GudPdxWriter writeBoolean(String fieldName, boolean value);
    GudPdxWriter writeByte(String fieldName, byte value);
    GudPdxWriter writeShort(String fieldName, short value);
    GudPdxWriter writeInt(String fieldName, int value);
    GudPdxWriter writeLong(String fieldName, long value);
    GudPdxWriter writeFloat(String fieldName, float value);
    GudPdxWriter writeDouble(String fieldName, double value);
    GudPdxWriter writeString(String fieldName, String value);
    GudPdxWriter writeObject(String fieldName, Object object);
    GudPdxWriter writeObject(String fieldName, Object object, boolean checkPortability);
    GudPdxWriter writeCharArray(String fieldName, char[] value);
    GudPdxWriter writeBooleanArray(String fieldName, boolean[] value);
    GudPdxWriter writeByteArray(String fieldName, byte[] value);
    GudPdxWriter writeShortArray(String fieldName, short[] value);
    GudPdxWriter writeIntArray(String fieldName, int[] value);
    GudPdxWriter writeLongArray(String fieldName, long[] value);
    GudPdxWriter writeFloatArray(String fieldName, float[] value);
    GudPdxWriter writeDoubleArray(String fieldName, double[] value);
    GudPdxWriter writeStringArray(String fieldName, String[] value);
    GudPdxWriter writeObjectArray(String fieldName, Object[] value);
    GudPdxWriter writeObjectArray(String fieldName, Object[] value, boolean checkPortability);
    GudPdxWriter writeArrayOfByteArrays(String fieldName, byte[][] value);
    GudPdxWriter writeDate(String fieldName, Date value);

    GudPdxWriter markIdentityField(String fieldName);

    GudPdxWriter writeField(String fieldName, Object fieldValue, Class<?> fieldType);
    GudPdxWriter writeField(String fieldName, Object fieldValue, Class<?> fieldType, boolean checkPortability);

    GudPdxWriter writeUnreadFields(GudPdxUnreadFields unreadFields);
}
