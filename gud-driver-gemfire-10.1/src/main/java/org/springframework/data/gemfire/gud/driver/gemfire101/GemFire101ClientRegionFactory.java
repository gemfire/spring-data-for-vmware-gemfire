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
 * 2026-03-13: Created GemFire 10.1 ClientRegionFactory adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire101;

import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionFactory;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.1 ClientRegionFactory.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFire101ClientRegionFactory<K, V> implements GudClientRegionFactory<K, V>, NativeWrapper<ClientRegionFactory<K, V>> {

    private final ClientRegionFactory<K, V> nativeFactory;

    public GemFire101ClientRegionFactory(ClientRegionFactory<K, V> nativeFactory) {
        this.nativeFactory = nativeFactory;
    }

    @Override
    public ClientRegionFactory<K, V> getNative() {
        return nativeFactory;
    }

    @Override
    public GudClientRegionFactory<K, V> setKeyConstraint(Class<K> keyConstraint) {
        nativeFactory.setKeyConstraint(keyConstraint);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setValueConstraint(Class<V> valueConstraint) {
        nativeFactory.setValueConstraint(valueConstraint);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader) {
        // ClientRegionFactory doesn't have setCacheLoader in 10.1 - use attributes
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
        // ClientRegionFactory doesn't have setCacheWriter in 10.1 - use attributes
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> addCacheListener(GudCacheListener<K, V> cacheListener) {
        if (cacheListener != null) {
            // Wrap the GUD listener in an adapter for native use
            nativeFactory.addCacheListener(new GemFire101CacheListenerAdapter<>(cacheListener));
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> initCacheListeners(GudCacheListener<K, V>[] cacheListeners) {
        if (cacheListeners != null) {
            org.apache.geode.cache.CacheListener<K, V>[] nativeListeners = new org.apache.geode.cache.CacheListener[cacheListeners.length];
            for (int i = 0; i < cacheListeners.length; i++) {
                if (cacheListeners[i] != null) {
                    nativeListeners[i] = new GemFire101CacheListenerAdapter<>(cacheListeners[i]);
                }
            }
            nativeFactory.initCacheListeners(nativeListeners);
        }
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive) {
        nativeFactory.setRegionTimeToLive(toNativeExpirationAttributes(timeToLive));
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout) {
        nativeFactory.setRegionIdleTimeout(toNativeExpirationAttributes(idleTimeout));
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive) {
        nativeFactory.setEntryTimeToLive(toNativeExpirationAttributes(timeToLive));
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout) {
        nativeFactory.setEntryIdleTimeout(toNativeExpirationAttributes(idleTimeout));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> setCustomEntryTimeToLive(GudCustomExpiry<K, V> customExpiry) {
        if (customExpiry instanceof NativeWrapper) {
            nativeFactory.setCustomEntryTimeToLive(((NativeWrapper<org.apache.geode.cache.CustomExpiry<K, V>>) customExpiry).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customExpiry) {
        if (customExpiry instanceof NativeWrapper) {
            nativeFactory.setCustomEntryIdleTimeout(((NativeWrapper<org.apache.geode.cache.CustomExpiry<K, V>>) customExpiry).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes) {
        if (evictionAttributes instanceof NativeWrapper) {
            nativeFactory.setEvictionAttributes(((NativeWrapper<EvictionAttributes>) evictionAttributes).getNative());
        }
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setDiskStoreName(String name) {
        nativeFactory.setDiskStoreName(name);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setDiskSynchronous(boolean isSynchronous) {
        nativeFactory.setDiskSynchronous(isSynchronous);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setPoolName(String poolName) {
        nativeFactory.setPoolName(poolName);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled) {
        nativeFactory.setStatisticsEnabled(statisticsEnabled);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setConcurrencyLevel(int concurrencyLevel) {
        nativeFactory.setConcurrencyLevel(concurrencyLevel);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setConcurrencyChecksEnabled(boolean concurrencyChecksEnabled) {
        nativeFactory.setConcurrencyChecksEnabled(concurrencyChecksEnabled);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setInitialCapacity(int initialCapacity) {
        nativeFactory.setInitialCapacity(initialCapacity);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setLoadFactor(float loadFactor) {
        nativeFactory.setLoadFactor(loadFactor);
        return this;
    }

    @Override
    public GudClientRegionFactory<K, V> setCloningEnabled(boolean cloningEnabled) {
        nativeFactory.setCloningEnabled(cloningEnabled);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudClientRegionFactory<K, V> setCompressor(GudCompressor compressor) {
        if (compressor instanceof NativeWrapper) {
            nativeFactory.setCompressor(((NativeWrapper<org.apache.geode.compression.Compressor>) compressor).getNative());
        }
        return this;
    }

    @Override
    public GudRegion<K, V> create(String regionName) {
        return new GemFire101Region<>(nativeFactory.create(regionName));
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegion<K, V> createSubregion(GudRegion<?, ?> parent, String subregionName) {
        if (parent instanceof NativeWrapper) {
            Region<?, ?> nativeParent = ((NativeWrapper<Region<?, ?>>) parent).getNative();
            return new GemFire101Region<>(nativeFactory.createSubregion(nativeParent, subregionName));
        }
        throw new IllegalArgumentException("Parent region must be a native wrapper");
    }

    private ExpirationAttributes toNativeExpirationAttributes(GudExpirationAttributes attrs) {
        if (attrs instanceof NativeWrapper) {
            @SuppressWarnings("unchecked")
            ExpirationAttributes nativeAttrs = ((NativeWrapper<ExpirationAttributes>) attrs).getNative();
            return nativeAttrs;
        }
        return new ExpirationAttributes(attrs.getTimeout(), toNativeExpirationAction(attrs.getAction()));
    }

    private org.apache.geode.cache.ExpirationAction toNativeExpirationAction(GudExpirationAction action) {
        if (action == null) return org.apache.geode.cache.ExpirationAction.INVALIDATE;
        switch (action) {
            case DESTROY: return org.apache.geode.cache.ExpirationAction.DESTROY;
            case LOCAL_DESTROY: return org.apache.geode.cache.ExpirationAction.LOCAL_DESTROY;
            case INVALIDATE: return org.apache.geode.cache.ExpirationAction.INVALIDATE;
            case LOCAL_INVALIDATE: return org.apache.geode.cache.ExpirationAction.LOCAL_INVALIDATE;
            default: return org.apache.geode.cache.ExpirationAction.INVALIDATE;
        }
    }
}
