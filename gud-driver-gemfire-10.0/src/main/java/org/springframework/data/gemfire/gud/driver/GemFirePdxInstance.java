/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

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
 * 2026-03-13: Created GemFire 10.0 PdxInstance adapter
 */

package org.springframework.data.gemfire.gud.driver;

import java.util.List;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.data.gemfire.gud.api.GudPdxInstance;
import org.springframework.data.gemfire.gud.api.GudWritablePdxInstance;

/**
 * GUD API adapter for GemFire 10.0 PdxInstance.
 */
public class GemFirePdxInstance implements GudPdxInstance, NativeWrapper<PdxInstance> {

    private final PdxInstance nativeInstance;

    public GemFirePdxInstance(PdxInstance nativeInstance) {
        this.nativeInstance = nativeInstance;
    }

    @Override
    public PdxInstance getNative() {
        return nativeInstance;
    }

    @Override
    public String getClassName() {
        return nativeInstance.getClassName();
    }

    @Override
    public boolean isDeserializable() {
        return nativeInstance.isDeserializable();
    }

    @Override
    public Object getObject() {
        return nativeInstance.getObject();
    }

    @Override
    public boolean hasField(String fieldName) {
        return nativeInstance.hasField(fieldName);
    }

    @Override
    public List<String> getFieldNames() {
        return nativeInstance.getFieldNames();
    }

    @Override
    public boolean isIdentityField(String fieldName) {
        return nativeInstance.isIdentityField(fieldName);
    }

    @Override
    public Object getField(String fieldName) {
        return nativeInstance.getField(fieldName);
    }

    @Override
    public GudWritablePdxInstance createWriter() {
        return new GemFireWritablePdxInstance(nativeInstance.createWriter());
    }

    @Override
    public boolean isEnum() {
        return nativeInstance.isEnum();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GemFirePdxInstance) {
            return nativeInstance.equals(((GemFirePdxInstance) obj).nativeInstance);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nativeInstance.hashCode();
    }

    @Override
    public String toString() {
        return nativeInstance.toString();
    }
}
