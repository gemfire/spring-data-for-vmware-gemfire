/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport.DEFAULT_HOSTNAME;
import static org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport.GEMFIRE_POOL_SERVERS_PROPERTY;

import java.math.BigDecimal;
import java.util.List;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests for Function Execution Return Types.
 *
 * @author Patrick Johnson
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.function.annotation.OnRegion
 * @see org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FunctionResultTypeIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class FunctionResultTypeIntegrationTests extends IntegrationTestsSupport {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGemFireServer() {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(false, "deploy --jar=/testJar.jar",
						"create region --name=Numbers --type=REPLICATE");

		gemFireCluster.acceptLicense().start();

		System.setProperty(GEMFIRE_POOL_SERVERS_PROPERTY,
				String.format("%s[%d]", DEFAULT_HOSTNAME, gemFireCluster.getServerPorts().get(0)));
	}

	@AfterClass
	public static void stopGemFireServer() {
		gemFireCluster.close();
	}

	@Autowired
	private MixedResultTypeFunctionExecutions functionExecutions;

	@Test
	public void singleResultFunctionsExecuteCorrectly() {
		BigDecimal num = functionExecutions.returnFive();
		assertThat(num.doubleValue()).isEqualTo(5);
	}

	@Test
	public void listResultFunctionsExecuteCorrectly() {
		List<BigDecimal> list = functionExecutions.returnList();
		assertThat(list.size()).isEqualTo(1);
	}

	@Test
	public void primitiveResultFunctionsExecuteCorrectly() {
		int num = functionExecutions.returnPrimitive();
		assertThat(num).isEqualTo(7);
	}

	@ClientCacheApplication(name = "FunctionResultTypeIntegrationTests")
	@EnableGemfireFunctionExecutions(basePackageClasses = MixedResultTypeFunctionExecutions.class)
  public static class TestConfiguration {

		@Bean("Numbers")
		ClientRegionFactoryBean<Long, BigDecimal> numbersRegion(GemFireCache gemFireCache) {

			ClientRegionFactoryBean<Long, BigDecimal> numbersRegion = new ClientRegionFactoryBean<>();

			numbersRegion.setCache(gemFireCache);
			numbersRegion.setPersistent(false);
			numbersRegion.setShortcut(ClientRegionShortcut.PROXY);

			return numbersRegion;
		}

		@Bean
		MixedResultTypeFunctions functions() {
			return new MixedResultTypeFunctions();
		}
	}
}
