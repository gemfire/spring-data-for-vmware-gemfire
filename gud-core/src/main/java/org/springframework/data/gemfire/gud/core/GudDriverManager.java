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
 * 2026-03-11: Created GudDriverManager for driver discovery and management
 * 2026-03-14: Added version-aware driver selection and capability checking
 */

package org.springframework.data.gemfire.gud.core;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manager class for discovering and managing GUD drivers.
 * Uses Java ServiceLoader for driver discovery.
 */
public final class GudDriverManager {

    private static final Logger logger = LoggerFactory.getLogger(GudDriverManager.class);
    private static final ConcurrentMap<String, GudDriver> drivers = new ConcurrentHashMap<>();
    private static volatile GudDriver defaultDriver;

    private GudDriverManager() {
    }

    /**
     * Discovers and loads all available GUD drivers using ServiceLoader.
     */
    public static void loadDrivers() {
        ServiceLoader<GudDriver> serviceLoader = ServiceLoader.load(GudDriver.class);
        for (GudDriver driver : serviceLoader) {
            registerDriver(driver);
        }
    }

    /**
     * Registers a driver with the manager.
     *
     * @param driver the driver to register
     */
    public static void registerDriver(GudDriver driver) {
        if (driver != null) {
            drivers.put(driver.getName(), driver);
            logger.info("Registered GUD driver: {} for GemFire {}", driver.getName(), driver.getSupportedVersion());
            if (defaultDriver == null) {
                defaultDriver = driver;
            }
        }
    }

    /**
     * Gets a driver by name.
     *
     * @param name the driver name
     * @return the driver, or null if not found
     */
    public static GudDriver getDriver(String name) {
        return drivers.get(name);
    }

    /**
     * Gets the default driver.
     *
     * @return the default driver
     * @throws IllegalStateException if no driver is registered
     */
    public static GudDriver getDefaultDriver() {
        if (defaultDriver == null) {
            loadDrivers();
        }
        if (defaultDriver == null) {
            throw new IllegalStateException("No GUD driver registered. Ensure a driver module is on the classpath.");
        }
        return defaultDriver;
    }

    /**
     * Sets the default driver.
     *
     * @param driver the driver to set as default
     */
    public static void setDefaultDriver(GudDriver driver) {
        defaultDriver = driver;
        if (driver != null && !drivers.containsKey(driver.getName())) {
            registerDriver(driver);
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
     * Clears all registered drivers. Primarily for testing.
     */
    public static void clearDrivers() {
        drivers.clear();
        defaultDriver = null;
    }

    // ===== Version-Aware Driver Selection =====

    /**
     * Gets a driver compatible with the specified GemFire version.
     * Returns the highest version driver that supports the requested version.
     *
     * @param gemfireVersion the GemFire version to find a driver for
     * @return the best matching driver
     * @throws IllegalStateException if no compatible driver is found
     */
    public static GudDriver getDriverForGemFireVersion(String gemfireVersion) {
        loadDriversIfNeeded();
        return drivers.values().stream()
            .filter(d -> isVersionCompatible(d.getSupportedVersion(), gemfireVersion))
            .max(Comparator.comparing(GudDriver::getSupportedVersion))
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
     * Checks if any registered driver supports the given capability.
     * Uses the default driver for the check.
     *
     * @param capability the capability to check for
     * @return true if the default driver supports the capability
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

    private static boolean isVersionCompatible(String driverVersion, String requestedVersion) {
        GudApiVersion driver = GudApiVersion.parse(driverVersion);
        GudApiVersion requested = GudApiVersion.parse(requestedVersion);
        return driver.compareTo(requested) >= 0;
    }

    private static void loadDriversIfNeeded() {
        if (drivers.isEmpty()) {
            loadDrivers();
        }
    }
}
