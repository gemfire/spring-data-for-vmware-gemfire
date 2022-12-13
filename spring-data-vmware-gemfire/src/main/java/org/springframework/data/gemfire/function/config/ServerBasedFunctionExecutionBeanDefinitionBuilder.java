/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import java.util.Optional;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.function.annotation.OnServer;
import org.springframework.data.gemfire.function.annotation.OnServers;
import org.springframework.data.gemfire.function.execution.GemfireFunctionProxyFactoryBean;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link OnServer} and {@link OnServers} {@link Function} {@link Execution}
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

	/**
	 * @inheritDoc
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

	/**
	 * @inheritDoc
	 */
	@Override
	protected Class<?> getFunctionProxyFactoryBeanClass() {
		return GemfireFunctionProxyFactoryBean.class;
	}

	protected abstract Class<?> getGemfireFunctionOperationsClass();

}
