/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheProvider for static cache access methods
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API provider for accessing cache instances.
 * This is a static utility class that delegates to the active driver.
 */
public final class GudCacheProvider {

    private static volatile GudClientCacheFactory clientCacheFactory;

    private GudCacheProvider() {
    }

    /**
     * Sets the client cache factory implementation.
     *
     * @param factory the factory implementation
     */
    public static void setClientCacheFactory(GudClientCacheFactory factory) {
        clientCacheFactory = factory;
    }

    /**
     * Gets the client cache factory.
     *
     * @return the client cache factory
     * @throws IllegalStateException if no factory is configured
     */
    public static GudClientCacheFactory getClientCacheFactory() {
        GudClientCacheFactory factory = clientCacheFactory;
        if (factory == null) {
            throw new IllegalStateException("No GudClientCacheFactory configured. " +
                "Ensure a GUD driver is on the classpath and initialized.");
        }
        return factory;
    }

    /**
     * Gets any existing client cache instance.
     *
     * @return the client cache, or null if none exists
     */
    public static GudClientCache getAnyClientCache() {
        try {
            return getClientCacheFactory().getAnyInstance();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a new client cache factory.
     *
     * @return a new client cache factory
     */
    public static GudClientCacheFactory createClientCacheFactory() {
        return getClientCacheFactory();
    }
}
