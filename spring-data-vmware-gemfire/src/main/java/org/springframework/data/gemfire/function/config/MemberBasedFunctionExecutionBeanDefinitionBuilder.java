/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import java.util.Optional;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.gemfire.function.annotation.OnMember;
import org.springframework.data.gemfire.function.annotation.OnMembers;
import org.springframework.data.gemfire.function.execution.GemfireFunctionProxyFactoryBean;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link OnMember} and {@link OnMembers} {@link Function} {@link Execution}
 * {@link BeanDefinitionBuilder BeanDefinitionBuilders}.
 *
 * @author David Turanski
 * @author John Blum
 * @see AbstractFunctionExecutionBeanDefinitionBuilder
 */
abstract class MemberBasedFunctionExecutionBeanDefinitionBuilder
		extends AbstractFunctionExecutionBeanDefinitionBuilder {

	MemberBasedFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
		super(configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BeanDefinitionBuilder getGemfireFunctionOperationsBeanDefinitionBuilder(BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder functionTemplateBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(getGemfireOperationsClass());

		Optional.ofNullable(this.configuration.getAttribute("groups"))
			.map(String::valueOf)
			.map(StringUtils::trimAllWhitespace)
			.filter(StringUtils::hasText)
			.map(StringUtils::commaDelimitedListToStringArray)
			.ifPresent(functionTemplateBuilder::addConstructorArgValue);

		return functionTemplateBuilder;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getFunctionProxyFactoryBeanClass() {
		return GemfireFunctionProxyFactoryBean.class;
	}

	protected abstract Class<?> getGemfireOperationsClass();

}
