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
 * 2026-03-13: Created GemFire 10.0 RegionEvent wrapper
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.RegionEvent;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GemFire 10.0 implementation of GudRegionEvent.
 * Wraps the native RegionEvent and delegates all operations.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFireRegionEvent<K, V> implements GudRegionEvent<K, V>, NativeWrapper<RegionEvent<K, V>> {

    private final RegionEvent<K, V> nativeEvent;

    public GemFireRegionEvent(RegionEvent<K, V> nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    @Override
    public RegionEvent<K, V> getNative() {
        return nativeEvent;
    }

    @Override
    public Object getCallbackArgument() {
        return nativeEvent.getCallbackArgument();
    }

    @Override
    public boolean isCallbackArgumentAvailable() {
        return nativeEvent.isCallbackArgumentAvailable();
    }

    @Override
    public boolean isReinitializing() {
        return nativeEvent.isReinitializing();
    }

    @Override
    public GudRegion<K, V> getRegion() {
        return new GemFireRegion<>(nativeEvent.getRegion());
    }

    @Override
    public GudOperation getOperation() {
        return toGudOperation(nativeEvent.getOperation());
    }

    @Override
    public GudDistributedMember getDistributedMember() {
        return new GemFireDistributedMember(nativeEvent.getDistributedMember());
    }

    @Override
    public boolean isOriginRemote() {
        return nativeEvent.isOriginRemote();
    }

    @Override
    public boolean isExpiration() {
        return nativeEvent.getOperation().isExpiration();
    }

    @Override
    public boolean isDistributed() {
        return nativeEvent.getOperation().isDistributed();
    }

    private GudOperation toGudOperation(org.apache.geode.cache.Operation op) {
        if (op.isCreate()) return GudOperation.CREATE;
        if (op.isUpdate()) return GudOperation.UPDATE;
        if (op.isDestroy()) return GudOperation.DESTROY;
        if (op.isInvalidate()) return GudOperation.INVALIDATE;
        if (op.isRegionDestroy()) return GudOperation.REGION_DESTROY;
        if (op.isClear()) return GudOperation.REGION_CLEAR;
        return GudOperation.UNKNOWN;
    }
}
