// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.RegionService;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.util.Assert;

/**
 * Constructs an {@link Execution} using {@link FunctionService#onServers(RegionService)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see RegionService
 * @see Execution
 * @see FunctionService
 * @see AbstractFunctionExecution
 */
class OnServersUsingRegionServiceFunctionExecution extends AbstractFunctionExecution {

	private final RegionService regionService;

	OnServersUsingRegionServiceFunctionExecution(RegionService regionService) {

		Assert.notNull(regionService, "RegionService must not be null");

		this.regionService = regionService;
	}

	protected RegionService getRegionService() {
		return regionService;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onServers(getRegionService());
	}
}
