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
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.util.Assert;

/**
 * Constructs an {@link GudExecution} using {@link GudFunctionService#onServers(GudPool)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudPool
 * @see GudExecution
 * @see GudFunctionService
 * @see AbstractFunctionExecution
 */
class OnServersUsingPoolFunctionExecution extends AbstractFunctionExecution {

	private final GudPool pool;

	OnServersUsingPoolFunctionExecution(GudPool pool) {

		Assert.notNull(pool, "Pool must not be null");

		this.pool = pool;
	}

	protected GudPool getPool() {
		return pool;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onServers(getPool());
	}
}
