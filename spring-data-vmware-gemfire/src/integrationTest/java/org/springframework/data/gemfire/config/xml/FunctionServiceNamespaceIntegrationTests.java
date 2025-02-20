/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link Function} configuration, declaration and registration
 * using SDG XML namespace configuration metadata.
 *
 * @author Costin Leau
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionContext
 * @see org.apache.geode.cache.execute.FunctionService
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class FunctionServiceNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void functionsAreRegistered() {

		Assertions.assertThat(FunctionService.getRegisteredFunctions().size()).isEqualTo(2);
		Assertions.assertThat(FunctionService.getFunction("function1")).isNotNull();
		Assertions.assertThat(FunctionService.getFunction("function2")).isNotNull();
	}

	public static class FunctionOne implements Function<Object> {

		@Override
		public void execute(FunctionContext functionContext) { }

		@Override
		public String getId() {
			return "function1";
		}
	}

	public static class FunctionTwo implements Function<Object> {

		@Override
		public void execute(FunctionContext functionContext) { }

		@Override
		public String getId() {
			return "function2";
		}
	}
}
