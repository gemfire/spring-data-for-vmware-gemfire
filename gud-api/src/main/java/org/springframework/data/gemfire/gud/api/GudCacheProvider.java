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
 * 2026-03-11: Created GudCacheProvider for static cache access methods
 * 2026-03-13: Updated to include factory references and auto-discovery via ServiceLoader
 */

package org.springframework.data.gemfire.gud.api;

import java.util.ServiceLoader;

/**
 * GUD API provider for accessing cache instances and factories.
 * This is the main entry point for applications to create and access GemFire caches
 * using the GUD API without direct dependencies on native GemFire types.
 * 
 * Factories are auto-discovered via ServiceLoader when first accessed.
 */
public final class GudCacheProvider {

    private static volatile GudClientCache currentClientCache;
    private static volatile GudCache currentCache;
    private static volatile GudClientCacheFactory clientCacheFactory;
    private static volatile GudCacheFactory cacheFactory;
    private static volatile GudJndiBinding jndiBinding;
    private static volatile GudPoolManager poolManager;
    private static volatile boolean initialized = false;

    private GudCacheProvider() {
    }

    /**
     * Initializes the provider by discovering factories via ServiceLoader.
     */
    private static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        // Discover GudClientCacheFactory implementations
        ServiceLoader<GudClientCacheFactory> clientFactories = ServiceLoader.load(GudClientCacheFactory.class);
        for (GudClientCacheFactory factory : clientFactories) {
            if (clientCacheFactory == null) {
                clientCacheFactory = factory;
            }
        }
        
        // Discover GudCacheFactory implementations
        ServiceLoader<GudCacheFactory> cacheFactories = ServiceLoader.load(GudCacheFactory.class);
        for (GudCacheFactory factory : cacheFactories) {
            if (cacheFactory == null) {
                cacheFactory = factory;
            }
        }
        
        // Discover GudJndiBinding implementations
        ServiceLoader<GudJndiBinding> jndiBindings = ServiceLoader.load(GudJndiBinding.class);
        for (GudJndiBinding binding : jndiBindings) {
            if (jndiBinding == null) {
                jndiBinding = binding;
            }
        }
        
        // Discover GudPoolManager implementations
        ServiceLoader<GudPoolManager> poolManagers = ServiceLoader.load(GudPoolManager.class);
        for (GudPoolManager pm : poolManagers) {
            if (poolManager == null) {
                poolManager = pm;
            }
        }
        
        initialized = true;
    }

    /**
     * Sets the client cache factory implementation.
     *
     * @param factory the factory implementation
     */
    public static void setClientCacheFactory(GudClientCacheFactory factory) {
        clientCacheFactory = factory;
        initialized = true;
    }

    /**
     * Gets the client cache factory. Auto-discovers via ServiceLoader if not set.
     *
     * @return the client cache factory
     * @throws IllegalStateException if no factory is configured or discovered
     */
    public static GudClientCacheFactory getClientCacheFactory() {
        if (clientCacheFactory == null) {
            initialize();
        }
        GudClientCacheFactory factory = clientCacheFactory;
        if (factory == null) {
            throw new IllegalStateException("No GudClientCacheFactory configured. " +
                "Ensure a GUD driver is on the classpath.");
        }
        return factory;
    }

    /**
     * Creates a new client cache factory instance.
     *
     * @return a new client cache factory
     */
    public static GudClientCacheFactory createClientCacheFactory() {
        return getClientCacheFactory();
    }

    /**
     * Sets the cache factory implementation.
     *
     * @param factory the factory implementation
     */
    public static void setCacheFactory(GudCacheFactory factory) {
        cacheFactory = factory;
        initialized = true;
    }

    /**
     * Gets the cache factory. Auto-discovers via ServiceLoader if not set.
     *
     * @return the cache factory
     * @throws IllegalStateException if no factory is configured or discovered
     */
    public static GudCacheFactory getCacheFactory() {
        if (cacheFactory == null) {
            initialize();
        }
        GudCacheFactory factory = cacheFactory;
        if (factory == null) {
            throw new IllegalStateException("No GudCacheFactory configured. " +
                "Ensure a GUD driver is on the classpath.");
        }
        return factory;
    }

    /**
     * Sets the current client cache instance.
     *
     * @param cache the client cache instance
     */
    public static void setCurrentClientCache(GudClientCache cache) {
        currentClientCache = cache;
    }

    /**
     * Sets the current cache instance.
     *
     * @param cache the cache instance
     */
    public static void setCurrentCache(GudCache cache) {
        currentCache = cache;
    }

    /**
     * Gets any existing client cache instance.
     *
     * @return the client cache, or null if none exists
     */
    public static GudClientCache getAnyClientCache() {
        return currentClientCache;
    }

    /**
     * Gets any existing cache instance.
     *
     * @return the cache, or null if none exists
     */
    public static GudCache getAnyCache() {
        return currentCache;
    }

    /**
     * Gets the JNDI binding implementation. Auto-discovers via ServiceLoader if not set.
     *
     * @return the JNDI binding implementation, or null if none available
     */
    public static GudJndiBinding getJndiBinding() {
        if (jndiBinding == null) {
            initialize();
        }
        return jndiBinding;
    }

    /**
     * Gets any existing DistributedSystem instance.
     * Delegates to the client cache factory if available.
     *
     * @return the distributed system, or null if none exists
     */
    public static GudDistributedSystem getDistributedSystem() {
        GudClientCache cache = currentClientCache;
        if (cache != null) {
            return cache.getDistributedSystem();
        }
        return null;
    }

    /**
     * Sets the JNDI binding implementation.
     *
     * @param binding the JNDI binding implementation
     */
    public static void setJndiBinding(GudJndiBinding binding) {
        jndiBinding = binding;
    }

    /**
     * Gets the pool manager implementation. Auto-discovers via ServiceLoader if not set.
     *
     * @return the pool manager implementation, or null if none available
     */
    public static GudPoolManager getPoolManager() {
        if (poolManager == null) {
            initialize();
        }
        return poolManager;
    }

    /**
     * Sets the pool manager implementation.
     *
     * @param pm the pool manager implementation
     */
    public static void setPoolManager(GudPoolManager pm) {
        poolManager = pm;
    }

    /**
     * Clears the current cache references.
     */
    public static void clear() {
        currentClientCache = null;
        currentCache = null;
        clientCacheFactory = null;
        cacheFactory = null;
        jndiBinding = null;
        poolManager = null;
        initialized = false;
    }
}
