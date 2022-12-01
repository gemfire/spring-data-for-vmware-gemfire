/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.ResultCollector;

/**
 * Unit Tests for {@link AbstractFunctionTemplate}.
 *
 * @author John Blum
 * @see Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see Function
 * @see ResultCollector
 * @see AbstractFunctionExecution
 * @see AbstractFunctionTemplate
 * @since 1.7.0
 */
@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("rawtypes")
public class AbstractFunctionTemplateUnitTests {

	@Mock
	private AbstractFunctionExecution mockFunctionExecution;

	@Mock
	private Function mockFunction;

	@Mock
	private ResultCollector mockResultCollector;

	@Test
	public void executeWithFunctionAndArgs() {

		Object[] args = { "test", "testing", "tested" };

		List<Object> results = Arrays.asList(args);

		when(mockFunctionExecution.setArguments(args)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setFunction(mockFunction)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setResultCollector(mockResultCollector)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setTimeout(500)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.execute()).thenReturn(results);

		AbstractFunctionTemplate functionTemplate = new AbstractFunctionTemplate() {

			@Override
			protected AbstractFunctionExecution getFunctionExecution() {
				return mockFunctionExecution;
			}
		};

		functionTemplate.setResultCollector(mockResultCollector);
		functionTemplate.setTimeout(500);

		Iterable<Object> actualResults = functionTemplate.execute(mockFunction, args);

		assertThat(functionTemplate.getResultCollector()).isEqualTo(mockResultCollector);
		assertThat(actualResults).isNotNull();
		assertThat(actualResults).isEqualTo((results));

		verify(mockFunctionExecution, times(1)).setArguments(args);
		verify(mockFunctionExecution, times(1)).setFunction(mockFunction);
		verify(mockFunctionExecution, times(1)).setResultCollector(eq(mockResultCollector));
		verify(mockFunctionExecution, times(1)).setTimeout(500);
		verify(mockFunctionExecution, times(1)).execute();
	}

	@Test
	public void executeAndExtractWithFunctionAndArgs() {

		Object[] args = { "test", "testing", "tested" };

		when(mockFunctionExecution.setArguments(args)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setFunction(mockFunction)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setResultCollector(mockResultCollector)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setTimeout(500)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.executeAndExtract()).thenReturn(args[0]);

		AbstractFunctionTemplate functionTemplate = new AbstractFunctionTemplate() {

			@Override
			protected AbstractFunctionExecution getFunctionExecution() {
				return mockFunctionExecution;
			}
		};

		functionTemplate.setResultCollector(mockResultCollector);
		functionTemplate.setTimeout(500);

		String result = functionTemplate.executeAndExtract(mockFunction, args);

		assertThat(functionTemplate.getResultCollector()).isEqualTo(mockResultCollector);
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo("test");

		verify(mockFunctionExecution, times(1)).setArguments(args);
		verify(mockFunctionExecution, times(1)).setFunction(mockFunction);
		verify(mockFunctionExecution, times(1)).setResultCollector(eq(mockResultCollector));
		verify(mockFunctionExecution, times(1)).setTimeout(500);
		verify(mockFunctionExecution, times(1)).executeAndExtract();
	}

	@Test
	public void executeWithFunctionIdAndArgs() {

		Object[] args = { "test", "testing", "tested" };
		List<Object> results = Arrays.asList(args);

		when(mockFunctionExecution.setArguments(args)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setFunctionId("TestFunction")).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setResultCollector(mockResultCollector)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setTimeout(500)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.execute()).thenReturn(results);

		AbstractFunctionTemplate functionTemplate = new AbstractFunctionTemplate() {

			@Override
			protected AbstractFunctionExecution getFunctionExecution() {
				return mockFunctionExecution;
			}
		};

		functionTemplate.setResultCollector(mockResultCollector);
		functionTemplate.setTimeout(500);

		Iterable<Object> actualResults = functionTemplate.execute("TestFunction", args);

		assertThat(functionTemplate.getResultCollector()).isEqualTo(mockResultCollector);
		assertThat(actualResults).isNotNull();
		assertThat(actualResults).isEqualTo(results);

		verify(mockFunctionExecution, times(1)).setArguments(args);
		verify(mockFunctionExecution, times(1)).setFunctionId("TestFunction");
		verify(mockFunctionExecution, times(1)).setResultCollector(eq(mockResultCollector));
		verify(mockFunctionExecution, times(1)).setTimeout(500);
		verify(mockFunctionExecution, times(1)).execute();
	}

	@Test
	public void executeAndExtractWithFunctionIdAndArgs() {

		Object[] args = { "test", "testing", "tested" };

		when(mockFunctionExecution.setArguments(args)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setFunctionId("TestFunction")).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setResultCollector(mockResultCollector)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setTimeout(500)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.executeAndExtract()).thenReturn(args[0]);

		AbstractFunctionTemplate functionTemplate = new AbstractFunctionTemplate() {

			@Override
			protected AbstractFunctionExecution getFunctionExecution() {
				return mockFunctionExecution;
			}
		};

		functionTemplate.setResultCollector(mockResultCollector);
		functionTemplate.setTimeout(500);

		String result = functionTemplate.executeAndExtract("TestFunction", args);

		assertThat(functionTemplate.getResultCollector()).isEqualTo(mockResultCollector);
		assertThat(result).isNotNull();
		assertThat(result).isEqualTo("test");

		verify(mockFunctionExecution, times(1)).setArguments(args);
		verify(mockFunctionExecution, times(1)).setFunctionId("TestFunction");
		verify(mockFunctionExecution, times(1)).setResultCollector(eq(mockResultCollector));
		verify(mockFunctionExecution, times(1)).setTimeout(500);
		verify(mockFunctionExecution, times(1)).executeAndExtract();
	}

	@Test
	public void executeWithNoResultWithFunctionIdAndArgs() {

		Object[] args = { "test", "testing", "tested" };

		when(mockFunctionExecution.setArguments(args)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setFunctionId("TestFunction")).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setResultCollector(mockResultCollector)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.setTimeout(500)).thenReturn(mockFunctionExecution);
		when(mockFunctionExecution.execute(eq(false))).thenReturn(null);

		AbstractFunctionTemplate functionTemplate = new AbstractFunctionTemplate() {

			@Override
			protected AbstractFunctionExecution getFunctionExecution() {
				return mockFunctionExecution;
			}
		};

		functionTemplate.setResultCollector(mockResultCollector);
		functionTemplate.setTimeout(500);

		functionTemplate.executeWithNoResult("TestFunction", args);

		assertThat(functionTemplate.getResultCollector()).isEqualTo(mockResultCollector);

		verify(mockFunctionExecution, times(1)).setArguments(args);
		verify(mockFunctionExecution, times(1)).setFunctionId("TestFunction");
		verify(mockFunctionExecution, times(1)).setResultCollector(eq(mockResultCollector));
		verify(mockFunctionExecution, times(1)).setTimeout(500);
		verify(mockFunctionExecution, times(1)).execute(eq(false));
	}
}
