/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDriver interface for driver abstraction
 * 2026-03-13: Added factory creation methods for cache creation without native dependencies
 * 2026-03-14: Added capability detection and version information for API evolution
 * 2026-03-17: Removed hypothetical 10.4 feature detection convenience methods
 * 2026-03-31: Added supportsServerRegionName convenience method
 * 2026-04-02: Updated imports — GudCapability/GudApiVersion moved to gud-api; added createJndiBinding/createPoolManager
 * 2026-04-17: Removed peer-cache factory and wrapCache from SPI — GUD drivers are client-only
 * 2026-04-17: Added eviction-attribute factory methods for GudEvictionAttributes static delegation
 * 2026-06-06: Added createFunctionService() default method for GudFunctionService delegate registration
 */

package org.springframework.data.gemfire.gud.core;

import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudApiVersion;
import org.springframework.data.gemfire.gud.api.GudCapability;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudJndiBinding;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolManager;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Main interface for GemFire Unified Driver implementations.
 * Each GemFire version provides its own driver implementation that wraps
 * native GemFire types with GUD API interfaces.
 *
 * <p>Drivers are discovered via Java's {@link java.util.ServiceLoader} mechanism.
 * Register a driver by placing its fully-qualified class name in
 * {@code META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver}.
 */
public interface GudDriver {

    // ===== Identity =====

    /**
     * Gets the name of this driver (e.g. {@code "gemfire-10.3"}).
     *
     * @return the driver name
     */
    String getName();

    /**
     * Gets the GemFire version this driver supports (e.g. {@code "10.3"}).
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
     * @return {@code true} if supported
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
     * A fresh factory instance is returned on each call.
     *
     * @return a new GudClientCacheFactory instance
     */
    GudClientCacheFactory createClientCacheFactory();

    /**
     * Returns the JNDI binding implementation for this driver.
     * Implementations may return a shared singleton.
     *
     * @return a GudJndiBinding instance
     */
    GudJndiBinding createJndiBinding();

    /**
     * Returns the pool manager implementation for this driver.
     * Implementations may return a shared singleton.
     *
     * @return a GudPoolManager instance
     */
    GudPoolManager createPoolManager();

    // ===== Eviction attribute factories (used by GudEvictionAttributes static methods) =====

    /**
     * Creates LRU-heap eviction attributes (native-backed in real drivers).
     */
    GudEvictionAttributes createLruHeapEvictionAttributes(GudObjectSizer objectSizer, GudEvictionAction action);

    /**
     * Creates LRU-memory eviction attributes with an explicit maximum.
     */
    GudEvictionAttributes createLruMemoryEvictionAttributes(int maximum, GudObjectSizer objectSizer, GudEvictionAction action);

    /**
     * Creates LRU-memory eviction attributes using only an object sizer and action.
     */
    GudEvictionAttributes createLruMemoryEvictionAttributesFromSizer(GudObjectSizer objectSizer, GudEvictionAction action);

    /**
     * Creates LRU-entry eviction attributes.
     */
    GudEvictionAttributes createLruEntryEvictionAttributes(int maximum, GudEvictionAction action);

    /**
     * Returns the driver-specific {@link GudFunctionService} implementation, or {@code null}
     * if this driver does not support function execution.
     *
     * <p>Called automatically by {@code GudDriverManager} when the driver is registered;
     * if non-null, the returned instance is passed to {@link GudFunctionService#register}.
     *
     * @return a concrete {@link GudFunctionService} subclass, or {@code null}
     */
    default GudFunctionService createFunctionService() {
        return null;
    }

    // ===== Wrapping =====

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
     * @return {@code true} if {@link GudCapability#PER_SERVER_CONNECTION_LIMITS} is supported
     */
    default boolean supportsPerServerConnectionLimits() {
        return supportsCapability(GudCapability.PER_SERVER_CONNECTION_LIMITS);
    }

    /**
     * Checks if this driver supports the disk store segments API.
     *
     * @return {@code true} if {@link GudCapability#DISK_STORE_SEGMENTS} is supported
     */
    default boolean supportsDiskStoreSegments() {
        return supportsCapability(GudCapability.DISK_STORE_SEGMENTS);
    }

    /**
     * Checks if this driver supports server region name mapping on client regions.
     *
     * @return {@code true} if {@link GudCapability#SERVER_REGION_NAME} is supported
     */
    default boolean supportsServerRegionName() {
        return supportsCapability(GudCapability.SERVER_REGION_NAME);
    }
}
