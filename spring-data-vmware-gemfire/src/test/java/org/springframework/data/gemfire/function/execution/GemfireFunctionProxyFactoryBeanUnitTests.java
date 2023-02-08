/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.gemfire.function.annotation.FunctionId;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Unit Tests for {@link GemfireFunctionProxyFactoryBean}.
 *
 * @author David Turanski
 * @author John Blum
 * @see AccessibleObject
 * @see Method
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see MethodInvocation
 * @see GemfireFunctionProxyFactoryBean
 */
public class GemfireFunctionProxyFactoryBeanUnitTests {

	private GemfireFunctionOperations functionOperations;

	@Before
	public void setUp() {
		this.functionOperations = mock(GemfireFunctionOperations.class);
	}

	@Test
	public void invoke() {

		MethodInvocation invocation = new TestMethodInvocation(IFoo.class)
			.withMethodNameAndArgTypes("collections", List.class);

		when(this.functionOperations.execute("collections", invocation.getArguments()))
			.thenReturn(Arrays.asList(1, 2, 3));

		GemfireFunctionProxyFactoryBean proxy =
			new GemfireFunctionProxyFactoryBean(IFoo.class, this.functionOperations);

		Object result = proxy.invoke(invocation);

		assertThat(result).isInstanceOf(List.class);
		assertThat((List<?>) result).hasSize(3);

		verify(this.functionOperations, times(1))
			.execute("collections", invocation.getArguments());
	}

	@Test
	public void invokeAndExtractWithAnnotatedFunctionId() {

		MethodInvocation invocation = new TestMethodInvocation(IFoo.class)
			.withMethodNameAndArgTypes("oneArg", String.class);

		when(this.functionOperations.execute("oneArg", invocation.getArguments()))
			.thenReturn(Collections.singleton(1));

		GemfireFunctionProxyFactoryBean proxy =
			new GemfireFunctionProxyFactoryBean(IFoo.class, this.functionOperations);

		Object result = proxy.invoke(invocation);

		assertThat(result).describedAs(result.getClass().getName()).isInstanceOf(Integer.class);
		assertThat(result).isEqualTo(1);

		verify(this.functionOperations, times(1))
			.execute("oneArg", invocation.getArguments());
	}

	@SuppressWarnings("unused")
	private static class TestMethodInvocation implements MethodInvocation {

		private Class<?> type;

		private Class<?>[] argumentTypes;

		private Object[] arguments;

		private String methodName;

		public TestMethodInvocation(Class<?> type) {
			this.type = type;
		}

		public TestMethodInvocation withArguments(Object ...arguments){

			this.arguments = arguments;

			return this;
		}

		public TestMethodInvocation withMethodNameAndArgTypes(String methodName, Class<?>... argTypes) {

			this.methodName = methodName;
			this.argumentTypes = argTypes;

			return this;
		}

		@Override
		public Object[] getArguments() {
			return this.arguments;
		}

		@Override
		public Object proceed() {
			return null;
		}

		@Override
		public Object getThis() {
			return null;
		}

		@Override
		public AccessibleObject getStaticPart() {
			return null;
		}

		@Override
		public Method getMethod() {

			try {
				return this.type.getMethod(this.methodName, this.argumentTypes);
			}
			catch (NoSuchMethodException | SecurityException cause) {
				return null;
			}
		}
	}

	@SuppressWarnings("unused")
	public interface IFoo {

		@FunctionId("oneArg")
		Integer oneArg(String key);

		Integer twoArg(String akey, String bkey);

		List<Integer> collections(List<Integer> args);

		Map<String, Integer> getMapWithNoArgs();

	}
}
