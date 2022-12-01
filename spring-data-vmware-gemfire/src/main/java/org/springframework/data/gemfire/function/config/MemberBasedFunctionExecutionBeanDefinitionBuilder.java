/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.config;

import java.util.Optional;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.gemfire.function.annotation.OnMember;
import org.springframework.data.gemfire.function.annotation.OnMembers;
import org.springframework.data.gemfire.function.execution.GemfireFunctionProxyFactoryBean;
import org.springframework.util.StringUtils;

/**
 * Base class for {@link OnMember} and {@link OnMembers} Function execution
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

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.AbstractFunctionExecutionBeanDefinitionBuilder#getGemfireFunctionOperationsBeanDefinitionBuilder(org.springframework.beans.factory.support.BeanDefinitionRegistry)
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


	/* (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.AbstractFunctionExecutionBeanDefinitionBuilder#getFunctionProxyFactoryBeanClass()
	 */
	@Override
	protected Class<?> getFunctionProxyFactoryBeanClass() {
		return GemfireFunctionProxyFactoryBean.class;
	}

	protected abstract Class<?> getGemfireOperationsClass();

}
