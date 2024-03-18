/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.data.gemfire.function.GemfireFunctionUtils;
import org.springframework.data.gemfire.function.annotation.Filter;

/**
 *
 * @author David Turanski
 * @author John Blum
 * @see FunctionExecutionMethodMetadata
 */
class RegionFunctionExecutionMethodMetadata extends FunctionExecutionMethodMetadata<RegionMethodMetadata>  {

	public RegionFunctionExecutionMethodMetadata(Class<?> serviceInterface) {
		super(serviceInterface);
	}

	@Override
	protected RegionMethodMetadata newMetadataInstance(Method method) {
		return new RegionMethodMetadata(method);
	}

}

class RegionMethodMetadata extends MethodMetadata {

	private final int filterArgPosition;

	public RegionMethodMetadata(Method method) {

		super(method);

		this.filterArgPosition = GemfireFunctionUtils.getAnnotationParameterPosition(method, Filter.class,
			new Class<?>[] { Set.class });
	}

	public int getFilterArgPosition() {
		return this.filterArgPosition;
	}
}
