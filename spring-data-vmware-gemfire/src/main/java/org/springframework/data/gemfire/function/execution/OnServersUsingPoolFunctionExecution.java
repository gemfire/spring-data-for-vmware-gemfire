/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.util.Assert;

/**
 * Constructs an {@link Execution} using {@link FunctionService#onServers(Pool)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.FunctionService
 * @see org.springframework.data.gemfire.function.execution.AbstractFunctionExecution
 */
class OnServersUsingPoolFunctionExecution extends AbstractFunctionExecution {

	private final Pool pool;

	OnServersUsingPoolFunctionExecution(Pool pool) {

		Assert.notNull(pool, "Pool must not be null");

		this.pool = pool;
	}

	protected Pool getPool() {
		return pool;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onServers(getPool());
	}
}
