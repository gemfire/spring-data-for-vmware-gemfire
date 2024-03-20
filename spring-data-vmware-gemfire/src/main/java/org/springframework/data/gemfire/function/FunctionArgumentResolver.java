/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

/**
 * The {@link FunctionArgumentResolver} interface is a {@literal Strategy} interface for resolving {@link Function}
 * invocation arguments from the given {@link FunctionContext}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Function
 * @see FunctionContext
 * @since 1.3.0
 */
interface FunctionArgumentResolver {

	Method getFunctionAnnotatedMethod();

	@SuppressWarnings("rawtypes")
	Object[] resolveFunctionArguments(FunctionContext functionContext);

}
