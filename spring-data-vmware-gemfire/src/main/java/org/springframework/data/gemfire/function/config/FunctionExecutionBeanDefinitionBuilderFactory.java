/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import org.springframework.data.gemfire.function.annotation.OnMember;
import org.springframework.data.gemfire.function.annotation.OnMembers;
import org.springframework.data.gemfire.function.annotation.OnRegion;
import org.springframework.data.gemfire.function.annotation.OnServer;
import org.springframework.data.gemfire.function.annotation.OnServers;

/**
 * Maps the Function Execution Annotation type to the corresponding Function Execution bean definition builder.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.springframework.data.gemfire.function.annotation.OnMember
 * @see org.springframework.data.gemfire.function.annotation.OnMembers
 * @see org.springframework.data.gemfire.function.annotation.OnRegion
 * @see org.springframework.data.gemfire.function.annotation.OnServer
 * @see org.springframework.data.gemfire.function.annotation.OnServers
 * @see OnMemberFunctionExecutionBeanDefinitionBuilder
 * @see OnMembersFunctionExecutionBeanDefinitionBuilder
 * @see OnRegionFunctionExecutionBeanDefinitionBuilder
 * @see OnServerFunctionExecutionBeanDefinitionBuilder
 * @see OnServersFunctionExecutionBeanDefinitionBuilder
 */
abstract class FunctionExecutionBeanDefinitionBuilderFactory {

	static AbstractFunctionExecutionBeanDefinitionBuilder newInstance(FunctionExecutionConfiguration configuration) {

		String functionExecutionAnnotation = configuration.getAnnotationType();

		if (OnMember.class.getName().equals(functionExecutionAnnotation)) {
			return new OnMemberFunctionExecutionBeanDefinitionBuilder(configuration);
		}
		else if (OnMembers.class.getName().equals(functionExecutionAnnotation)) {
			return new OnMembersFunctionExecutionBeanDefinitionBuilder(configuration);
		}
		else if (OnRegion.class.getName().equals(functionExecutionAnnotation)) {
			return new OnRegionFunctionExecutionBeanDefinitionBuilder(configuration);
		}
		else if (OnServer.class.getName().equals(functionExecutionAnnotation)) {
			return new OnServerFunctionExecutionBeanDefinitionBuilder(configuration);
		}
		else if (OnServers.class.getName().equals(functionExecutionAnnotation)) {
			return new OnServersFunctionExecutionBeanDefinitionBuilder(configuration);
		}

		return null;
	}
}
