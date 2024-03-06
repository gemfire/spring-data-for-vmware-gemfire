/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.util.Assert;

/**
 * Constructs an {@link Execution} using {@link FunctionService#onServer(Pool)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Pool
 * @see Execution
 * @see FunctionService
 * @see AbstractFunctionExecution
 */
class OnServerUsingPoolFunctionExecution extends AbstractFunctionExecution {

	private final Pool pool;

	OnServerUsingPoolFunctionExecution(Pool pool) {

		Assert.notNull(pool, "Pool must not be null");

		this.pool = pool;
	}

	protected Pool getPool() {
		return this.pool;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onServer(getPool());
	}
}
