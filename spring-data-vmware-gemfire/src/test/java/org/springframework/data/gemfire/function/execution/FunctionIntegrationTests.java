/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.apache.geode.cache.execute.FunctionContext;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests for SDG Function support.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
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
		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
				.withGfsh(true, "deploy --jar=/testJar.jar", "create region --name=TestRegion --type=PARTITION");

		gemFireCluster.acceptLicense().start();

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

		assertThat(result instanceof Map).as(result.getClass().getName()).isTrue();

		Map<String, Integer> map = (Map<String, Integer>) result;

		assertThat(map.get("one").intValue()).isEqualTo(1);
		assertThat(map.get("two").intValue()).isEqualTo(2);
		assertThat(map.get("three").intValue()).isEqualTo(3);

		result = template.executeAndExtract("collections", Arrays.asList(1, 2, 3, 4, 5));

		assertThat(result instanceof List).as(result.getClass().getName()).isTrue();

		List<?> list = (List<?>) result;

		assertThat(list.isEmpty()).isFalse();
		assertThat(list.size()).isEqualTo(5);

		int expectedNumber = 1;

		for (Object actualNumber : list) {
			assertThat(actualNumber).isEqualTo(expectedNumber++);
		}
	}

	@Test
	@SuppressWarnings("all")
	public void testArrayReturnTypes() {

		Object result = new GemfireOnRegionFunctionTemplate(this.region)
			.executeAndExtract("arrays", new int[] { 1, 2, 3, 4, 5 });

		assertThat(result instanceof int[]).as(result.getClass().getName()).isTrue();
		assertThat(((int[]) result).length).isEqualTo(5);
	}

	@Test
	public void testOnRegionFunctionExecution() {

		GemfireOnRegionOperations template = new GemfireOnRegionFunctionTemplate(this.region);

		assertThat(template.<Integer>execute("oneArg", "two").iterator().next().intValue()).isEqualTo(2);

		assertThat(template.<Integer>execute("twoArg", "two", "three").iterator().next().intValue()).isEqualTo(5);
		assertThat(template.<Integer>executeAndExtract("twoArg", "two", "three").intValue()).isEqualTo(5);
	}

	public static class OneArgFunction implements Function<Integer> {

		@Override
		public String getId() {
			return "oneArg";
		}

		@Override
		public void execute(FunctionContext functionContext) {
			Object[] args = (Object[]) functionContext.getArguments();
			String key = (String) args[0];
			Region<String, Integer> region = functionContext.getCache().getRegion("TestRegion");
			functionContext.getResultSender().lastResult(region.get(key));
		}
	}

	public static class TwoArgFunction implements Function<Integer> {

		@Override
		public String getId() {
			return "twoArg";
		}

		@Override
		public void execute(FunctionContext functionContext) {
			Object[] args = (Object[]) functionContext.getArguments();

			Region<String, Integer> region = functionContext.getCache().getRegion("TestRegion");
			if (region.get(args[0]) != null && region.get(args[1]) != null) {

				functionContext.getResultSender().lastResult(region.get(args[0]) + region.get(args[1]));
			}

			functionContext.getResultSender().lastResult(null);
		}
	}

	public static class CollectionsFunction implements Function<List<Integer>> {

		@Override
		public String getId() {
			return "collections";
		}

		@Override
		public void execute(FunctionContext functionContext) {
			Object[] args = (Object[]) functionContext.getArguments();

			List<Integer> integers = (List<Integer>) args[0];

			functionContext.getResultSender().lastResult(integers);
		}

	}

	public static class GetMapWithNoArgsFunction implements Function<Map<String, Integer>> {
		@Override
		public String getId() {
			return "getMapWithNoArgs";
		}

		@Override
		public void execute(FunctionContext functionContext) {
			Map<String, Integer> region = new HashMap<>();
			for (Map.Entry<Object, Object> entry : functionContext.getCache().getRegion("TestRegion").entrySet()) {
				region.put((String)entry.getKey(), (Integer)entry.getValue());
			}

			functionContext.getResultSender().lastResult(region);
		}
	}

	public static class ArraysFunction implements Function<Object[]> {

		@Override
		public String getId() {
			return "arrays";
		}

		@Override
		public void execute(FunctionContext functionContext) {
			Object[] args = (Object[]) functionContext.getArguments();

			functionContext.getResultSender().lastResult(args[0]);
		}

	}
	public static class NoResultFunction implements Function {

		@Override
		public boolean hasResult() {
			return false;
		}

		@Override
		public boolean isHA() {
			return false;
		}

		@Override
		public String getId() {
			return "noResult";
		}

		@Override
		public void execute(FunctionContext functionContext) {}
	}
}
