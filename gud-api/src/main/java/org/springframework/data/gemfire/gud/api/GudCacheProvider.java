/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudCacheProvider for static cache access methods
 * 2026-03-13: Updated to include factory references and auto-discovery via ServiceLoader
 * 2026-04-02: Replaced singleton GudClientCacheFactory with Supplier so each call to
 *             createClientCacheFactory() returns a fresh stateful builder instance; added
 *             configure() entry-point for GudDriverManager push-registration; removed
 *             per-factory ServiceLoader loops (replaced by single GudDriver registration path);
 *             fixed setClientCacheFactory() incorrectly setting initialized=true for all fields;
 *             synchronised initialize() with AtomicBoolean guard
 * 2026-04-17: ensureInitialized() now bootstraps via GudDriverManager (reflectively, since
 *             gud-api cannot depend on gud-core) so the provider works when no code has yet
 *             triggered driver discovery; fallback ServiceLoader for individual factory
 *             types remains for legacy drivers that still publish them
 * 2026-04-17: Removed peer cache factory and peer cache instance accessors (client-only provider)
 */

package org.springframework.data.gemfire.gud.api;

import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * GUD API provider for accessing cache instances and factories.
 * This is the main entry point for the Spring layer to create and access GemFire
 * <strong>client</strong> caches using the GUD API without direct dependencies on native GemFire types.
 *
 * <p><strong>Client-only contract:</strong> Peer (server-side) caches, locators, and server regions
 * are not part of the supported GUD application surface. Use
 * <a href="https://github.com/gemfire/gemfire-testcontainers">GemFire Testcontainers</a> (or native
 * server APIs) for that topology.
 *
 * <p>The preferred initialisation path is via {@link #configure(Supplier, GudJndiBinding, GudPoolManager)},
 * which is called automatically by {@code GudDriverManager} when a driver is registered.
 * A ServiceLoader fallback is also provided for environments that load driver modules before Spring
 * bootstraps {@code GudDriverManager}.
 */
public final class GudCacheProvider {

    private static volatile GudClientCache currentClientCache;

    private static volatile Supplier<GudClientCacheFactory> clientCacheFactorySupplier;
    private static volatile GudJndiBinding jndiBinding;
    private static volatile GudPoolManager poolManager;

    private static final AtomicBoolean initialized = new AtomicBoolean(false);

    private GudCacheProvider() {
    }

    // ===== Configuration Entry Points =====

    /**
     * Configures the provider from a driver.  Called by {@code GudDriverManager} after a
     * driver is registered so that both discovery paths stay coordinated.
     *
     * @param clientCacheFactorySupplier supplier that produces a fresh factory on each call
     * @param jndiBinding                JNDI binding implementation
     * @param poolManager                pool manager implementation
     */
    public static synchronized void configure(
            Supplier<GudClientCacheFactory> clientCacheFactorySupplier,
            GudJndiBinding jndiBinding,
            GudPoolManager poolManager) {

        GudCacheProvider.clientCacheFactorySupplier = clientCacheFactorySupplier;
        GudCacheProvider.jndiBinding = jndiBinding;
        GudCacheProvider.poolManager = poolManager;
        initialized.set(true);
    }

    /**
     * Sets the client cache factory supplier.  Only populates this one field; use
     * {@link #configure(Supplier, GudJndiBinding, GudPoolManager)} to set all factories atomically.
     *
     * @param supplier supplier that produces a fresh factory on each call
     */
    public static void setClientCacheFactory(Supplier<GudClientCacheFactory> supplier) {
        clientCacheFactorySupplier = supplier;
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
     * Sets the pool manager implementation.
     *
     * @param pm the pool manager implementation
     */
    public static void setPoolManager(GudPoolManager pm) {
        poolManager = pm;
    }

    // ===== Factory Accessors =====

    /**
     * Creates a new {@link GudClientCacheFactory} instance.
     * Each call returns a fresh, independently-configurable factory builder.
     *
     * @return a new client cache factory
     * @throws IllegalStateException if no factory has been configured or discovered
     */
    public static GudClientCacheFactory createClientCacheFactory() {
        ensureInitialized();
        Supplier<GudClientCacheFactory> supplier = clientCacheFactorySupplier;
        if (supplier == null) {
            throw new IllegalStateException(
                "No GudClientCacheFactory configured. Ensure a GUD driver module is on the classpath.");
        }
        return supplier.get();
    }

    /**
     * Gets the JNDI binding implementation. Auto-discovers via ServiceLoader if not yet configured.
     *
     * @return the JNDI binding implementation, or {@code null} if none available
     */
    public static GudJndiBinding getJndiBinding() {
        ensureInitialized();
        return jndiBinding;
    }

    /**
     * Gets the pool manager implementation. Auto-discovers via ServiceLoader if not yet configured.
     *
     * @return the pool manager implementation, or {@code null} if none available
     */
    public static GudPoolManager getPoolManager() {
        ensureInitialized();
        return poolManager;
    }

    // ===== Cache Instance Accessors =====

    /**
     * Sets the current client cache instance.
     *
     * @param cache the client cache instance
     */
    public static void setCurrentClientCache(GudClientCache cache) {
        currentClientCache = cache;
    }

    /**
     * Gets any existing client cache instance.
     *
     * @return the client cache, or {@code null} if none exists
     */
    public static GudClientCache getAnyClientCache() {
        return currentClientCache;
    }

    /**
     * Gets any existing {@link GudDistributedSystem} instance, derived from the current client cache.
     *
     * @return the distributed system, or {@code null} if none exists
     */
    public static GudDistributedSystem getDistributedSystem() {
        GudClientCache cache = currentClientCache;
        return cache != null ? cache.getDistributedSystem() : null;
    }

    // ===== Lifecycle =====

    /**
     * Clears all cached references and resets the provider.  Primarily for testing.
     */
    public static synchronized void clear() {
        currentClientCache = null;
        clientCacheFactorySupplier = null;
        jndiBinding = null;
        poolManager = null;
        initialized.set(false);
    }

    // ===== Internal =====

    /**
     * Falls back to ServiceLoader discovery if {@link GudDriverManager} has not yet
     * pushed factories via {@link #configure}.  This handles environments where driver
     * modules are loaded before Spring bootstraps {@code GudDriverManager}.
     */
    private static synchronized void ensureInitialized() {
        if (initialized.get()) {
            return;
        }

        // Preferred path: bootstrap via GudDriverManager, which will call configure() on us
        // after discovering a GudDriver via ServiceLoader.  Reflection is required because
        // gud-api cannot depend on gud-core.
        try {
            Class<?> driverManagerClass = Class.forName(
                "org.springframework.data.gemfire.gud.core.GudDriverManager");
            driverManagerClass.getMethod("loadDrivers").invoke(null);
            if (initialized.get()) {
                return;
            }
        } catch (ReflectiveOperationException | LinkageError ignore) {
            // gud-core not on the classpath, or driver registration threw; fall through to
            // the legacy per-factory ServiceLoader path below.
        }

        // Legacy fallback: discover individual factory services directly.  Retained for
        // backward compatibility with drivers that register these service entries.
        ServiceLoader<GudClientCacheFactory> clientFactories = ServiceLoader.load(GudClientCacheFactory.class);
        for (GudClientCacheFactory factory : clientFactories) {
            if (clientCacheFactorySupplier == null) {
                final GudClientCacheFactory discovered = factory;
                clientCacheFactorySupplier = () -> discovered;
            }
        }

        ServiceLoader<GudJndiBinding> jndiBindings = ServiceLoader.load(GudJndiBinding.class);
        for (GudJndiBinding binding : jndiBindings) {
            if (jndiBinding == null) {
                jndiBinding = binding;
            }
        }

        ServiceLoader<GudPoolManager> poolManagers = ServiceLoader.load(GudPoolManager.class);
        for (GudPoolManager pm : poolManagers) {
            if (poolManager == null) {
                poolManager = pm;
            }
        }

        initialized.set(true);
    }
}
