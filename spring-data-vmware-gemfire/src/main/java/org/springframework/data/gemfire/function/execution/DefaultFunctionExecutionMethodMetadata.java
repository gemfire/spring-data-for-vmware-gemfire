/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.lang.reflect.Method;


/**
 * @author David Turanski
 *
 */
class DefaultFunctionExecutionMethodMetadata extends FunctionExecutionMethodMetadata<MethodMetadata>  {

	/**
	 * @param serviceInterface
	 */
	public DefaultFunctionExecutionMethodMetadata(Class<?> serviceInterface) {
		super(serviceInterface);
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.gemfire.function.config.FunctionExecutionMethodMetadata#newMetadataInstance(java.lang.reflect.Method)
	 */
	@Override
	protected MethodMetadata newMetadataInstance(Method method) {
		return new MethodMetadata(method);
	}

}
