/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;

import org.apache.geode.cache.execute.FunctionContext;

/**
 * @author David Turanski
 * @author John Blum
 * @since 1.3.0
 */
class DefaultFunctionArgumentResolver implements FunctionArgumentResolver {

	private static final Object[] EMPTY_ARRAY = new Object[0];

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
	 * @see org.springframework.data.gemfire.function.FunctionArgumentResolver#resolveFunctionArguments(org.apache.geode.cache.execute.FunctionContext)
	 */
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
