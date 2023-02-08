/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Unit Tests for {@link ServerBasedFunctionExecutionBeanDefinitionBuilder}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see ServerBasedFunctionExecutionBeanDefinitionBuilder
 * @since 1.7.0
 */
public class ServerBasedFunctionExecutionBeanDefinitionBuilderUnitTests {

	@Test
	public void getGemfireFunctionOperationsBeanDefinitionBuilder() {

		FunctionExecutionConfiguration mockFunctionExecutionConfiguration =
			mock(FunctionExecutionConfiguration.class, "MockFunctionExecutionConfiguration");

		when(mockFunctionExecutionConfiguration.getAttribute(eq("cache"))).thenReturn(null);
		when(mockFunctionExecutionConfiguration.getAttribute(eq("pool"))).thenReturn("");
		when(mockFunctionExecutionConfiguration.getFunctionExecutionInterface()).thenAnswer(invocation -> Object.class);

		ServerBasedFunctionExecutionBeanDefinitionBuilder builder =
			new ServerBasedFunctionExecutionBeanDefinitionBuilder(mockFunctionExecutionConfiguration) {

				@Override
				protected Class<?> getGemfireFunctionOperationsClass() {
					return Object.class;
				}
			};

		BeanDefinitionBuilder beanDefinitionBuilder =
			builder.getGemfireFunctionOperationsBeanDefinitionBuilder(null);

		assertThat(beanDefinitionBuilder).isNotNull();

		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		assertThat(beanDefinition).isNotNull();
		assertThat(beanDefinition.getBeanClass()).isEqualTo(Object.class);

		ConstructorArgumentValues.ValueHolder constructorArgumentValue =
			beanDefinition.getConstructorArgumentValues().getArgumentValue(0, RuntimeBeanReference.class);

		assertThat(constructorArgumentValue).isNotNull();
		assertThat(((RuntimeBeanReference) constructorArgumentValue.getValue()).getBeanName()).isEqualTo("gemfireCache");
		assertThat(beanDefinition.getPropertyValues().getPropertyValue("pool")).isNull();

		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("cache"));
		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("pool"));
	}

	@Test
	public void getGemfireFunctionOperationsBeanDefinitionBuilderWithCache() {

		FunctionExecutionConfiguration mockFunctionExecutionConfiguration =
			mock(FunctionExecutionConfiguration.class, "MockFunctionExecutionConfiguration");

		when(mockFunctionExecutionConfiguration.getAttribute(eq("cache"))).thenReturn("TestCache");
		when(mockFunctionExecutionConfiguration.getAttribute(eq("pool"))).thenReturn("  ");

		ServerBasedFunctionExecutionBeanDefinitionBuilder builder =
			new ServerBasedFunctionExecutionBeanDefinitionBuilder(mockFunctionExecutionConfiguration) {

				@Override
				protected Class<?> getGemfireFunctionOperationsClass() {
					return Object.class;
				}
			};

		BeanDefinitionBuilder beanDefinitionBuilder =
			builder.getGemfireFunctionOperationsBeanDefinitionBuilder(null);

		assertThat(beanDefinitionBuilder).isNotNull();

		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		assertThat(beanDefinition).isNotNull();
		assertThat(beanDefinition.getBeanClass()).isEqualTo(Object.class);

		ConstructorArgumentValues.ValueHolder constructorArgumentValue =
			beanDefinition.getConstructorArgumentValues().getArgumentValue(0, RuntimeBeanReference.class);

		assertThat(constructorArgumentValue).isNotNull();
		assertThat(((RuntimeBeanReference) constructorArgumentValue.getValue()).getBeanName()).isEqualTo("TestCache");
		assertThat(beanDefinition.getPropertyValues().getPropertyValue("pool")).isNull();

		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("cache"));
		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("pool"));
	}

	@Test
	public void getGemfireFunctionOperationsBeanDefinitionBuilderWithPool() {

		FunctionExecutionConfiguration mockFunctionExecutionConfiguration =
			mock(FunctionExecutionConfiguration.class, "MockFunctionExecutionConfiguration");

		when(mockFunctionExecutionConfiguration.getAttribute(eq("cache"))).thenReturn(null);
		when(mockFunctionExecutionConfiguration.getAttribute(eq("pool"))).thenReturn("TestPool");

		ServerBasedFunctionExecutionBeanDefinitionBuilder builder =
			new ServerBasedFunctionExecutionBeanDefinitionBuilder(mockFunctionExecutionConfiguration) {

				@Override
				protected Class<?> getGemfireFunctionOperationsClass() {
					return Object.class;
				}
			};

		BeanDefinitionBuilder beanDefinitionBuilder =
			builder.getGemfireFunctionOperationsBeanDefinitionBuilder(null);

		assertThat(beanDefinitionBuilder).isNotNull();

		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		assertThat(beanDefinition).isNotNull();
		assertThat(beanDefinition.getBeanClass()).isEqualTo(Object.class);

		ConstructorArgumentValues.ValueHolder constructorArgumentValue =
			beanDefinition.getConstructorArgumentValues().getArgumentValue(0, RuntimeBeanReference.class);

		assertThat(constructorArgumentValue).isNotNull();
		assertThat(((RuntimeBeanReference) constructorArgumentValue.getValue()).getBeanName()).isEqualTo("gemfireCache");

		PropertyValue propertyValue = beanDefinition.getPropertyValues().getPropertyValue("pool");

		assertThat(propertyValue).isNotNull();
		assertThat(((RuntimeBeanReference) propertyValue.getValue()).getBeanName()).isEqualTo("TestPool");

		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("cache"));
		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("pool"));
	}

	@Test
	public void getGemfireFunctionOperationsBeanDefinitionBuilderWithCacheAndPool() {

		FunctionExecutionConfiguration mockFunctionExecutionConfiguration =
			mock(FunctionExecutionConfiguration.class, "MockFunctionExecutionConfiguration");

		when(mockFunctionExecutionConfiguration.getAttribute(eq("cache"))).thenReturn("TestCache");
		when(mockFunctionExecutionConfiguration.getAttribute(eq("pool"))).thenReturn("TestPool");

		ServerBasedFunctionExecutionBeanDefinitionBuilder builder =
			new ServerBasedFunctionExecutionBeanDefinitionBuilder(mockFunctionExecutionConfiguration) {

				@Override
				protected Class<?> getGemfireFunctionOperationsClass() {
					return Object.class;
				}
			};


		BeanDefinitionBuilder beanDefinitionBuilder =
			builder.getGemfireFunctionOperationsBeanDefinitionBuilder(null);

		assertThat(beanDefinitionBuilder).isNotNull();

		AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();

		assertThat(beanDefinition).isNotNull();
		assertThat(beanDefinition.getBeanClass()).isEqualTo(Object.class);

		ConstructorArgumentValues.ValueHolder constructorArgumentValue =
			beanDefinition.getConstructorArgumentValues().getArgumentValue(0, RuntimeBeanReference.class);

		assertThat(constructorArgumentValue).isNotNull();
		assertThat(((RuntimeBeanReference) constructorArgumentValue.getValue()).getBeanName()).isEqualTo("TestCache");

		PropertyValue propertyValue = beanDefinition.getPropertyValues().getPropertyValue("pool");

		assertThat(propertyValue).isNotNull();
		assertThat(((RuntimeBeanReference) propertyValue.getValue()).getBeanName()).isEqualTo("TestPool");

		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("cache"));
		verify(mockFunctionExecutionConfiguration, times(1)).getAttribute(eq("pool"));
	}
}
