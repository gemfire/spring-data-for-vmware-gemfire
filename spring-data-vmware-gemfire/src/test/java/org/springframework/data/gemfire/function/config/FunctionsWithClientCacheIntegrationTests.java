/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.execute.Function;

import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing Apache Geode {@link Function} execution configuration using SDG's {@link Function}
 * annotation configuration support.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see Function
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
public class FunctionsWithClientCacheIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void doNothing() {
		// Just make sure this comes up for SGF-186.
	}
}
