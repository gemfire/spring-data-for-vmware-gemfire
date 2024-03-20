/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.springframework.data.gemfire.function.execution.GemfireOnServersFunctionTemplate;

/**
 * @author David Turanski
 * @author John Blum
 */
class OnServersFunctionExecutionBeanDefinitionBuilder extends ServerBasedFunctionExecutionBeanDefinitionBuilder {

	OnServersFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
		super(configuration);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	protected Class<?> getGemfireFunctionOperationsClass() {
		return GemfireOnServersFunctionTemplate.class;
	}
}
