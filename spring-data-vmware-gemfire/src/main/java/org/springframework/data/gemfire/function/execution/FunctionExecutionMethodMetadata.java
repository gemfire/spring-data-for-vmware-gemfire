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

package org.springframework.data.gemfire.function.execution;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.util.ReflectionUtils;

/**
 * Base class for method-level metadata for a {@link GudFunction} {@link GudExecution} interface.
 *
 * This is used at runtime by the {@link GudFunction} {@link GudExecution} proxy to create
 * the corresponding {@link GudFunction} {@link GudExecution}.
 *
 * @author David Turanski
 * @author John Blum
 * @see MethodMetadata
 */
abstract class FunctionExecutionMethodMetadata<T extends MethodMetadata> {

	protected final Map<Method, T> methodMetadata = new HashMap<>();

	public FunctionExecutionMethodMetadata(Class<?> serviceInterface) {

		ReflectionUtils.doWithMethods(serviceInterface, method -> {

			T methodMetadata = newMetadataInstance(method);

			if (methodMetadata.getFunctionId() == null) {
				methodMetadata.setFunctionId(method.getName());
			}

			this.methodMetadata.put(method, methodMetadata);
		});
	}

	protected abstract T newMetadataInstance(Method method);

	T getMethodMetadata(Method method) {
		return this.methodMetadata.get(method);
	}
}

class MethodMetadata {

	private String functionId;

	public MethodMetadata(Method method) {

		FunctionId functionIdAnnotation = method.getAnnotation(FunctionId.class);

		if (functionIdAnnotation != null) {
			this.functionId = functionIdAnnotation.value();
		}
	}

	public void setFunctionId(String functionId) {
		this.functionId = functionId;
	}

	public String getFunctionId() {
		return this.functionId;
	}
}
