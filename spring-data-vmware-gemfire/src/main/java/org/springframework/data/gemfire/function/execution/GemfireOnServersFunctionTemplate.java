/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.gud.api.GudRegionService;

/**
 * Creates an {@literal OnServers} {@link GudFunction} {@link GudExecution} initialized with
 * either a {@link GudRegionService} or a {@link GudPool}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudRegionService
 * @see GudPool
 * @see GudExecution
 * @see GudFunction
 * @see AbstractClientFunctionTemplate
 */
@SuppressWarnings("unused")
public class GemfireOnServersFunctionTemplate extends AbstractClientFunctionTemplate {

	public GemfireOnServersFunctionTemplate(GudRegionService cache) {
		super(cache);
	}

	public GemfireOnServersFunctionTemplate(GudPool pool) {
		super(pool);
	}

	public GemfireOnServersFunctionTemplate(String poolName) {
		super(poolName);
	}

	@Override
	protected AbstractFunctionExecution newFunctionExecutionUsingPool(GudPool pool) {
		return new OnServersUsingPoolFunctionExecution(pool);
	}

	@Override
	protected AbstractFunctionExecution newFunctionExecutionUsingRegionService(GudRegionService regionService) {
		return new OnServersUsingRegionServiceFunctionExecution(regionService);
	}
}
