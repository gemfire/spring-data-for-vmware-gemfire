/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;

/**
 * Creates an {@literal OnMembers} {@link Function} {@link Execution} for all members
 * using {@link FunctionService#onMembers(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Execution
 * @see Function
 * @see FunctionService
 * @see AbstractFunctionExecution
 * @since 1.3.0
 */
class OnAllMembersFunctionExecution extends AbstractFunctionExecution {

  	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMembers();
	}
}
