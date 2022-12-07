/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;

import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.pdx.PdxInstance;

import org.springframework.util.ClassUtils;

/**
 * The PdxFunctionArgumentResolver class is a Spring Data GemFire FunctionArgumentResolver that automatically resolves
 * PDX types when GemFire is configured with read-serialized set to true, but the application domain classes
 * are actually on the classpath.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.function.DefaultFunctionArgumentResolver
 * @see org.apache.geode.pdx.PdxInstance
 * @since 1.5.2
 */
@SuppressWarnings("unused")
class PdxFunctionArgumentResolver extends DefaultFunctionArgumentResolver {

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.execute.FunctionContext
	 */
	@Override
	public Object[] resolveFunctionArguments(final FunctionContext functionContext) {

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

	/*
	 * (non-Javadoc)
	 * @see java.lang.reflect.Method
	 */
	@Override
	public Method getFunctionAnnotatedMethod() {
		throw new UnsupportedOperationException("Not Implemented");
	}

	/*
	 * (non-Javadoc)
	 * @see org.apache.geode.cache.Cache#getPdxSerializer()
	 * @see org.apache.geode.cache.CacheFactory#getAnyInstance()
	 */
	boolean isPdxSerializerConfigured() {
		try {
			return (CacheFactory.getAnyInstance().getPdxSerializer() != null);
		}
		catch (CacheClosedException ignore) {
			return false;
		}
	}

	/*
	 * (non-Javadac)
	 * @see #isOnClasspath(String)
	 * @see #functionAnnotatedMethodHasParameterOfType(String)
	 */
	boolean isDeserializationNecessary(final String className) {
		return (isOnClasspath(className) && functionAnnotatedMethodHasParameterOfType(className));
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Thread#currentThread()
	 * @see java.lang.Thread#getContextClassLoader()
	 * @see org.springframework.util.ClassUtils#isPresent(String, ClassLoader)
	 */
	boolean isOnClasspath(final String className) {
		return ClassUtils.isPresent(className, Thread.currentThread().getContextClassLoader());
	}

	/*
	 * (non-Javadoc)
	 * @see #getFunctionAnnotatedMethod()
	 * @see java.lang.reflect.Method#getParameterTypes()
	 */
	boolean functionAnnotatedMethodHasParameterOfType(final String className) {
		for (Class<?> parameterType : getFunctionAnnotatedMethod().getParameterTypes()) {
			if (parameterType.getName().equals(className)) {
				return true;
			}
		}

		return false;
	}
}
