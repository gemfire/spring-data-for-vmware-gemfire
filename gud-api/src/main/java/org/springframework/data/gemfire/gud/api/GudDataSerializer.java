/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDataSerializer interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * GUD API abstraction for GemFire DataSerializer.
 */
public abstract class GudDataSerializer {

    public abstract boolean toData(Object o, DataOutput out) throws IOException;
    
    public abstract Object fromData(DataInput in) throws IOException, ClassNotFoundException;
    
    public abstract int getId();
    
    public abstract Class<?>[] getSupportedClasses();

    public static void writeClass(Class<?> type, DataOutput out) throws IOException {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static Class<?> readClass(DataInput in) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }

    public static void register(Class<? extends GudDataSerializer> serializerClass) {
        throw new UnsupportedOperationException("Must be implemented by driver");
    }
}
