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
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDriver interface for driver abstraction
 * 2026-03-13: Added factory creation methods for cache creation without native dependencies
 * 2026-03-14: Added capability detection and version information for API evolution
 * 2026-03-17: Removed hypothetical 10.4 feature detection convenience methods
 * 2026-03-31: Added supportsServerRegionName convenience method
 */

package org.springframework.data.gemfire.gud.core;

import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudCache;
import org.springframework.data.gemfire.gud.api.GudCacheFactory;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Main interface for GemFire Unified Driver implementations.
 * Each GemFire version provides its own driver implementation that wraps
 * native GemFire types with GUD API interfaces.
 */
public interface GudDriver {

    // ===== Identity =====

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

    // ===== Version Information =====

    /**
     * Returns the minimum GUD API version this driver supports.
     *
     * @return the minimum supported API version
     */
    GudApiVersion getMinimumApiVersion();

    /**
     * Returns the maximum GUD API version this driver supports.
     *
     * @return the maximum supported API version
     */
    GudApiVersion getMaximumApiVersion();

    // ===== Capability Detection =====

    /**
     * Checks if this driver supports a specific capability.
     *
     * @param capability the capability to check
     * @return true if supported
     */
    boolean supportsCapability(GudCapability capability);

    /**
     * Returns all capabilities supported by this driver.
     *
     * @return unmodifiable set of supported capabilities
     */
    Set<GudCapability> getCapabilities();

    // ===== Factory Creation =====

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

    // ===== Wrapping =====

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

    // ===== Exception Handling =====

    /**
     * Translates a native GemFire exception to a GUD exception.
     *
     * @param nativeException the native exception
     * @return a GUD API exception
     */
    GudGemFireException translateException(Throwable nativeException);

    // ===== Feature Detection Convenience Methods =====

    /**
     * Checks if this driver supports per-server connection limits.
     *
     * @return true if per-server connection limits are supported
     */
    default boolean supportsPerServerConnectionLimits() {
        return supportsCapability(GudCapability.PER_SERVER_CONNECTION_LIMITS);
    }

    /**
     * Checks if this driver supports disk store segments API.
     *
     * @return true if disk store segments are supported
     */
    default boolean supportsDiskStoreSegments() {
        return supportsCapability(GudCapability.DISK_STORE_SEGMENTS);
    }

    /**
     * Checks if this driver supports server region name mapping on client regions.
     *
     * @return true if server region name is supported
     */
    default boolean supportsServerRegionName() {
        return supportsCapability(GudCapability.SERVER_REGION_NAME);
    }
}
