/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.function.config;

import java.util.Optional;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.gemfire.function.annotation.OnMember;
import org.springframework.data.gemfire.function.annotation.OnMembers;
import org.springframework.data.gemfire.function.execution.GemfireFunctionProxyFactoryBean;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link OnMember} and {@link OnMembers} {@link GudFunction} {@link GudExecution}
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
