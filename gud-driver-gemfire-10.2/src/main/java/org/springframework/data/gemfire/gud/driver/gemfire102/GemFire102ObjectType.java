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
 * 2026-03-13: Created GemFire 10.2 ObjectType adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire102;

import org.apache.geode.cache.query.types.ObjectType;

import org.springframework.data.gemfire.gud.api.GudObjectType;

/**
 * GUD API adapter for GemFire 10.2 ObjectType.
 */
public class GemFire102ObjectType implements GudObjectType, NativeWrapper<ObjectType> {

    private final ObjectType nativeType;

    public GemFire102ObjectType(ObjectType nativeType) {
        this.nativeType = nativeType;
    }

    @Override
    public ObjectType getNative() {
        return nativeType;
    }

    @Override
    public Class<?> resolveClass() {
        try {
            return nativeType.resolveClass();
        } catch (Exception e) {
            throw new RuntimeException("Failed to resolve class", e);
        }
    }

    @Override
    public String getSimpleClassName() {
        return nativeType.getSimpleClassName();
    }

    @Override
    public boolean isCollectionType() {
        return nativeType.isCollectionType();
    }

    @Override
    public boolean isMapType() {
        return nativeType.isMapType();
    }

    @Override
    public boolean isStructType() {
        return nativeType.isStructType();
    }
}
