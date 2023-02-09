/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.client.Pool;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

/**
 * Creates an {@literal OnServer} {@link Function} {@link Execution} initialized with
 * either a {@link RegionService} or a {@link Pool}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.RegionService
 * @see org.apache.geode.cache.client.Pool
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.function.execution.AbstractClientFunctionTemplate
 */
@SuppressWarnings("unused")
public class GemfireOnServerFunctionTemplate extends AbstractClientFunctionTemplate {

	public GemfireOnServerFunctionTemplate(RegionService cache) {
		super(cache);
	}

	public GemfireOnServerFunctionTemplate(Pool pool) {
		super(pool);
	}

	public GemfireOnServerFunctionTemplate(String poolName) {
		super(poolName);
	}

	@Override
	protected AbstractFunctionExecution newFunctionExecutionUsingPool(Pool pool) {
		return new OnServerUsingPoolFunctionExecution(pool);
	}

	@Override
	protected AbstractFunctionExecution newFunctionExecutionUsingRegionService(RegionService regionService) {
		return new OnServerUsingRegionServiceFunctionExecution(regionService);
	}
}
