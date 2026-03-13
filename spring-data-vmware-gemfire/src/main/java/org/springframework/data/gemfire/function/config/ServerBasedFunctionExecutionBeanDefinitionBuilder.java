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
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.function.annotation.OnServer;
import org.springframework.data.gemfire.function.annotation.OnServers;
import org.springframework.data.gemfire.function.execution.GemfireFunctionProxyFactoryBean;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.util.StringUtils;

/**
 * Abstract base class for {@link OnServer} and {@link OnServers} {@link GudFunction} {@link GudExecution}
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
	 * {@inheritDoc}
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
	 * {@inheritDoc}
	 */
	@Override
	protected Class<?> getFunctionProxyFactoryBeanClass() {
		return GemfireFunctionProxyFactoryBean.class;
	}

	protected abstract Class<?> getGemfireFunctionOperationsClass();

}
