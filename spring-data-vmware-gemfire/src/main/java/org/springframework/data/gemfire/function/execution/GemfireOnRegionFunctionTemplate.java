/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;

import org.springframework.util.Assert;

/**
 * An {@link AbstractFunctionTemplate} implementation for {@link Execution executing} a {@link Function}
 * on a target {@link Region}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.function.execution.AbstractFunctionTemplate
 * @see org.springframework.data.gemfire.function.execution.GemfireOnRegionOperations
 */
public class GemfireOnRegionFunctionTemplate extends AbstractFunctionTemplate implements GemfireOnRegionOperations {

	private final Region<?, ?> region;

	/**
	 * Constructs a new instance of the {@link GemfireOnRegionFunctionTemplate} initialized with
	 * the given {@link Region}.
	 *
	 * @param region {@link Region} on which the {@link Function} will be executed.
	 * @throws IllegalArgumentException if {@link Region} is {@literal null}.
	 * @see org.apache.geode.cache.Region
	 */
	public GemfireOnRegionFunctionTemplate(Region<?, ?> region) {

		Assert.notNull(region, "Region must not be null");

		this.region = region;
	}

	@Override
	protected OnRegionFunctionExecution getFunctionExecution() {
		return new OnRegionFunctionExecution(getRegion());
	}

	protected Region<?, ?> getRegion() {
		return this.region;
	}

	@Override
	public <T> Iterable<T> execute(String functionId, Set<?> keys, Object... args) {

		return execute(getFunctionExecution()
			.setKeys(keys)
			.setArguments(args)
			.setFunctionId(functionId)
			.setTimeout(getTimeout()));
	}

	@Override
	public <T> T executeAndExtract(String functionId, Set<?> keys, Object... args) {

		return executeAndExtract(getFunctionExecution()
			.setKeys(keys)
			.setFunctionId(functionId)
			.setTimeout(getTimeout()).setArguments(args));
	}

	@Override
	public void executeWithNoResult(String functionId, Set<?> keys, Object... args) {

		execute(getFunctionExecution()
			.setKeys(keys)
			.setArguments(args)
			.setFunctionId(functionId)
			.setTimeout(getTimeout()), false);
	}
}
