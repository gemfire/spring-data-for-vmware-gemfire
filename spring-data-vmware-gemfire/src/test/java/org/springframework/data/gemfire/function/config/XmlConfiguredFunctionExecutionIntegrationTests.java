// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.function.config.two.TestOnRegionFunction;
import org.springframework.data.gemfire.function.execution.GemfireOnRegionFunctionTemplate;
import org.springframework.data.gemfire.function.execution.OnRegionFunctionProxyFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author David Turanski
 * @author John Blum
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class XmlConfiguredFunctionExecutionIntegrationTests extends IntegrationTestsSupport {

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

		assertThat(TestUtils.<Object>readField("region", template)).isSameAs(regionOne);
	}
}
