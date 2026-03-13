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
 * 2026-03-11: Created GemFire 10.3 driver implementation
 * 2026-03-13: Added factory creation methods for cache creation
 */

package org.springframework.data.gemfire.gud.driver.gemfire103;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.core.GudDriver;

/**
 * GUD driver implementation for GemFire 10.3.x.
 * Wraps native GemFire 10.3 types with GUD API interfaces.
 */
public class GemFire103Driver implements GudDriver {

    public static final String DRIVER_NAME = "gemfire-10.3";
    public static final String SUPPORTED_VERSION = "10.3";

    @Override
    public String getName() {
        return DRIVER_NAME;
    }

    @Override
    public String getSupportedVersion() {
        return SUPPORTED_VERSION;
    }

    @Override
    public GudClientCacheFactory createClientCacheFactory() {
        return new GemFire103ClientCacheFactory();
    }

    @Override
    public GudCacheFactory createCacheFactory() {
        return new GemFire103CacheFactory();
    }

    @Override
    public GudCache wrapCache(Object nativeCache) {
        if (nativeCache instanceof Cache) {
            return new GemFire103Cache((Cache) nativeCache);
        }
        throw new IllegalArgumentException("Expected Cache instance but got: " + 
            (nativeCache != null ? nativeCache.getClass().getName() : "null"));
    }

    @Override
    public GudClientCache wrapClientCache(Object nativeClientCache) {
        if (nativeClientCache instanceof ClientCache) {
            return new GemFire103ClientCache((ClientCache) nativeClientCache);
        }
        throw new IllegalArgumentException("Expected ClientCache instance but got: " + 
            (nativeClientCache != null ? nativeClientCache.getClass().getName() : "null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> GudRegion<K, V> wrapRegion(Object nativeRegion) {
        if (nativeRegion instanceof Region) {
            return new GemFire103Region<>((Region<K, V>) nativeRegion);
        }
        throw new IllegalArgumentException("Expected Region instance but got: " + 
            (nativeRegion != null ? nativeRegion.getClass().getName() : "null"));
    }

    @Override
    public GudPool wrapPool(Object nativePool) {
        if (nativePool instanceof Pool) {
            return new GemFire103Pool((Pool) nativePool);
        }
        throw new IllegalArgumentException("Expected Pool instance but got: " + 
            (nativePool != null ? nativePool.getClass().getName() : "null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Object gudObject) {
        if (gudObject instanceof NativeWrapper) {
            return ((NativeWrapper<T>) gudObject).getNative();
        }
        throw new IllegalArgumentException("Object is not a GUD wrapper: " + 
            (gudObject != null ? gudObject.getClass().getName() : "null"));
    }
}
