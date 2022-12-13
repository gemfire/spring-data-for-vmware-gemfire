/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.function.config.two.TestOnRegionFunction;
import org.springframework.data.gemfire.function.execution.GemfireOnRegionFunctionTemplate;
import org.springframework.data.gemfire.function.execution.OnRegionFunctionProxyFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for the execution of Apache Geode {@link Function Functions}
 * using SDG's {@link Function} execution annotation support.
 *
 * @author David Turanski
 * @author John Blum
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FunctionExecutionIntegrationTests.TestConfiguration.class,
	initializers = GemFireMockObjectsApplicationContextInitializer.class)
@SuppressWarnings("unused")
public class FunctionExecutionIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void testProxyFactoryBeanCreated() throws Exception {

		OnRegionFunctionProxyFactoryBean factoryBean =
			applicationContext.getBean("&testFunction", OnRegionFunctionProxyFactoryBean.class);

		Class<?> serviceInterface = TestUtils.readField("functionExecutionInterface", factoryBean);

		assertThat(TestOnRegionFunction.class).isEqualTo(serviceInterface);

		Region<?, ?> regionOne = applicationContext.getBean("r1", Region.class);

		GemfireOnRegionFunctionTemplate template = TestUtils.readField("gemfireFunctionOperations", factoryBean);

		assertThat(TestUtils.<Region<?, ?>>readField("region", template)).isSameAs(regionOne);
	}

	@Configuration
	@EnableGemfireFunctionExecutions(basePackages = "org.springframework.data.gemfire.function.config.two")
	@ImportResource("/org/springframework/data/gemfire/function/config/FunctionExecutionIntegrationTests-context.xml")
	static class TestConfiguration { }

}

