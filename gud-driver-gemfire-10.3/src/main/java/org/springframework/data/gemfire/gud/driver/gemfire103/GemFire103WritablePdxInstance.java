/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Created GemFire 10.3 WritablePdxInstance adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import java.util.List;

import org.apache.geode.pdx.WritablePdxInstance;

import org.springframework.data.gemfire.gud.api.GudWritablePdxInstance;

/**
 * GUD API adapter for GemFire 10.3 WritablePdxInstance.
 */
public class GemFire103WritablePdxInstance implements GudWritablePdxInstance, NativeWrapper<WritablePdxInstance> {

    private final WritablePdxInstance nativeInstance;

    public GemFire103WritablePdxInstance(WritablePdxInstance nativeInstance) {
        this.nativeInstance = nativeInstance;
    }

    @Override
    public WritablePdxInstance getNative() {
        return nativeInstance;
    }

    @Override
    public void setField(String fieldName, Object value) {
        nativeInstance.setField(fieldName, value);
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
}
