/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution.onservers;

import java.util.List;

import org.apache.geode.cache.execute.Function;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions;
import org.springframework.data.gemfire.function.sample.AllServersAdminFunctions;
import org.springframework.data.gemfire.function.sample.Metric;
import org.springframework.data.gemfire.function.sample.SingleServerAdminFunctions;
import org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests testing the return values from {@link Function} executions.
 *
 * @author Patrick Johnson
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.function.config.EnableGemfireFunctionExecutions
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@SuppressWarnings("unused")
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = FunctionsReturnResultsFromAllServersIntegrationTests.TestConfiguration.class)
public class FunctionsReturnResultsFromAllServersIntegrationTests extends ClientServerIntegrationTestsSupport {

	private static final int NUMBER_OF_METRICS = 10;

	private static GemFireCluster gemFireCluster;

	@Autowired
	private AllServersAdminFunctions allServersAdminFunctions;

	@Autowired
	private SingleServerAdminFunctions singleServerAdminFunctions;

	@BeforeClass
	public static void startGemFireServer() {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 2);

		gemFireCluster.acceptLicense().start();
		gemFireCluster.getContainers().values().forEach(container -> container
				.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"));

		gemFireCluster.gfshBuilder().build().run("deploy --jar=/testJar.jar");

		System.setProperty(ClientServerIntegrationTestsSupport.GEMFIRE_POOL_SERVERS_PROPERTY,
				String.format("%s[%d],%s[%d]", ClientServerIntegrationTestsSupport.DEFAULT_HOSTNAME,
						gemFireCluster.getServerPorts().get(0), ClientServerIntegrationTestsSupport.DEFAULT_HOSTNAME,
						gemFireCluster.getServerPorts().get(1)));
	}

	@AfterClass
	public static void stopGemFireServer() {
		gemFireCluster.close();
	}

	@Test
	public void executeFunctionOnAllServers() {
		List<List<Metric>> metrics = allServersAdminFunctions.getAllMetrics();

		Assertions.assertThat(metrics.size()).isEqualTo(2);
		Assertions.assertThat(metrics.get(0).size()).isEqualTo(NUMBER_OF_METRICS);
		Assertions.assertThat(metrics.get(1).size()).isEqualTo(NUMBER_OF_METRICS);
	}

	@Test
	public void executeFunctionOnSingleServer() {

		List<Metric> metrics = singleServerAdminFunctions.getAllMetrics();

		Assertions.assertThat(metrics.size()).isEqualTo(NUMBER_OF_METRICS);
	}

	@ClientCacheApplication
	@EnableGemfireFunctionExecutions(basePackageClasses = AllServersAdminFunctions.class)
	static class TestConfiguration { }
}
