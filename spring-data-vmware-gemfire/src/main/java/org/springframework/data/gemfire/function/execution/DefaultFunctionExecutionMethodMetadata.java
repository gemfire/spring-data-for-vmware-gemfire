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
	 * Constructs a new instance of {@link DefaultFunctionExecutionMethodMetadata} initialized with
	 * the {@link org.apache.geode.cache.execute.Function} {@link org.apache.geode.cache.execute.Execution}
	 * {@link Class interface}.
	 *
	 * @param serviceInterface {@link org.apache.geode.cache.execute.Function}
	 * {@link org.apache.geode.cache.execute.Execution} {@link Class interface}.
	 */
	public DefaultFunctionExecutionMethodMetadata(Class<?> serviceInterface) {
		super(serviceInterface);
	}

	@Override
	protected MethodMetadata newMetadataInstance(Method method) {
		return new MethodMetadata(method);
	}
}
