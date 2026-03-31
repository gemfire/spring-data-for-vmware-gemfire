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
 * 2026-03-13: Created GemFire 10.3 CollectionType adapter
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.query.types.CollectionType;

import org.springframework.data.gemfire.gud.api.GudCollectionType;
import org.springframework.data.gemfire.gud.api.GudObjectType;

/**
 * GUD API adapter for GemFire 10.3 CollectionType.
 */
public class GemFireCollectionType implements GudCollectionType, NativeWrapper<CollectionType> {

    private final CollectionType nativeType;

    public GemFireCollectionType(CollectionType nativeType) {
        this.nativeType = nativeType;
    }

    @Override
    public CollectionType getNative() {
        return nativeType;
    }

    @Override
    public GudObjectType getElementType() {
        return new GemFireObjectType(nativeType.getElementType());
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

    @Override
    public boolean allowsDuplicates() {
        return nativeType.allowsDuplicates();
    }

    @Override
    public boolean isOrdered() {
        return nativeType.isOrdered();
    }
}
