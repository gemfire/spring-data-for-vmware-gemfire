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
 * 2026-03-11: Created GudDriver interface for driver abstraction
 * 2026-03-13: Added factory creation methods for cache creation without native dependencies
 */

package org.springframework.data.gemfire.gud.core;

import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Main interface for GemFire Unified Driver implementations.
 * Each GemFire version provides its own driver implementation that wraps
 * native GemFire types with GUD API interfaces.
 */
public interface GudDriver {

    /**
     * Gets the name of this driver.
     *
     * @return the driver name
     */
    String getName();

    /**
     * Gets the GemFire version this driver supports.
     *
     * @return the supported GemFire version
     */
    String getSupportedVersion();

    /**
     * Creates a new client cache factory for building client caches.
     * This allows applications to create caches without referencing native GemFire types.
     *
     * @return a new GudClientCacheFactory instance
     */
    GudClientCacheFactory createClientCacheFactory();

    /**
     * Creates a new cache factory for building peer caches.
     * This allows applications to create caches without referencing native GemFire types.
     *
     * @return a new GudCacheFactory instance
     */
    GudCacheFactory createCacheFactory();

    /**
     * Wraps a native cache object with the GUD API.
     *
     * @param nativeCache the native GemFire cache
     * @return a GUD API cache wrapper
     */
    GudCache wrapCache(Object nativeCache);

    /**
     * Wraps a native client cache object with the GUD API.
     *
     * @param nativeClientCache the native GemFire client cache
     * @return a GUD API client cache wrapper
     */
    GudClientCache wrapClientCache(Object nativeClientCache);

    /**
     * Wraps a native region object with the GUD API.
     *
     * @param nativeRegion the native GemFire region
     * @param <K> the key type
     * @param <V> the value type
     * @return a GUD API region wrapper
     */
    <K, V> GudRegion<K, V> wrapRegion(Object nativeRegion);

    /**
     * Wraps a native pool object with the GUD API.
     *
     * @param nativePool the native GemFire pool
     * @return a GUD API pool wrapper
     */
    GudPool wrapPool(Object nativePool);

    /**
     * Unwraps a GUD API object to its native GemFire type.
     *
     * @param gudObject the GUD API object
     * @param <T> the expected native type
     * @return the native GemFire object
     */
    <T> T unwrap(Object gudObject);
}
