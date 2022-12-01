/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.config;

import java.util.Optional;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.function.annotation.OnServer;
import org.springframework.data.gemfire.function.annotation.OnServers;
import org.springframework.data.gemfire.function.execution.GemfireFunctionProxyFactoryBean;
import org.springframework.util.StringUtils;

/**
 * Base class for {@link OnServer} and {@link OnServers} Function execution
 * {@link BeanDefinitionBuilder BeanDefinitionBuilders}.
 *
 * @author David Turanski
 * @author John Blum
 * @see BeanDefinitionBuilder
 * @see BeanDefinitionRegistry
 * @see OnServer
 * @see OnServers
 * @see AbstractFunctionExecutionBeanDefinitionBuilder
 */
abstract class ServerBasedFunctionExecutionBeanDefinitionBuilder
		extends AbstractFunctionExecutionBeanDefinitionBuilder {

	ServerBasedFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
		super(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.AbstractFunctionExecutionBeanDefinitionBuilder
	 * 	#getGemfireFunctionOperationsBeanDefinitionBuilder(org.springframework.beans.factory.support.BeanDefinitionRegistry)
	 */
	@Override
	protected BeanDefinitionBuilder getGemfireFunctionOperationsBeanDefinitionBuilder(BeanDefinitionRegistry registry) {

		String resolvedCacheBeanName = Optional.ofNullable(this.configuration.getAttribute("cache"))
			.map(String::valueOf)
			.filter(StringUtils::hasText)
			.orElse(GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME);

		Optional<String> poolBeanName = Optional.ofNullable(this.configuration.getAttribute("pool"))
			.map(String::valueOf)
			.filter(StringUtils::hasText);

		BeanDefinitionBuilder functionTemplateBuilder =
			BeanDefinitionBuilder.genericBeanDefinition(getGemfireFunctionOperationsClass());

		functionTemplateBuilder.addConstructorArgReference(resolvedCacheBeanName);

		poolBeanName.ifPresent(it -> {
			functionTemplateBuilder.addDependsOn(it);
			functionTemplateBuilder.addPropertyReference("pool", it);
		});

		return functionTemplateBuilder;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.AbstractFunctionExecutionBeanDefinitionBuilder
	 * 	#getFunctionProxyFactoryBeanClass()
	 */
	@Override
	protected Class<?> getFunctionProxyFactoryBeanClass() {
		return GemfireFunctionProxyFactoryBean.class;
	}

	protected abstract Class<?> getGemfireFunctionOperationsClass();

}
