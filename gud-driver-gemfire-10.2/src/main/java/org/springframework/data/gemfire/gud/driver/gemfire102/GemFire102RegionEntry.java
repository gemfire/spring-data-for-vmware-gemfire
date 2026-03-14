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
 * 2026-03-13: Created GemFire 10.2 RegionEntry adapter
 */

package org.springframework.data.gemfire.gud.driver.gemfire102;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.gud.api.GudCacheStatistics;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionEntry;

/**
 * GUD API adapter for GemFire 10.2 Region.Entry.
 *
 * @param <K> the key type
 * @param <V> the value type
 */
public class GemFire102RegionEntry<K, V> implements GudRegionEntry<K, V>, NativeWrapper<Region.Entry<K, V>> {

    private final Region.Entry<K, V> nativeEntry;

    public GemFire102RegionEntry(Region.Entry<K, V> nativeEntry) {
        this.nativeEntry = nativeEntry;
    }

    @Override
    public Region.Entry<K, V> getNative() {
        return nativeEntry;
    }

    @Override
    public K getKey() {
        return nativeEntry.getKey();
    }

    @Override
    public V getValue() {
        return nativeEntry.getValue();
    }

    @Override
    public V setValue(V value) {
        return nativeEntry.setValue(value);
    }

    @Override
    public GudRegion<K, V> getRegion() {
        return new GemFire102Region<>(nativeEntry.getRegion());
    }

    @Override
    public GudCacheStatistics getStatistics() {
        return new GemFire102CacheStatistics(nativeEntry.getStatistics());
    }

    @Override
    public Object getUserAttribute() {
        return nativeEntry.getUserAttribute();
    }

    @Override
    public Object setUserAttribute(Object userAttribute) {
        return nativeEntry.setUserAttribute(userAttribute);
    }

    @Override
    public boolean isLocal() {
        return nativeEntry.isLocal();
    }

    @Override
    public boolean isDestroyed() {
        return nativeEntry.isDestroyed();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof GemFire102RegionEntry) {
            return nativeEntry.equals(((GemFire102RegionEntry<?, ?>) obj).nativeEntry);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return nativeEntry.hashCode();
    }
}
