/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests for SDG Function support.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class FunctionIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGemFireServer() throws Exception {
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();
		gemFireCluster.getContainers().values().forEach(container -> container
				.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"));

		gemFireCluster.gfshBuilder().build().run("deploy --jar=/testJar.jar");
		gemFireCluster.gfshBuilder().build().run("create region --name=TestRegion --type=PARTITION");

		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
	}

	@AfterClass
	public static void teardown() {
		gemFireCluster.close();
	}

	@Autowired
	@Qualifier("TestRegion")
	private Region<String, Integer> region;

	@Before
	public void initializeRegion() {
		this.region.put("one", 1);
		this.region.put("two", 2);
		this.region.put("three", 3);
	}

	@Test
	public void withVoidReturnType() {

		GemfireOnRegionOperations template = new GemfireOnRegionFunctionTemplate(this.region);

		// Should work either way but the first invocation traps an exception if there is a result.
		template.executeWithNoResult("noResult");
		template.execute("noResult");
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCollectionReturnTypes() {
		GemfireOnRegionOperations template = new GemfireOnRegionFunctionTemplate(region);

		Object result = template.executeAndExtract("getMapWithNoArgs");

		Assertions.assertThat(result instanceof Map).as(result.getClass().getName()).isTrue();

		Map<String, Integer> map = (Map<String, Integer>) result;

		Assertions.assertThat(map.get("one").intValue()).isEqualTo(1);
		Assertions.assertThat(map.get("two").intValue()).isEqualTo(2);
		Assertions.assertThat(map.get("three").intValue()).isEqualTo(3);

		result = template.executeAndExtract("collections", Arrays.asList(1, 2, 3, 4, 5));

		Assertions.assertThat(result instanceof List).as(result.getClass().getName()).isTrue();

		List<?> list = (List<?>) result;

		Assertions.assertThat(list.isEmpty()).isFalse();
		Assertions.assertThat(list.size()).isEqualTo(5);

		int expectedNumber = 1;

		for (Object actualNumber : list) {
			Assertions.assertThat(actualNumber).isEqualTo(expectedNumber++);
		}
	}

	@Test
	@SuppressWarnings("all")
	public void testArrayReturnTypes() {

		Object result = new GemfireOnRegionFunctionTemplate(this.region)
			.executeAndExtract("arrays", new int[] { 1, 2, 3, 4, 5 });

		Assertions.assertThat(result instanceof int[]).as(result.getClass().getName()).isTrue();
		Assertions.assertThat(((int[]) result).length).isEqualTo(5);
	}

	@Test
	public void testOnRegionFunctionExecution() {

		GemfireOnRegionOperations template = new GemfireOnRegionFunctionTemplate(this.region);

		Assertions.assertThat(template.<Integer>execute("oneArg", "two").iterator().next().intValue()).isEqualTo(2);

		Assertions.assertThat(template.<Integer>execute("twoArg", "two", "three").iterator().next().intValue()).isEqualTo(5);
		Assertions.assertThat(template.<Integer>executeAndExtract("twoArg", "two", "three").intValue()).isEqualTo(5);
	}
}
