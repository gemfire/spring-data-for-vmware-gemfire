/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudAttributesFactory interface for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire AttributesFactory.
 * Used to create RegionAttributes.
 */
public interface GudAttributesFactory<K, V> {

    GudAttributesFactory<K, V> setScope(GudScope scope);
    
    GudAttributesFactory<K, V> setDataPolicy(GudDataPolicy dataPolicy);
    
    GudAttributesFactory<K, V> setEntryIdleTimeout(GudExpirationAttributes idleTimeout);
    
    GudAttributesFactory<K, V> setEntryTimeToLive(GudExpirationAttributes timeToLive);
    
    GudAttributesFactory<K, V> setRegionIdleTimeout(GudExpirationAttributes idleTimeout);
    
    GudAttributesFactory<K, V> setRegionTimeToLive(GudExpirationAttributes timeToLive);
    
    GudAttributesFactory<K, V> setEvictionAttributes(GudEvictionAttributes evictionAttributes);
    
    GudAttributesFactory<K, V> setCacheLoader(GudCacheLoader<K, V> cacheLoader);
    
    GudAttributesFactory<K, V> setCacheWriter(GudCacheWriter<K, V> cacheWriter);
    
    GudAttributesFactory<K, V> addCacheListener(GudCacheListener<K, V> listener);
    
    GudAttributesFactory<K, V> setStatisticsEnabled(boolean statisticsEnabled);
    
    GudAttributesFactory<K, V> setPoolName(String poolName);
    
    GudAttributesFactory<K, V> setDiskStoreName(String diskStoreName);
    
    GudAttributesFactory<K, V> setKeyConstraint(Class<K> keyConstraint);
    
    GudAttributesFactory<K, V> setValueConstraint(Class<V> valueConstraint);
    
    GudRegionAttributes<K, V> create();
}
