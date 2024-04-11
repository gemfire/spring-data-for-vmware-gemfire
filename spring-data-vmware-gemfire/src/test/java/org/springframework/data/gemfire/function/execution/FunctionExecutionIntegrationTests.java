/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientCacheFactory;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.Pool;

import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.client.support.PoolManagerPoolResolver;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests for SDG Function support.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.execute.Function
 */
public class FunctionExecutionIntegrationTests {

	private ClientCache gemfireCache = null;

	private Pool gemfirePool = null;

	private final PoolResolver poolResolver = new PoolManagerPoolResolver();

	private Region<String, String> gemfireRegion = null;

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void setupGemFireServer() {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(false, "deploy --jar=/testJar.jar",
						"create region --name=test-function --type=REPLICATE",
						"put --region=/test-function --key=one --value=1",
						"put --region=/test-function --key=two --value=2",
						"put --region=/test-function --key=three --value=3");

		gemFireCluster.acceptLicense().start();
	}

	@Before
	public void setupGemFireClient() {

		this.gemfireCache = new ClientCacheFactory()
			.set("name", FunctionExecutionIntegrationTests.class.getSimpleName())
			.set("log-level", "error")
			.setPoolSubscriptionEnabled(true)
			.addPoolServer("localhost", gemFireCluster.getServerPorts().get(0))
			.create();

		assertThat(this.gemfireCache).isNotNull();
		assertThat(this.gemfireCache.getName()).isEqualTo(FunctionExecutionIntegrationTests.class.getSimpleName());

		this.gemfireRegion = this.gemfireCache.<String, String>createClientRegionFactory(ClientRegionShortcut.PROXY)
			.create("test-function");

		assertThat(this.gemfireRegion).isNotNull();
		assertThat(this.gemfireRegion.getName()).isEqualTo("test-function");

		this.gemfirePool = this.poolResolver.resolve("DEFAULT");

		assertThat(this.gemfirePool).isNotNull();
		assertThat(this.gemfirePool.getName()).isEqualTo("DEFAULT");
	}

	@After
	public void tearDownGemFireClient() {
		Optional.ofNullable(this.gemfireCache).ifPresent(cache -> SpringExtensions.safeDoOperation(() -> cache.close()));
	}

	@Test
	public void basicFunctionExecutionsAreCorrect() {

		verifyFunctionExecution(new OnServerUsingPoolFunctionExecution(gemfirePool));
		verifyFunctionExecution(new OnRegionFunctionExecution(gemfireRegion));
		verifyFunctionExecution(new OnServerUsingRegionServiceFunctionExecution(gemfireCache));
		verifyFunctionExecution(new OnServersUsingRegionServiceFunctionExecution(gemfireCache));
	}

	private void verifyFunctionExecution(AbstractFunctionExecution functionExecution) {

		Iterable<String> results = functionExecution
			.setArguments("1", "2", "3")
			.setFunctionId(EchoFunction.FUNCTION_ID)
			.execute();

		int count = 1;

		for (String result : results) {
			assertThat(result).isEqualTo(String.valueOf(count++));
		}
	}
}
