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
 * 2026-03-13: Created GemFire 10.0 RegionFactory adapter
 */

package org.springframework.data.gemfire.gud.driver;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.PartitionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionFactory;
import org.apache.geode.cache.Scope;
import org.apache.geode.cache.SubscriptionAttributes;

import org.springframework.data.gemfire.gud.api.*;

/**
 * GUD API adapter for GemFire 10.0 RegionFactory.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFireRegionFactory<K, V> implements GudRegionFactory<K, V>, NativeWrapper<RegionFactory<K, V>> {

    private final RegionFactory<K, V> nativeFactory;

    public GemFireRegionFactory(RegionFactory<K, V> nativeFactory) {
        this.nativeFactory = nativeFactory;
    }

    @Override
    public RegionFactory<K, V> getNative() {
        return nativeFactory;
    }

    @Override
    public GudRegionFactory<K, V> setDataPolicy(GudDataPolicy dataPolicy) {
        nativeFactory.setDataPolicy(toNativeDataPolicy(dataPolicy));
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setScope(GudScope scope) {
        nativeFactory.setScope(toNativeScope(scope));
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setKeyConstraint(Class<K> keyConstraint) {
        nativeFactory.setKeyConstraint(keyConstraint);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setValueConstraint(Class<V> valueConstraint) {
        nativeFactory.setValueConstraint(valueConstraint);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader) {
        if (cacheLoader instanceof NativeWrapper) {
            nativeFactory.setCacheLoader(((NativeWrapper<org.apache.geode.cache.CacheLoader<K, V>>) cacheLoader).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter) {
        if (cacheWriter instanceof NativeWrapper) {
            nativeFactory.setCacheWriter(((NativeWrapper<org.apache.geode.cache.CacheWriter<K, V>>) cacheWriter).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> addCacheListener(GudCacheListener<K, V> cacheListener) {
        if (cacheListener instanceof NativeWrapper) {
            nativeFactory.addCacheListener(((NativeWrapper<org.apache.geode.cache.CacheListener<K, V>>) cacheListener).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> initCacheListeners(GudCacheListener<K, V>[] cacheListeners) {
        if (cacheListeners != null) {
            org.apache.geode.cache.CacheListener<K, V>[] nativeListeners = new org.apache.geode.cache.CacheListener[cacheListeners.length];
            for (int i = 0; i < cacheListeners.length; i++) {
                if (cacheListeners[i] instanceof NativeWrapper) {
                    nativeListeners[i] = ((NativeWrapper<org.apache.geode.cache.CacheListener<K, V>>) cacheListeners[i]).getNative();
                }
            }
            nativeFactory.initCacheListeners(nativeListeners);
        }
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive) {
        nativeFactory.setRegionTimeToLive(toNativeExpirationAttributes(timeToLive));
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout) {
        nativeFactory.setRegionIdleTimeout(toNativeExpirationAttributes(idleTimeout));
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive) {
        nativeFactory.setEntryTimeToLive(toNativeExpirationAttributes(timeToLive));
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout) {
        nativeFactory.setEntryIdleTimeout(toNativeExpirationAttributes(idleTimeout));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setCustomEntryTimeToLive(GudCustomExpiry<K, V> customExpiry) {
        if (customExpiry instanceof NativeWrapper) {
            nativeFactory.setCustomEntryTimeToLive(((NativeWrapper<org.apache.geode.cache.CustomExpiry<K, V>>) customExpiry).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customExpiry) {
        if (customExpiry instanceof NativeWrapper) {
            nativeFactory.setCustomEntryIdleTimeout(((NativeWrapper<org.apache.geode.cache.CustomExpiry<K, V>>) customExpiry).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes) {
        if (evictionAttributes instanceof NativeWrapper) {
            nativeFactory.setEvictionAttributes(((NativeWrapper<EvictionAttributes>) evictionAttributes).getNative());
        }
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setPartitionAttributes(GudPartitionAttributes<K, V> partitionAttributes) {
        if (partitionAttributes instanceof NativeWrapper) {
            nativeFactory.setPartitionAttributes(((NativeWrapper<PartitionAttributes<K, V>>) partitionAttributes).getNative());
        }
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setMembershipAttributes(GudMembershipAttributes membershipAttributes) {
        // MembershipAttributes is deprecated, ignore
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setSubscriptionAttributes(GudSubscriptionAttributes subscriptionAttributes) {
        if (subscriptionAttributes instanceof NativeWrapper) {
            nativeFactory.setSubscriptionAttributes(((NativeWrapper<SubscriptionAttributes>) subscriptionAttributes).getNative());
        }
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setDiskStoreName(String name) {
        nativeFactory.setDiskStoreName(name);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setDiskSynchronous(boolean isSynchronous) {
        nativeFactory.setDiskSynchronous(isSynchronous);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setEnableSubscriptionConflation(boolean enableSubscriptionConflation) {
        nativeFactory.setEnableSubscriptionConflation(enableSubscriptionConflation);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setIgnoreJTA(boolean ignoreJTA) {
        nativeFactory.setIgnoreJTA(ignoreJTA);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setPoolName(String poolName) {
        nativeFactory.setPoolName(poolName);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled) {
        nativeFactory.setStatisticsEnabled(statisticsEnabled);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setLockGrantor(boolean lockGrantor) {
        nativeFactory.setLockGrantor(lockGrantor);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setConcurrencyLevel(int concurrencyLevel) {
        nativeFactory.setConcurrencyLevel(concurrencyLevel);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setConcurrencyChecksEnabled(boolean concurrencyChecksEnabled) {
        nativeFactory.setConcurrencyChecksEnabled(concurrencyChecksEnabled);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setInitialCapacity(int initialCapacity) {
        nativeFactory.setInitialCapacity(initialCapacity);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setLoadFactor(float loadFactor) {
        nativeFactory.setLoadFactor(loadFactor);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setCloningEnabled(boolean cloningEnabled) {
        nativeFactory.setCloningEnabled(cloningEnabled);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setMulticastEnabled(boolean multicastEnabled) {
        nativeFactory.setMulticastEnabled(multicastEnabled);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> setOffHeap(boolean offHeap) {
        nativeFactory.setOffHeap(offHeap);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegionFactory<K, V> setCompressor(GudCompressor compressor) {
        if (compressor instanceof NativeWrapper) {
            nativeFactory.setCompressor(((NativeWrapper<org.apache.geode.compression.Compressor>) compressor).getNative());
        }
        return this;
    }

    @Override
    public GudRegionFactory<K, V> addGatewaySenderId(String gatewaySenderId) {
        nativeFactory.addGatewaySenderId(gatewaySenderId);
        return this;
    }

    @Override
    public GudRegionFactory<K, V> addAsyncEventQueueId(String asyncEventQueueId) {
        nativeFactory.addAsyncEventQueueId(asyncEventQueueId);
        return this;
    }

    @Override
    public GudRegion<K, V> create(String regionName) {
        return new GemFireRegion<>(nativeFactory.create(regionName));
    }

    @Override
    @SuppressWarnings("unchecked")
    public GudRegion<K, V> createSubregion(GudRegion<?, ?> parent, String subregionName) {
        if (parent instanceof NativeWrapper) {
            Region<?, ?> nativeParent = ((NativeWrapper<Region<?, ?>>) parent).getNative();
            return new GemFireRegion<>(nativeFactory.createSubregion(nativeParent, subregionName));
        }
        throw new IllegalArgumentException("Parent region must be a native wrapper");
    }

    private DataPolicy toNativeDataPolicy(GudDataPolicy dataPolicy) {
        switch (dataPolicy) {
            case EMPTY: return DataPolicy.EMPTY;
            case NORMAL: return DataPolicy.NORMAL;
            case REPLICATE: return DataPolicy.REPLICATE;
            case PERSISTENT_REPLICATE: return DataPolicy.PERSISTENT_REPLICATE;
            case PARTITION: return DataPolicy.PARTITION;
            case PERSISTENT_PARTITION: return DataPolicy.PERSISTENT_PARTITION;
            case PRELOADED: return DataPolicy.PRELOADED;
            default: return DataPolicy.DEFAULT;
        }
    }

    private Scope toNativeScope(GudScope scope) {
        switch (scope) {
            case LOCAL: return Scope.LOCAL;
            case DISTRIBUTED_NO_ACK: return Scope.DISTRIBUTED_NO_ACK;
            case DISTRIBUTED_ACK: return Scope.DISTRIBUTED_ACK;
            case GLOBAL: return Scope.GLOBAL;
            default: return Scope.DISTRIBUTED_NO_ACK;
        }
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
