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
 * 2026-03-11: Created GudClientRegionFactory interface as 1:1 mapping of GemFire ClientRegionFactory
 * 2026-03-31: Added setServerRegionName default method (10.3+)
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ClientRegionFactory interface.
 * Factory for creating client regions.
 *
 * @param <K> the type of keys
 * @param <V> the type of values
 */
public interface GudClientRegionFactory<K, V> {

    GudClientRegionFactory<K, V> setKeyConstraint(Class<K> keyConstraint);
    GudClientRegionFactory<K, V> setValueConstraint(Class<V> valueConstraint);

    GudClientRegionFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader);
    GudClientRegionFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter);
    GudClientRegionFactory<K, V> addCacheListener(GudCacheListener<K, V> cacheListener);
    GudClientRegionFactory<K, V> initCacheListeners(GudCacheListener<K, V>[] cacheListeners);

    GudClientRegionFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive);
    GudClientRegionFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout);
    GudClientRegionFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive);
    GudClientRegionFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout);
    GudClientRegionFactory<K, V> setCustomEntryTimeToLive(GudCustomExpiry<K, V> customExpiry);
    GudClientRegionFactory<K, V> setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customExpiry);

    GudClientRegionFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes);

    GudClientRegionFactory<K, V> setDiskStoreName(String name);
    GudClientRegionFactory<K, V> setDiskSynchronous(boolean isSynchronous);

    GudClientRegionFactory<K, V> setPoolName(String poolName);

    GudClientRegionFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled);
    GudClientRegionFactory<K, V> setConcurrencyLevel(int concurrencyLevel);
    GudClientRegionFactory<K, V> setConcurrencyChecksEnabled(boolean concurrencyChecksEnabled);
    GudClientRegionFactory<K, V> setInitialCapacity(int initialCapacity);
    GudClientRegionFactory<K, V> setLoadFactor(float loadFactor);

    GudClientRegionFactory<K, V> setCloningEnabled(boolean cloningEnabled);

    GudClientRegionFactory<K, V> setCompressor(GudCompressor compressor);

    /**
     * Sets the name of the remote server region being proxied by this client region.
     * If unset, the remote server region will be named the same as the client region.
     * <p>This feature was added in GemFire 10.3. Drivers for older versions
     * will throw {@link GudUnsupportedOperationException}.
     *
     * @param serverRegionName name of the server-side region
     * @return this factory
     * @throws GudUnsupportedOperationException if not supported by the driver
     * @since GemFire 10.3
     */
    default GudClientRegionFactory<K, V> setServerRegionName(String serverRegionName) {
        throw new GudUnsupportedOperationException(
            "setServerRegionName() is not supported. This feature was added in GemFire 10.3.",
            "SERVER_REGION_NAME", "10.3");
    }

    GudRegion<K, V> create(String regionName);
    GudRegion<K, V> createSubregion(GudRegion<?, ?> parent, String subregionName);
}
