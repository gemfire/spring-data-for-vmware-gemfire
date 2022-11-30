// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.annotation.Annotation;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.security.ResourcePermission;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests testing the registration of Apache Geode/Pivotal GemFire {@link Function Functions}
 * with required permissions specified using the {@link GemfireFunction} {@link Annotation}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
 * @see org.springframework.data.gemfire.function.config.EnableGemfireFunctions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.1.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class FunctionWithRequiredPermissionsRegistrationIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void functionsRegisteredWithRequiredPermissionsSuccessfully() {

		Function<?> function = FunctionService.getFunction("testFunctionWithRequiredPermissions");

		assertThat(function).isNotNull();

		assertThat(function.getRequiredPermissions("test")).containsExactly(
			new ResourcePermission(ResourcePermission.Resource.CLUSTER, ResourcePermission.Operation.MANAGE),
			new ResourcePermission(ResourcePermission.Resource.DATA, ResourcePermission.Operation.READ, "Example")
		);
	}

	@Configuration
	@EnableGemfireFunctions
	static class TestConfiguration {

		@Bean
		TestFunctions testFunctions() {
			return new TestFunctions();
		}
	}

	static class TestFunctions {

		@GemfireFunction(requiredPermissions = { "CLUSTER:MANAGE", "DATA:READ:Example" })
		public void testFunctionWithRequiredPermissions() { }

	}
}
