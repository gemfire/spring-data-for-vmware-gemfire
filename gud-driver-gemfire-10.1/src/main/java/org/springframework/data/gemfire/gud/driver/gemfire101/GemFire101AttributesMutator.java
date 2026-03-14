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
 * 2026-03-11: Created GemFire 10.1 AttributesMutator adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import org.apache.geode.cache.AttributesMutator;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.1 AttributesMutator.
 */
public class GemFire101AttributesMutator<K, V> implements GudAttributesMutator<K, V>, NativeWrapper<AttributesMutator<K, V>> {

    private final AttributesMutator<K, V> nativeMutator;

    public GemFire101AttributesMutator(AttributesMutator<K, V> nativeMutator) {
        this.nativeMutator = nativeMutator;
    }

    @Override
    public AttributesMutator<K, V> getNative() {
        return nativeMutator;
    }

    @Override
    public GudRegion<K, V> getRegion() {
        return new GemFire101Region<>(nativeMutator.getRegion());
    }

    @Override
    public GudExpirationAttributes setRegionTimeToLive(GudExpirationAttributes timeToLive) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudExpirationAttributes setRegionIdleTimeout(GudExpirationAttributes idleTimeout) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudExpirationAttributes setEntryTimeToLive(GudExpirationAttributes timeToLive) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudExpirationAttributes setEntryIdleTimeout(GudExpirationAttributes idleTimeout) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudCustomExpiry<K, V> setCustomEntryTimeToLive(GudCustomExpiry<K, V> customExpiry) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudCustomExpiry<K, V> setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customExpiry) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudCacheLoader<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader) {
        org.apache.geode.cache.CacheLoader<K, V> oldLoader = nativeMutator.setCacheLoader(
            cacheLoader != null ? new GemFire101CacheLoaderAdapter<>(cacheLoader) : null
        );
        return null; // Return previous loader wrapped - not implemented for simplicity
    }

    @Override
    public GudCacheWriter<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
        org.apache.geode.cache.CacheWriter<K, V> oldWriter = nativeMutator.setCacheWriter(
            cacheWriter != null ? new GemFire101CacheWriterAdapter<>(cacheWriter) : null
        );
        return null; // Return previous writer wrapped - not implemented for simplicity
    }

    @Override
    public void addCacheListener(GudCacheListener<K, V> cacheListener) {
        if (cacheListener != null) {
            nativeMutator.addCacheListener(new GemFire101CacheListenerAdapter<>(cacheListener));
        }
    }

    @Override
    public void removeCacheListener(GudCacheListener<K, V> cacheListener) {
        // Note: This doesn't work perfectly because we can't match the adapter to the original listener
        // A more complete implementation would need to track the mapping
        throw new UnsupportedOperationException("Removing individual listeners not yet supported");
    }

    @Override
    @SuppressWarnings("unchecked")
    public void initCacheListeners(GudCacheListener<K, V>[] cacheListeners) {
        if (cacheListeners == null || cacheListeners.length == 0) {
            nativeMutator.initCacheListeners(null);
        } else {
            org.apache.geode.cache.CacheListener<K, V>[] nativeListeners = 
                new org.apache.geode.cache.CacheListener[cacheListeners.length];
            for (int i = 0; i < cacheListeners.length; i++) {
                nativeListeners[i] = new GemFire101CacheListenerAdapter<>(cacheListeners[i]);
            }
            nativeMutator.initCacheListeners(nativeListeners);
        }
    }

    @Override
    public void setEvictionMaximum(int maximum) {
        nativeMutator.getEvictionAttributesMutator().setMaximum(maximum);
    }

    @Override
    public void setCloningEnabled(boolean cloningEnabled) {
        nativeMutator.setCloningEnabled(cloningEnabled);
    }

    @Override
    public void addGatewaySenderId(String gatewaySenderId) {
        nativeMutator.addGatewaySenderId(gatewaySenderId);
    }

    @Override
    public void removeGatewaySenderId(String gatewaySenderId) {
        nativeMutator.removeGatewaySenderId(gatewaySenderId);
    }

    @Override
    public void addAsyncEventQueueId(String asyncEventQueueId) {
        nativeMutator.addAsyncEventQueueId(asyncEventQueueId);
    }

    @Override
    public void removeAsyncEventQueueId(String asyncEventQueueId) {
        nativeMutator.removeAsyncEventQueueId(asyncEventQueueId);
    }

    @Override
    public GudEvictionAttributesMutator getEvictionAttributesMutator() {
        return new GemFire101EvictionAttributesMutator(nativeMutator.getEvictionAttributesMutator());
    }

    @Override
    public boolean getCloningEnabled() {
        return nativeMutator.getCloningEnabled();
    }
}
