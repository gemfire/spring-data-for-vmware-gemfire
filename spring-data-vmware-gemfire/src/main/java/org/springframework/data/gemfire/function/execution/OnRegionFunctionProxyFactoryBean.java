/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.lang.reflect.Method;
import java.util.Set;

import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * @author David Turanski
 * @author John Blum
 */
public class OnRegionFunctionProxyFactoryBean extends GemfireFunctionProxyFactoryBean {

	private final RegionFunctionExecutionMethodMetadata methodMetadata;

	/**
	 * @param serviceInterface the Service class interface specifying the operations to proxy.
	 * @param gemfireOnRegionOperations an {@link GemfireOnRegionOperations} instance
	 */
	public OnRegionFunctionProxyFactoryBean(Class<?> serviceInterface,
			GemfireOnRegionOperations gemfireOnRegionOperations) {

		super(serviceInterface, gemfireOnRegionOperations);

		this.methodMetadata = new RegionFunctionExecutionMethodMetadata(serviceInterface);
	}

	@Override
	protected Iterable<?> invokeFunction(Method method, Object[] args) {

		GemfireOnRegionOperations gemfireOnRegionOperations =
			(GemfireOnRegionOperations) getGemfireFunctionOperations();

		RegionMethodMetadata regionMethodMetadata = this.methodMetadata.getMethodMetadata(method);

		int filterArgPosition = regionMethodMetadata.getFilterArgPosition();

		String functionId = regionMethodMetadata.getFunctionId();

		Set<?> filter = null;

		// extract filter from args if necessary
		if (filterArgPosition >= 0) {
			filter = (Set<?>) args[filterArgPosition];
			args = ArrayUtils.remove(args, filterArgPosition);
		}

		return filter != null ? gemfireOnRegionOperations.execute(functionId, filter, args)
			: gemfireOnRegionOperations.execute(functionId, args);
	}
}
