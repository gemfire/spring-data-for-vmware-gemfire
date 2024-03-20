/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.FunctionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.fork.ServerProcess;
import org.springframework.data.gemfire.function.sample.ExceptionThrowingFunctionExecution;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the proper behavior of SDG's {@link Function} annotation support when the {@link Function}
 * throws a {@link FunctionException}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionContext
 * @see org.apache.geode.cache.execute.FunctionException
 * @see org.springframework.data.gemfire.fork.ServerProcess
 * @see org.springframework.data.gemfire.function.annotation.GemfireFunction
 * @see org.springframework.data.gemfire.function.sample.ExceptionThrowingFunctionExecution
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.7.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ExceptionThrowingFunctionExecutionIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static ProcessWrapper gemfireServer;

	@Autowired
	private ExceptionThrowingFunctionExecution exceptionThrowingFunctionExecution;

	@BeforeClass
	public static void startGeodeServer() throws IOException {
		startGemFireServer(ServerProcess.class,
			getServerContextXmlFileLocation(ExceptionThrowingFunctionExecutionIntegrationTests.class));
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
