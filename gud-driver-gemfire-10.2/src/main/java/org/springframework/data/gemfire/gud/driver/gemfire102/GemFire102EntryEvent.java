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
 * 2026-03-13: Created GemFire 10.2 EntryEvent wrapper
 */

package org.springframework.data.gemfire.gud.driver.gemfire102;

import org.apache.geode.cache.EntryEvent;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GemFire 10.2 implementation of GudEntryEvent.
 * Wraps the native EntryEvent and delegates all operations.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFire102EntryEvent<K, V> implements GudEntryEvent<K, V>, NativeWrapper<EntryEvent<K, V>> {

    private final EntryEvent<K, V> nativeEvent;

    public GemFire102EntryEvent(EntryEvent<K, V> nativeEvent) {
        this.nativeEvent = nativeEvent;
    }

    @Override
    public EntryEvent<K, V> getNative() {
        return nativeEvent;
    }

    @Override
    public K getKey() {
        return nativeEvent.getKey();
    }

    @Override
    public V getOldValue() {
        return nativeEvent.getOldValue();
    }

    @Override
    public V getNewValue() {
        return nativeEvent.getNewValue();
    }

    @Override
    public GudSerializedCacheValue<V> getSerializedOldValue() {
        // SerializedCacheValue is not commonly used, return null for now
        return null;
    }

    @Override
    public GudSerializedCacheValue<V> getSerializedNewValue() {
        // SerializedCacheValue is not commonly used, return null for now
        return null;
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
    public boolean hasNewValue() {
        return nativeEvent.getNewValue() != null;
    }

    @Override
    public boolean hasOldValue() {
        return nativeEvent.getOldValue() != null;
    }

    @Override
    public boolean isOldValueAvailable() {
        return nativeEvent.isOldValueAvailable();
    }

    @Override
    public GudTransactionId getTransactionId() {
        return nativeEvent.getTransactionId() != null 
            ? new GemFire102TransactionId(nativeEvent.getTransactionId()) 
            : null;
    }

    @Override
    public GudRegion<K, V> getRegion() {
        return new GemFire102Region<>(nativeEvent.getRegion());
    }

    @Override
    public GudOperation getOperation() {
        return toGudOperation(nativeEvent.getOperation());
    }

    @Override
    public GudDistributedMember getDistributedMember() {
        return new GemFire102DistributedMember(nativeEvent.getDistributedMember());
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
