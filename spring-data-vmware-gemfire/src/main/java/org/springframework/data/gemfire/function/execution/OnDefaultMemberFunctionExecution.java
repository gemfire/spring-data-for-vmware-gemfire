/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.distributed.DistributedMember;

/**
 * Creates an {@literal OnMember} {@link Function} {@link Execution} for a single member
 * using {@link FunctionService#onMember(String...)} with no {@link String groups}
 * nor a {@link DistributedMember}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Execution
 * @see Function
 * @see FunctionService
 * @see AbstractFunctionExecution
 * @since 1.3.0
 */
class OnDefaultMemberFunctionExecution extends AbstractFunctionExecution {

	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMember();
	}
}
