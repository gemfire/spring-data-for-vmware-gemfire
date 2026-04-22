/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

// Copyright (c) 2026 Broadcom. All Rights Reserved.

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-17: JUnit 4 base class exposing GudResetRule for tests that exercise GUD-backed
 *             FactoryBeans (isolates GudCacheProvider / GudDriverManager static state)
 */

package org.springframework.data.gemfire;

import org.junit.Rule;

import org.springframework.data.gemfire.gud.driver.mock.GudResetRule;

/**
 * Optional base for JUnit 4 tests that touch {@code GudCacheProvider} or
 * {@code GudDriverManager} static state.  Resets provider + driver registry before and
 * after each test so {@code gud-driver-mock} is rediscovered via {@link java.util.ServiceLoader}.
 */
public abstract class GemFireGudUnitTestSupport {

	@Rule
	public final GudResetRule gudResetRule = new GudResetRule();
}
