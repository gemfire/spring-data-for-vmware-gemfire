/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: Created MockGudDriver - in-memory driver for unit tests; advertises every
 *             GudCapability so tests exercising feature-gated code paths always take the
 *             supported branch
 * 2026-04-17: Removed peer cache factory and wrapCache (client-only driver)
 * 2026-04-17: Implemented eviction-attribute factories for GudEvictionAttributes static delegation
 * 2026-06-06: Added MockGudFunctionService field and createFunctionService() override
 */

package org.springframework.data.gemfire.gud.driver.mock;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudApiVersion;
import org.springframework.data.gemfire.gud.api.GudCapability;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudClientCacheFactory;
import org.springframework.data.gemfire.gud.api.GudEvictionAction;
import org.springframework.data.gemfire.gud.api.GudEvictionAlgorithm;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.data.gemfire.gud.api.GudGemFireException;
import org.springframework.data.gemfire.gud.api.GudJndiBinding;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudPoolManager;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.core.GudDriver;

/**
 * In-memory mock {@link GudDriver} implementation for unit tests.
 * <p>
 * Registered via {@code META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver}
 * so that any test classpath containing {@code gud-driver-mock} will automatically have a
 * driver available to {@code GudCacheProvider} and {@code GudDriverManager}.
 * <p>
 * Reports {@link #SUPPORTED_VERSION} = {@code "10.99"} so it sorts after every real driver
 * in {@code GudDriverManager#getDriverForGemFireVersion(String)}. Advertises every
 * {@link GudCapability} so tests exercising feature-gated code paths never hit the
 * unsupported branch.
 */
public class MockGudDriver implements GudDriver {

    public static final String DRIVER_NAME = "gemfire-mock";
    public static final String SUPPORTED_VERSION = "10.99";

    private static final Set<GudCapability> ALL_CAPABILITIES =
        Collections.unmodifiableSet(EnumSet.allOf(GudCapability.class));

    private final MockGudPoolManager poolManager = new MockGudPoolManager();
    private final MockGudJndiBinding jndiBinding = new MockGudJndiBinding();
    private final MockGudFunctionService functionService = new MockGudFunctionService();

    @Override
    public String getName() {
        return DRIVER_NAME;
    }

    @Override
    public String getSupportedVersion() {
        return SUPPORTED_VERSION;
    }

    @Override
    public GudApiVersion getMinimumApiVersion() {
        return GudApiVersion.V1_0_0;
    }

    @Override
    public GudApiVersion getMaximumApiVersion() {
        return GudApiVersion.V1_0_0;
    }

    @Override
    public boolean supportsCapability(GudCapability capability) {
        return ALL_CAPABILITIES.contains(capability);
    }

    @Override
    public Set<GudCapability> getCapabilities() {
        return ALL_CAPABILITIES;
    }

    @Override
    public GudClientCacheFactory createClientCacheFactory() {
        return MockGudFactories.newClientCacheFactory(poolManager);
    }

    @Override
    public GudJndiBinding createJndiBinding() {
        return jndiBinding;
    }

    @Override
    public GudPoolManager createPoolManager() {
        return poolManager;
    }

    @Override
    public GudFunctionService createFunctionService() {
        return functionService;
    }

    @Override
    public GudEvictionAttributes createLruHeapEvictionAttributes(GudObjectSizer objectSizer, GudEvictionAction action) {
        return new MockGudEvictionAttributes(GudEvictionAlgorithm.LRU_HEAP, action, 0, objectSizer);
    }

    @Override
    public GudEvictionAttributes createLruMemoryEvictionAttributes(int maximum, GudObjectSizer objectSizer, GudEvictionAction action) {
        return new MockGudEvictionAttributes(GudEvictionAlgorithm.LRU_MEMORY, action, maximum, objectSizer);
    }

    @Override
    public GudEvictionAttributes createLruMemoryEvictionAttributesFromSizer(GudObjectSizer objectSizer, GudEvictionAction action) {
        return new MockGudEvictionAttributes(GudEvictionAlgorithm.LRU_MEMORY, action,
            GudEvictionAttributes.DEFAULT_MEMORY_MAXIMUM, objectSizer);
    }

    @Override
    public GudEvictionAttributes createLruEntryEvictionAttributes(int maximum, GudEvictionAction action) {
        return new MockGudEvictionAttributes(GudEvictionAlgorithm.LRU_ENTRY, action, maximum, null);
    }

    @Override
    public GudClientCache wrapClientCache(Object nativeClientCache) {
        if (nativeClientCache instanceof GudClientCache) {
            return (GudClientCache) nativeClientCache;
        }
        throw new IllegalArgumentException(
            "MockGudDriver only wraps GudClientCache instances; got: "
                + (nativeClientCache != null ? nativeClientCache.getClass().getName() : "null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K, V> GudRegion<K, V> wrapRegion(Object nativeRegion) {
        if (nativeRegion instanceof GudRegion) {
            return (GudRegion<K, V>) nativeRegion;
        }
        throw new IllegalArgumentException(
            "MockGudDriver only wraps GudRegion instances; got: "
                + (nativeRegion != null ? nativeRegion.getClass().getName() : "null"));
    }

    @Override
    public GudPool wrapPool(Object nativePool) {
        if (nativePool instanceof GudPool) {
            return (GudPool) nativePool;
        }
        throw new IllegalArgumentException(
            "MockGudDriver only wraps GudPool instances; got: "
                + (nativePool != null ? nativePool.getClass().getName() : "null"));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T unwrap(Object gudObject) {
        return (T) gudObject;
    }

    @Override
    public GudGemFireException translateException(Throwable nativeException) {
        if (nativeException instanceof GudGemFireException) {
            return (GudGemFireException) nativeException;
        }
        String message = nativeException != null ? nativeException.getMessage() : null;
        return new GudGemFireException(message, nativeException);
    }
}
