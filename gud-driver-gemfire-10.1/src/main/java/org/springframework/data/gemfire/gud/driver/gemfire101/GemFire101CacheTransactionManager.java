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
 * 2026-03-13: Created GemFire 10.1 CacheTransactionManager adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.CacheTransactionManager;
import org.apache.geode.cache.CommitConflictException;
import org.apache.geode.cache.TransactionId;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.1 CacheTransactionManager.
 */
public class GemFire101CacheTransactionManager implements GudCacheTransactionManager, NativeWrapper<CacheTransactionManager> {

    private final CacheTransactionManager nativeManager;

    public GemFire101CacheTransactionManager(CacheTransactionManager nativeManager) {
        this.nativeManager = nativeManager;
    }

    @Override
    public CacheTransactionManager getNative() {
        return nativeManager;
    }

    @Override
    public void begin() {
        nativeManager.begin();
    }

    @Override
    public void commit() throws GudCommitConflictException {
        try {
            nativeManager.commit();
        } catch (CommitConflictException e) {
            throw new GudCommitConflictException(e.getMessage(), e);
        }
    }

    @Override
    public void rollback() {
        nativeManager.rollback();
    }

    @Override
    public GudTransactionId suspend() {
        TransactionId txId = nativeManager.suspend();
        return txId != null ? new GemFire101TransactionId(txId) : null;
    }

    @Override
    public void resume(GudTransactionId transactionId) {
        if (transactionId instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            TransactionId nativeTxId = ((NativeWrapper<TransactionId>) transactionId).getNative();
            nativeManager.resume(nativeTxId);
        }
    }

    @Override
    public boolean exists() {
        return nativeManager.exists();
    }

    @Override
    public boolean exists(GudTransactionId transactionId) {
        if (transactionId instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            TransactionId nativeTxId = ((NativeWrapper<TransactionId>) transactionId).getNative();
            return nativeManager.exists(nativeTxId);
        }
        return false;
    }

    @Override
    public GudTransactionId getTransactionId() {
        TransactionId txId = nativeManager.getTransactionId();
        return txId != null ? new GemFire101TransactionId(txId) : null;
    }

    @Override
    public boolean isSuspended(GudTransactionId transactionId) {
        if (transactionId instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            TransactionId nativeTxId = ((NativeWrapper<TransactionId>) transactionId).getNative();
            return nativeManager.isSuspended(nativeTxId);
        }
        return false;
    }

    @Override
    public boolean tryResume(GudTransactionId transactionId) {
        if (transactionId instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            TransactionId nativeTxId = ((NativeWrapper<TransactionId>) transactionId).getNative();
            return nativeManager.tryResume(nativeTxId);
        }
        return false;
    }

    @Override
    public boolean tryResume(GudTransactionId transactionId, long time, TimeUnit unit) {
        if (transactionId instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            TransactionId nativeTxId = ((NativeWrapper<TransactionId>) transactionId).getNative();
            return nativeManager.tryResume(nativeTxId, time, unit);
        }
        return false;
    }

    @Override
    public GudTransactionWriter getWriter() {
        // TransactionWriter is user-implemented, return wrapper if set
        org.apache.geode.cache.TransactionWriter writer = nativeManager.getWriter();
        return writer != null ? new GemFire101TransactionWriterWrapper(writer) : null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setWriter(GudTransactionWriter writer) {
        if (writer instanceof NativeWrapper) {
            nativeManager.setWriter(((NativeWrapper<org.apache.geode.cache.TransactionWriter>) writer).getNative());
        }
    }

    @Override
    public GudTransactionListener[] getListeners() {
        org.apache.geode.cache.TransactionListener[] listeners = nativeManager.getListeners();
        if (listeners == null) return new GudTransactionListener[0];
        GudTransactionListener[] result = new GudTransactionListener[listeners.length];
        for (int i = 0; i < listeners.length; i++) {
            result[i] = new GemFire101TransactionListenerWrapper(listeners[i]);
        }
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudTransactionListener setListener(GudTransactionListener listener) {
        if (listener instanceof NativeWrapper) {
            org.apache.geode.cache.TransactionListener old = nativeManager.setListener(
                ((NativeWrapper<org.apache.geode.cache.TransactionListener>) listener).getNative());
            return old != null ? new GemFire101TransactionListenerWrapper(old) : null;
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addListener(GudTransactionListener listener) {
        if (listener instanceof NativeWrapper) {
            nativeManager.addListener(((NativeWrapper<org.apache.geode.cache.TransactionListener>) listener).getNative());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void removeListener(GudTransactionListener listener) {
        if (listener instanceof NativeWrapper) {
            nativeManager.removeListener(((NativeWrapper<org.apache.geode.cache.TransactionListener>) listener).getNative());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initListeners(GudTransactionListener[] listeners) {
        if (listeners != null) {
            org.apache.geode.cache.TransactionListener[] nativeListeners = new org.apache.geode.cache.TransactionListener[listeners.length];
            for (int i = 0; i < listeners.length; i++) {
                if (listeners[i] instanceof NativeWrapper) {
                    nativeListeners[i] = ((NativeWrapper<org.apache.geode.cache.TransactionListener>) listeners[i]).getNative();
                }
            }
            nativeManager.initListeners(nativeListeners);
        }
    }

    @Override
    public void setDistributed(boolean distributed) {
        nativeManager.setDistributed(distributed);
    }

    @Override
    public boolean isDistributed() {
        return nativeManager.isDistributed();
    }
}
