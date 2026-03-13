/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 PdxInstance adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.List;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.data.gemfire.gud.api.GudPdxInstance;
import org.springframework.data.gemfire.gud.api.GudWritablePdxInstance;

/**
 * GUD API adapter for GemFire 10.3 PdxInstance.
 */
public class GemFire103PdxInstance implements GudPdxInstance, NativeWrapper<PdxInstance> {

    private final PdxInstance nativeInstance;

    public GemFire103PdxInstance(PdxInstance nativeInstance) {
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
        return new GemFire103WritablePdxInstance(nativeInstance.createWriter());
    }

    @Override
    public boolean isEnum() {
        return nativeInstance.isEnum();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GemFire103PdxInstance) {
            return nativeInstance.equals(((GemFire103PdxInstance) obj).nativeInstance);
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
