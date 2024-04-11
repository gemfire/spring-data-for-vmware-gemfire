/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import static org.assertj.core.api.Assertions.assertThat;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.function.sample.ExceptionThrowingFunctionExecution;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.utility.MountableFile;

/**
 * Integration Tests testing the proper behavior of SDG's {@link Function} annotation support when the {@link Function}
 * throws a {@link FunctionException}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionContext
 * @see org.apache.geode.cache.execute.FunctionException
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
 * @see org.springframework.data.gemfire.function.sample.ExceptionThrowingFunctionExecution
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ExceptionThrowingFunctionExecutionIntegrationTests {


	@Autowired
	private ExceptionThrowingFunctionExecution exceptionThrowingFunctionExecution;

  @BeforeClass
	public static void startGemFireServer() {

    GemFireCluster gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
        .withPreStart(GemFireCluster.ALL_GLOB, container -> container.copyFileToContainer(MountableFile.forHostPath(System.getProperty("TEST_JAR_PATH")), "/testJar.jar"))
        .withGfsh(false, "deploy --jar=/testJar.jar");

		gemFireCluster.acceptLicense().start();

		System.setProperty("spring.data.gemfire.cache.server.port", String.valueOf(gemFireCluster.getServerPorts().get(0)));
	}

	@Test(expected = FunctionException.class)
	public void exceptionThrowingFunctionExecutionRethrowsException() {

		try {
			this.exceptionThrowingFunctionExecution.exceptionThrowingFunction();
		}
		catch (FunctionException expected) {

			assertThat(expected).hasMessage("Execution of Function [with ID [exceptionThrowingFunction]] failed");
			assertThat(expected).hasCauseInstanceOf(IllegalArgumentException.class);
			assertThat(expected.getCause()).hasMessage("TEST");
			assertThat(expected.getCause()).hasNoCause();

			throw expected;
		}
	}

	public static class ExceptionThrowingFunction implements Function<Object> {

		@Override
		public String getId() {
			return "exceptionThrowingFunction";
		}

		@Override
		public void execute(FunctionContext context) {
			context.getResultSender().sendException(new IllegalArgumentException("TEST"));
		}
	}
}
