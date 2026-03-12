/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudPdxReader interface as 1:1 mapping of GemFire PdxReader
 */

package org.springframework.data.gemfire.gud.api;

import java.util.Date;
import java.util.List;

/**
 * GUD API abstraction for GemFire PdxReader interface.
 * Reads PDX serialized data.
 */
public interface GudPdxReader {

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
    char[] readCharArray(String fieldName);
    boolean[] readBooleanArray(String fieldName);
    byte[] readByteArray(String fieldName);
    short[] readShortArray(String fieldName);
    int[] readIntArray(String fieldName);
    long[] readLongArray(String fieldName);
    float[] readFloatArray(String fieldName);
    double[] readDoubleArray(String fieldName);
    String[] readStringArray(String fieldName);
    Object[] readObjectArray(String fieldName);
    byte[][] readArrayOfByteArrays(String fieldName);
    Date readDate(String fieldName);

    boolean hasField(String fieldName);
    boolean isIdentityField(String fieldName);

    Object readField(String fieldName);

    List<String> getFieldNames();
}
