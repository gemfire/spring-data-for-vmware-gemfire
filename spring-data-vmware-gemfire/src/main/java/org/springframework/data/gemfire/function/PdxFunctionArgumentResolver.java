/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;

import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.pdx.PdxInstance;

import org.springframework.lang.NonNull;
import org.springframework.util.ClassUtils;

/**
 * {@link PdxFunctionArgumentResolver} is a {@link FunctionArgumentResolver} that automatically resolves PDX types
 * when Apache Geode is configured with {@literal read-serialized} set to {@literal true}, but the application
 * domain model classes are actually on the application classpath.
 *
 * @author John Blum
 * @see PdxInstance
 * @see DefaultFunctionArgumentResolver
 * @since 1.5.2
 */
@SuppressWarnings("unused")
class PdxFunctionArgumentResolver extends DefaultFunctionArgumentResolver {

	@Override
	@SuppressWarnings("rawtypes")
	public Object[] resolveFunctionArguments(@NonNull FunctionContext functionContext) {

		Object[] functionArguments = super.resolveFunctionArguments(functionContext);

		if (isPdxSerializerConfigured()) {

			int index = 0;

			for (Object functionArgument : functionArguments) {
				if (functionArgument instanceof PdxInstance) {

					String className = ((PdxInstance) functionArgument).getClassName();

					if (isDeserializationNecessary(className)) {
						functionArguments[index] = ((PdxInstance) functionArgument).getObject();
					}
				}

				index++;
			}
		}

		return functionArguments;
	}

	@Override
	public Method getFunctionAnnotatedMethod() {
		throw new UnsupportedOperationException("Not Implemented");
	}

	boolean isPdxSerializerConfigured() {

		try {
			return (CacheFactory.getAnyInstance().getPdxSerializer() != null);
		}
		catch (CacheClosedException ignore) {
			return false;
		}
	}

	boolean isDeserializationNecessary(final String className) {
		return (isOnClasspath(className) && functionAnnotatedMethodHasParameterOfType(className));
	}

	boolean isOnClasspath(final String className) {
		return ClassUtils.isPresent(className, Thread.currentThread().getContextClassLoader());
	}

	boolean functionAnnotatedMethodHasParameterOfType(final String className) {

		for (Class<?> parameterType : getFunctionAnnotatedMethod().getParameterTypes()) {
			if (parameterType.getName().equals(className)) {
				return true;
			}
		}

		return false;
	}
}
