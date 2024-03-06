/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

/**
 * Creates an {@literal OnServers} {@link Function} {@link Execution} initialized with
 * either a {@link RegionService} or a {@link Pool}.
 *
 * @author David Turanski
 * @author John Blum
 * @see RegionService
 * @see Pool
 * @see Execution
 * @see Function
 * @see AbstractClientFunctionTemplate
 */
@SuppressWarnings("unused")
public class GemfireOnServersFunctionTemplate extends AbstractClientFunctionTemplate {

	public GemfireOnServersFunctionTemplate(RegionService cache) {
		super(cache);
	}

	public GemfireOnServersFunctionTemplate(Pool pool) {
		super(pool);
	}

	public GemfireOnServersFunctionTemplate(String poolName) {
		super(poolName);
	}

	@Override
	protected AbstractFunctionExecution newFunctionExecutionUsingPool(Pool pool) {
		return new OnServersUsingPoolFunctionExecution(pool);
	}

	@Override
	protected AbstractFunctionExecution newFunctionExecutionUsingRegionService(RegionService regionService) {
		return new OnServersUsingRegionServiceFunctionExecution(regionService);
	}
}
