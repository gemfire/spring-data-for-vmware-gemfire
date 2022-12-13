/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

/**
 * {@link FunctionArgumentResolver} used to resolve {@link Object[] arguments} passed to
 * an Apache Geode {@link Function} during execution.
 *
 * @author David Turanski
 * @author John Blum
 * @see Function
 * @see FunctionContext
 * @see FunctionArgumentResolver
 * @since 1.3.0
 */
class DefaultFunctionArgumentResolver implements FunctionArgumentResolver {

	private static final Object[] EMPTY_ARRAY = new Object[0];

	@Override
	public Method getFunctionAnnotatedMethod() {
		throw new UnsupportedOperationException("Not Implemented");
	}

	@Override
	public Object[] resolveFunctionArguments(final FunctionContext functionContext) {

		return isArray(functionContext.getArguments())
			? toObjectArray((Object[]) functionContext.getArguments())
			: getArguments(functionContext);
	}

	private boolean isArray(final Object value) {
		return value != null && value.getClass().isArray();
	}

	private Object[] toObjectArray(final Object[] arguments) {

		Object[] result = new Object[arguments.length];

		System.arraycopy(arguments, 0, result, 0, arguments.length);

		return result;
	}

	private Object[] getArguments(final FunctionContext context) {

		Object arguments = context.getArguments();

		return arguments != null ? new Object[] { arguments } : EMPTY_ARRAY;
	}
}
