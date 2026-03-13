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

import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.util.Assert;

/**
 * Creates an {@literal OnRegion} {@link GudFunction} {@link GudExecution} initialized with a {@link GudRegion}
 * using {@link GudFunctionService#onRegion(GudRegion)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudRegion
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionService
 * @see AbstractFunctionExecution
 */
class OnRegionFunctionExecution extends AbstractFunctionExecution {

	private final GudRegion<?, ?> region;

	private volatile Set<?> keys;

	public OnRegionFunctionExecution(GudRegion<?, ?> region) {

		Assert.notNull(region, "Region must not be null");

		this.region = region;
	}

	public OnRegionFunctionExecution setKeys(Set<?> keys) {
		this.keys = keys;
		return this;
	}

	protected Set<?> getKeys() {
		return this.keys;
	}

	protected GudRegion<?, ?> getRegion() {
		return this.region;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected GudExecution getExecution() {

		GudExecution execution = GudFunctionService.onRegion(getRegion());

		Set<?> keys = getKeys();

		execution = CollectionUtils.isEmpty(keys) ? execution : execution.withFilter(keys);

		return execution;
	}
}
