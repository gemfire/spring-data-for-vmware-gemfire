/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.result;

import java.math.BigDecimal;
import java.util.List;

import org.apache.geode.cache.GemFireCache;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Function Execution Return Types.
 *
 * @author Patrick Johnson
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
 * @see org.springframework.data.gemfire.function.annotation.OnRegion
 * @see org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions
 * @see org.springframework.data.gemfire.function.config.EnableGemfireFunctions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FunctionResultTypeIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class FunctionResultTypeIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private MixedResultTypeFunctionExecutions functionExecutions;

	@Test
	public void singleResultFunctionsExecuteCorrectly() {
		BigDecimal num = functionExecutions.returnFive();
		Assertions.assertThat(num.doubleValue()).isEqualTo(5);
	}

	@Test
	public void listResultFunctionsExecuteCorrectly() {
		List<BigDecimal> list = functionExecutions.returnList();
		Assertions.assertThat(list.size()).isEqualTo(1);
	}

	@Test
	public void primitiveResultFunctionsExecuteCorrectly() {
		int num = functionExecutions.returnPrimitive();
		Assertions.assertThat(num).isEqualTo(7);
	}

	@PeerCacheApplication(name = "FunctionResultTypeIntegrationTests")
	@EnableGemfireFunctionExecutions(basePackageClasses = MixedResultTypeFunctionExecutions.class)
	@EnableGemfireFunctions
	public static class TestConfiguration {

		@Bean("Numbers")
		ReplicatedRegionFactoryBean<Long, BigDecimal> numbersRegion(GemFireCache gemFireCache) {

			ReplicatedRegionFactoryBean<Long, BigDecimal> numbersRegion = new ReplicatedRegionFactoryBean<>();

			numbersRegion.setCache(gemFireCache);
			numbersRegion.setPersistent(false);

			return numbersRegion;
		}

		@Bean
		MixedResultTypeFunctions functions() {
			return new MixedResultTypeFunctions();
		}
	}
}
