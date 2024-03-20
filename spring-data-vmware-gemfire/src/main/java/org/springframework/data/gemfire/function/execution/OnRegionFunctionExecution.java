/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.util.Assert;

/**
 * Creates an {@literal OnRegion} {@link Function} {@link Execution} initialized with a {@link Region}
 * using {@link FunctionService#onRegion(Region)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Region
 * @see Execution
 * @see Function
 * @see FunctionService
 * @see AbstractFunctionExecution
 */
class OnRegionFunctionExecution extends AbstractFunctionExecution {

	private final Region<?, ?> region;

	private volatile Set<?> keys;

	public OnRegionFunctionExecution(Region<?, ?> region) {

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

	protected Region<?, ?> getRegion() {
		return this.region;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Execution getExecution() {

		Execution execution = FunctionService.onRegion(getRegion());

		Set<?> keys = getKeys();

		execution = CollectionUtils.isEmpty(keys) ? execution : execution.withFilter(keys);

		return execution;
	}
}
