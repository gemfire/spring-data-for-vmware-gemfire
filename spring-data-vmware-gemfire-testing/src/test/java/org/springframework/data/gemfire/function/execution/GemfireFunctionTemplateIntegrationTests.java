/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import static org.assertj.core.api.Assertions.assertThat;

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

import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.client.support.PoolManagerPoolResolver;
import org.springframework.data.gemfire.tests.integration.ClientServerIntegrationTestsSupport;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests for Apache Geode Function templates.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.client.ClientCacheFactory
 * @see org.apache.geode.cache.client.Pool
 */
public class GemfireFunctionTemplateIntegrationTests extends ClientServerIntegrationTestsSupport {

	private static GemFireCluster gemFireCluster;

	private ClientCache gemfireCache = null;

	private Pool gemfirePool = null;

	private final PoolResolver poolResolver = new PoolManagerPoolResolver();

	private Region<String, String> gemfireRegion = null;

	@BeforeClass
	public static void startGeodeServer() throws Exception {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(true, "deploy --jar=/testJar.jar", "create region --name=test-function --type=PARTITION");

		gemFireCluster.acceptLicense().start();
	}

	@Before
	public void setupGemFireClient() {

		this.gemfireCache = new ClientCacheFactory()
			.set("name", GemfireFunctionTemplateIntegrationTests.class.getSimpleName())
			.set("log-level", "error")
			.setPoolSubscriptionEnabled(true)
			.addPoolServer("localhost", gemFireCluster.getServerPorts().get(0))
			.create();

		assertThat(this.gemfireCache).isNotNull();
		assertThat(this.gemfireCache.getName()).isEqualTo(GemfireFunctionTemplateIntegrationTests.class.getSimpleName());

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
		GemfireUtils.close(this.gemfireCache);
	}

	@Test
	public void functionTemplatesAreCorrect() {

		verifyFunctionTemplateExecution(new GemfireOnRegionFunctionTemplate(gemfireRegion));
		verifyFunctionTemplateExecution(new GemfireOnServerFunctionTemplate(gemfireCache));
		verifyFunctionTemplateExecution(new GemfireOnServerFunctionTemplate(gemfirePool));
		verifyFunctionTemplateExecution(new GemfireOnServersFunctionTemplate(gemfireCache));
		verifyFunctionTemplateExecution(new GemfireOnServersFunctionTemplate(gemfirePool));
	}

	private void verifyFunctionTemplateExecution(GemfireFunctionOperations functionTemplate) {

		Iterable<String> results = functionTemplate.execute("echoFunction", "1", "2", "3");

		int count = 1;

		for (String result : results) {
			assertThat(result).isEqualTo(String.valueOf(count++));
		}
	}
}
