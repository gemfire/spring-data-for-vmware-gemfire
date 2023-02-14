/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Unit tests for {@link MemberBasedFunctionExecutionBeanDefinitionBuilder}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.function.config.MemberBasedFunctionExecutionBeanDefinitionBuilder
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class MemberBasedFunctionExecutionBeanDefinitionBuilderUnitTests {

	@Mock
	private FunctionExecutionConfiguration mockFunctionExecutionConfiguration;

	private MemberBasedFunctionExecutionBeanDefinitionBuilder beanDefinitionBuilder;

	@Before
	public void setup() {
		this.beanDefinitionBuilder =
			new TestMemberBasedFunctionExecutionBeanDefinitionBuilder(this.mockFunctionExecutionConfiguration);
	}

	@Test
	public void getGemfireFunctionOperationsBeanDefinitionBuilderIsSuccessful() {

		when(this.mockFunctionExecutionConfiguration.getAttribute(eq("groups")))
			.thenReturn(" TestGroupOne,  TestGroupTwo ");

		BeanDefinitionBuilder builder =
			this.beanDefinitionBuilder.getGemfireFunctionOperationsBeanDefinitionBuilder(null);

		assertThat(builder).isNotNull();

		BeanDefinition beanDefinition = builder.getRawBeanDefinition();

		assertThat(beanDefinition).isNotNull();
		assertThat(beanDefinition.getBeanClassName()).isEqualTo(Object.class.getName());
		assertThat(beanDefinition.getConstructorArgumentValues().getArgumentCount()).isEqualTo(1);

		ConstructorArgumentValues.ValueHolder constructorArgumentValue =
			beanDefinition.getConstructorArgumentValues().getArgumentValue(0, String[].class);

		assertThat(constructorArgumentValue).isNotNull();
		assertThat((String[]) constructorArgumentValue.getValue()).containsExactly("TestGroupOne", "TestGroupTwo");

		verify(this.mockFunctionExecutionConfiguration, times(1))
			.getAttribute(eq("groups"));
	}

	private static final class TestMemberBasedFunctionExecutionBeanDefinitionBuilder
			extends MemberBasedFunctionExecutionBeanDefinitionBuilder {

		private TestMemberBasedFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
			super(configuration);
		}

		@Override
		protected Class<?> getGemfireOperationsClass() {
			return Object.class;
		}
	}
}
