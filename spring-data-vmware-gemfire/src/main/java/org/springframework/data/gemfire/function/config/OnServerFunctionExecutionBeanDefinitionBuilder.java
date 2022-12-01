/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.config;

import org.springframework.data.gemfire.function.execution.GemfireOnServerFunctionTemplate;

/**
 * @author David Turanski
 * @author John Blum
 */
class OnServerFunctionExecutionBeanDefinitionBuilder extends ServerBasedFunctionExecutionBeanDefinitionBuilder {

	OnServerFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
		super(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.ServerBasedFunctionExecutionBeanDefinitionBuilder
	 * 	#getGemfireFunctionOperationsClass()
	 */
	@Override
	protected Class<?> getGemfireFunctionOperationsClass() {
		 return GemfireOnServerFunctionTemplate.class;
	}
}
