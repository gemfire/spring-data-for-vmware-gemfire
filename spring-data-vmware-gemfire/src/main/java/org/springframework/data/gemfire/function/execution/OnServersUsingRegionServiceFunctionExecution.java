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
import org.springframework.data.gemfire.gud.api.GudRegionService;
import org.springframework.util.Assert;

/**
 * Constructs an {@link GudExecution} using {@link GudFunctionService#onServers(GudRegionService)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudRegionService
 * @see GudExecution
 * @see GudFunctionService
 * @see AbstractFunctionExecution
 */
class OnServersUsingRegionServiceFunctionExecution extends AbstractFunctionExecution {

	private final GudRegionService regionService;

	OnServersUsingRegionServiceFunctionExecution(GudRegionService regionService) {

		Assert.notNull(regionService, "RegionService must not be null");

		this.regionService = regionService;
	}

	protected GudRegionService getRegionService() {
		return regionService;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onServers(getRegionService());
	}
}
