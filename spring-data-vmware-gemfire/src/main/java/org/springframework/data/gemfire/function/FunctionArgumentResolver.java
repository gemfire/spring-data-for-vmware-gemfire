/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import java.lang.reflect.Method;

import org.apache.geode.cache.execute.FunctionContext;

/**
 * The FunctionArgumentResolver interface is a Strategy Interface for resolving Function invocation arguments,
 * given a {@link FunctionContext}.
 *
 * @author David Turanski
 * @author John Blum
 * @see FunctionContext
 * @since 1.3.0
 */
interface FunctionArgumentResolver {

	Method getFunctionAnnotatedMethod();

	Object[] resolveFunctionArguments(FunctionContext functionContext);

}
