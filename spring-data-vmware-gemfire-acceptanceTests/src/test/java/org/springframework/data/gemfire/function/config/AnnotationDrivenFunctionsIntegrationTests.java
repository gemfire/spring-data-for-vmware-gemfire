/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.function.annotation.Filter;
import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.data.gemfire.function.annotation.RegionData;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for SDG Annotation-driver Apache Geode {@link Function} configuration.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionService
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class AnnotationDrivenFunctionsIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void testAnnotatedFunctions() {

		Assertions.assertThat(FunctionService.isRegistered("foo")).isTrue();

		Function<?> function = FunctionService.getFunction("foo");

		Assertions.assertThat(function.isHA()).isFalse();
		Assertions.assertThat(function.optimizeForWrite()).isFalse();
		Assertions.assertThat(function.hasResult()).isFalse();
		Assertions.assertThat(FunctionService.isRegistered("bar")).isTrue();

		function = FunctionService.getFunction("bar");

		Assertions.assertThat(function.isHA()).isTrue();
		Assertions.assertThat(function.optimizeForWrite()).isFalse();
		Assertions.assertThat(function.hasResult()).isTrue();
		Assertions.assertThat(FunctionService.isRegistered("foo2")).isTrue();

		function = FunctionService.getFunction("foo2");

		Assertions.assertThat(function.isHA()).isTrue();
		Assertions.assertThat(function.optimizeForWrite()).isTrue();
		Assertions.assertThat(function.hasResult()).isTrue();
		Assertions.assertThat(FunctionService.isRegistered("injectFilter")).isTrue();

		function = FunctionService.getFunction("injectFilter");

		Assertions.assertThat(function.isHA()).isTrue();
		Assertions.assertThat(function.optimizeForWrite()).isTrue();
		Assertions.assertThat(function.hasResult()).isTrue();
	}

	@Component
	public static class FooFunction {

		@GemfireFunction
		public void foo() { }

		@GemfireFunction(HA = true, optimizeForWrite = false)
		public String bar() {
			return null;
		}
	}

	@Component
	public static class Foo2Function {

		@GemfireFunction(id = "foo2", HA = true, optimizeForWrite = true)
		public List<String> foo(Object someVal, @RegionData Map<?, ?> region, Object someOtherValue) {
			return null;
		}

		@GemfireFunction(id = "injectFilter", HA = true, optimizeForWrite = true)
		public List<String> injectFilter(@Filter Set<?> keySet) {
			return null;
		}
	}
}
