/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionService
 * @see org.springframework.data.gemfire.function.execution.AbstractFunctionExecution
 * @since 1.3.0
 */
class OnAllMembersFunctionExecution extends AbstractFunctionExecution {

  	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMembers();
	}
}
