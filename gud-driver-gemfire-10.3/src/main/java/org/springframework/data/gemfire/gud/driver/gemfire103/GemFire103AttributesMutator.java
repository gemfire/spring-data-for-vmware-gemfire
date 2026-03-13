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
 * 2026-03-11: Created GemFire 10.3 AttributesMutator adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.AttributesMutator;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.3 AttributesMutator.
 */
public class GemFire103AttributesMutator<K, V> implements GudAttributesMutator<K, V>, NativeWrapper<AttributesMutator<K, V>> {

    private final AttributesMutator<K, V> nativeMutator;

    public GemFire103AttributesMutator(AttributesMutator<K, V> nativeMutator) {
        this.nativeMutator = nativeMutator;
    }

    @Override
    public AttributesMutator<K, V> getNative() {
        return nativeMutator;
    }

    @Override
    public GudRegion<K, V> getRegion() {
        return new GemFire103Region<>(nativeMutator.getRegion());
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public GudCacheWriter<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void addCacheListener(GudCacheListener<K, V> cacheListener) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void removeCacheListener(GudCacheListener<K, V> cacheListener) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void initCacheListeners(GudCacheListener<K, V>[] cacheListeners) {
        throw new UnsupportedOperationException("Not yet implemented");
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
        return new GemFire103EvictionAttributesMutator(nativeMutator.getEvictionAttributesMutator());
    }

    @Override
    public boolean getCloningEnabled() {
        return nativeMutator.getCloningEnabled();
    }
}
