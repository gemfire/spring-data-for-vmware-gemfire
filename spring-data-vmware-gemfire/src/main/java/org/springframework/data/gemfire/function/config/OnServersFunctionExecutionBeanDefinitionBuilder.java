/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
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

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.ServerBasedFunctionExecutionBeanDefinitionBuilder
	 * 	#getGemfireFunctionOperationsClass()
	 */
	@Override
	protected Class<?> getGemfireFunctionOperationsClass() {
		return GemfireOnServersFunctionTemplate.class;
	}
}
