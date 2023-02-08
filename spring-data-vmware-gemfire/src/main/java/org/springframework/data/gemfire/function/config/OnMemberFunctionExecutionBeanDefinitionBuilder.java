/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.config;

import org.springframework.data.gemfire.function.execution.GemfireOnMemberFunctionTemplate;

/**
 * @author David Turanski
 * @author John Blum
 */
class OnMemberFunctionExecutionBeanDefinitionBuilder extends MemberBasedFunctionExecutionBeanDefinitionBuilder {

	OnMemberFunctionExecutionBeanDefinitionBuilder(FunctionExecutionConfiguration configuration) {
		super(configuration);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.MemberBasedFunctionExecutionBeanDefinitionBuilder#getGemfireFunctionOperationsClass()
	 */
	@Override
	protected Class<?> getGemfireOperationsClass() {
		return GemfireOnMemberFunctionTemplate.class;
	}
}
