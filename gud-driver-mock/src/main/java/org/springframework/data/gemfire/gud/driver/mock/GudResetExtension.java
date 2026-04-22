/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: JUnit 5 Extension that resets GudCacheProvider/GudDriverManager static state
 *             before and after each test; forces re-discovery of registered drivers via
 *             ServiceLoader so the MockGudDriver (on the test classpath) is always fresh
 */

package org.springframework.data.gemfire.gud.driver.mock;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.core.GudDriverManager;

/**
 * JUnit 5 extension that isolates tests from shared {@link GudCacheProvider} and
 * {@link GudDriverManager} static state.
 * <p>
 * Before each test the provider and driver registry are cleared, then
 * {@link GudDriverManager#loadDrivers()} is invoked so that the
 * {@code MockGudDriver} (registered via {@code META-INF/services}) is rediscovered
 * and its factories are pushed into {@link GudCacheProvider}.
 * <p>
 * Apply to a test class via {@code @ExtendWith(GudResetExtension.class)}.
 */
public class GudResetExtension implements BeforeEachCallback, AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        resetGudState();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        resetGudState();
    }

    private static void resetGudState() {
        GudCacheProvider.clear();
        GudDriverManager.clearDrivers();
        GudDriverManager.loadDrivers();
    }
}
