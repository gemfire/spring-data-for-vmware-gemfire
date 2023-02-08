/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.gemfire.function.execution.GemfireOnRegionFunctionTemplate;
import org.springframework.data.gemfire.function.execution.OnRegionFunctionProxyFactoryBean;

/**
 * @author David Turanski
 * @author John Blum
 */
class OnRegionFunctionExecutionBeanDefinitionBuilder extends AbstractFunctionExecutionBeanDefinitionBuilder {

    OnRegionFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
		super(configuration);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected BeanDefinitionBuilder getGemfireFunctionOperationsBeanDefinitionBuilder(BeanDefinitionRegistry registry) {

		BeanDefinitionBuilder functionTemplateBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(GemfireOnRegionFunctionTemplate.class);

		String regionBeanName = String.valueOf(this.configuration.getAttribute("region"));

		functionTemplateBuilder.addConstructorArgReference(regionBeanName);

		return functionTemplateBuilder;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected Class<?> getFunctionProxyFactoryBeanClass() {
		return OnRegionFunctionProxyFactoryBean.class;
	}
}
