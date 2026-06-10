/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudDriverManager for driver discovery and management
 * 2026-03-14: Added version-aware driver selection and capability checking
 * 2026-04-02: Fixed lexicographic version sort (use GudApiVersion.parse comparator); fixed
 *             loadDriversIfNeeded() race condition (AtomicBoolean); updated imports for relocated
 *             GudCapability/GudApiVersion; added push of driver factories to GudCacheProvider on
 *             driver registration so both discovery paths stay coordinated
 * 2026-04-17: pushFactoriesToCacheProvider registers client-only factories (no peer cache factory)
 * 2026-06-06: Register GudFunctionService delegate in pushFactoriesToCacheProvider(); clear it in clearDrivers()
 */

package org.springframework.data.gemfire.gud.core;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.gemfire.gud.api.GudApiVersion;
import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.api.GudCapability;
import org.springframework.data.gemfire.gud.api.GudFunctionService;

/**
 * Manager class for discovering and managing GUD drivers.
 * Uses Java's {@link ServiceLoader} for driver discovery.
 *
 * <p>Drivers are located via {@code META-INF/services/org.springframework.data.gemfire.gud.core.GudDriver}.
 * After registration the manager pushes the default driver's factories into {@link GudCacheProvider}
 * so that both discovery paths remain coordinated.
 */
public final class GudDriverManager {

    private static final Logger logger = LoggerFactory.getLogger(GudDriverManager.class);
    private static final ConcurrentMap<String, GudDriver> drivers = new ConcurrentHashMap<>();
    private static volatile GudDriver defaultDriver;
    private static final AtomicBoolean loaded = new AtomicBoolean(false);

    private GudDriverManager() {
    }

    /**
     * Discovers and loads all available GUD drivers using {@link ServiceLoader}.
     * Safe to call multiple times; loading only occurs once.
     */
    public static synchronized void loadDrivers() {
        if (loaded.get()) {
            return;
        }
        ServiceLoader<GudDriver> serviceLoader = ServiceLoader.load(GudDriver.class);
        for (GudDriver driver : serviceLoader) {
            registerDriver(driver);
        }
        loaded.set(true);
    }

    /**
     * Registers a driver with the manager.
     * If no default driver has been set yet, the first registered driver becomes the default.
     * After setting the default, its factories are pushed into {@link GudCacheProvider}.
     *
     * @param driver the driver to register
     */
    public static void registerDriver(GudDriver driver) {
        if (driver == null) {
            return;
        }
        drivers.put(driver.getName(), driver);
        logger.info("Registered GUD driver: {} for GemFire {}", driver.getName(), driver.getSupportedVersion());
        if (defaultDriver == null) {
            defaultDriver = driver;
            pushFactoriesToCacheProvider(driver);
        }
    }

    /**
     * Gets a driver by name.
     *
     * @param name the driver name
     * @return the driver, or {@code null} if not found
     */
    public static GudDriver getDriver(String name) {
        return drivers.get(name);
    }

    /**
     * Gets the default driver, loading drivers first if none have been registered.
     *
     * @return the default driver
     * @throws IllegalStateException if no driver is registered
     */
    public static GudDriver getDefaultDriver() {
        loadDriversIfNeeded();
        if (defaultDriver == null) {
            throw new IllegalStateException("No GUD driver registered. Ensure a driver module is on the classpath.");
        }
        return defaultDriver;
    }

    /**
     * Explicitly sets the default driver.
     *
     * @param driver the driver to set as default
     */
    public static void setDefaultDriver(GudDriver driver) {
        defaultDriver = driver;
        if (driver != null) {
            if (!drivers.containsKey(driver.getName())) {
                drivers.put(driver.getName(), driver);
            }
            pushFactoriesToCacheProvider(driver);
        }
    }

    /**
     * Gets all registered driver names.
     *
     * @return an iterable of driver names
     */
    public static Iterable<String> getDriverNames() {
        return drivers.keySet();
    }

    /**
     * Clears all registered drivers and resets state. Primarily for testing.
     * Also clears the {@link GudFunctionService} delegate so a stale mock is not left registered.
     */
    public static synchronized void clearDrivers() {
        drivers.clear();
        defaultDriver = null;
        loaded.set(false);
        GudFunctionService.register(null);
    }

    // ===== Version-Aware Driver Selection =====

    /**
     * Gets the driver compatible with the specified GemFire version.
     * Returns the highest-version driver whose supported version is &gt;= the requested version.
     * Version comparison is performed numerically (not lexicographically).
     *
     * @param gemfireVersion the GemFire version to find a driver for (e.g. {@code "10.1"})
     * @return the best matching driver
     * @throws IllegalStateException if no compatible driver is found
     */
    public static GudDriver getDriverForGemFireVersion(String gemfireVersion) {
        loadDriversIfNeeded();
        GudApiVersion requested = GudApiVersion.parse(gemfireVersion);
        return drivers.values().stream()
            .filter(d -> GudApiVersion.parse(d.getSupportedVersion()).compareTo(requested) >= 0)
            .max(Comparator.comparing(d -> GudApiVersion.parse(d.getSupportedVersion())))
            .orElseThrow(() -> new IllegalStateException(
                "No driver found for GemFire version: " + gemfireVersion));
    }

    /**
     * Gets all drivers that support a specific capability.
     *
     * @param capability the capability to check for
     * @return list of drivers supporting the capability
     */
    public static List<GudDriver> getDriversWithCapability(GudCapability capability) {
        loadDriversIfNeeded();
        return drivers.values().stream()
            .filter(d -> d.supportsCapability(capability))
            .collect(Collectors.toList());
    }

    /**
     * Checks if the default driver supports the given capability.
     *
     * @param capability the capability to check for
     * @return {@code true} if the default driver supports the capability
     */
    public static boolean isCapabilityAvailable(GudCapability capability) {
        return getDefaultDriver().supportsCapability(capability);
    }

    /**
     * Gets all registered drivers.
     *
     * @return list of all registered drivers
     */
    public static List<GudDriver> getAllDrivers() {
        loadDriversIfNeeded();
        return drivers.values().stream().collect(Collectors.toList());
    }

    // ===== Internal Helpers =====

    /**
     * Pushes factories from the given driver into {@link GudCacheProvider} so that the
     * ServiceLoader path in the provider and the driver-manager path stay coordinated.
     * Uses the Supplier form so each call to {@code GudCacheProvider.createClientCacheFactory()}
     * returns a freshly-constructed factory instance.
     */
    private static void pushFactoriesToCacheProvider(GudDriver driver) {
        try {
            GudCacheProvider.configure(
                driver::createClientCacheFactory,
                driver.createJndiBinding(),
                driver.createPoolManager()
            );
            GudFunctionService functionService = driver.createFunctionService();
            if (functionService != null) {
                GudFunctionService.register(functionService);
            }
        } catch (Exception ex) {
            logger.warn("Failed to push driver factories to GudCacheProvider: {}", ex.getMessage());
        }
    }

    private static void loadDriversIfNeeded() {
        if (!loaded.get()) {
            loadDrivers();
        }
    }
}
