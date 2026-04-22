/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: JUnit 4 Rule variant of GudResetExtension; resets GudCacheProvider and
 *             GudDriverManager static state before/after each test and forces a
 *             ServiceLoader rediscovery so MockGudDriver is always registered
 */

package org.springframework.data.gemfire.gud.driver.mock;

import org.junit.rules.ExternalResource;

import org.springframework.data.gemfire.gud.api.GudCacheProvider;
import org.springframework.data.gemfire.gud.core.GudDriverManager;

/**
 * JUnit 4 rule variant of {@link GudResetExtension}.
 * <p>
 * Use on JUnit 4 tests via {@code @Rule public GudResetRule gudResetRule = new GudResetRule();}.
 * Resets {@link GudCacheProvider} and {@link GudDriverManager} static state before/after
 * each test and reloads drivers so the mock driver is freshly registered.
 */
public class GudResetRule extends ExternalResource {

    @Override
    protected void before() {
        resetGudState();
    }

    @Override
    protected void after() {
        resetGudState();
    }

    private static void resetGudState() {
        GudCacheProvider.clear();
        GudDriverManager.clearDrivers();
        GudDriverManager.loadDrivers();
    }
}
