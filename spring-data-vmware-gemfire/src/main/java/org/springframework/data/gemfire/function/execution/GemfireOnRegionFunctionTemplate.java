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
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.util.Assert;

/**
 * An {@link AbstractFunctionTemplate} implementation for {@link GudExecution executing} a {@link GudFunction}
 * on a target {@link GudRegion}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudRegion
 * @see GudExecution
 * @see GudFunction
 * @see AbstractFunctionTemplate
 * @see GemfireOnRegionOperations
 */
public class GemfireOnRegionFunctionTemplate extends AbstractFunctionTemplate implements GemfireOnRegionOperations {

	private final GudRegion<?, ?> region;

	/**
	 * Constructs a new instance of the {@link GemfireOnRegionFunctionTemplate} initialized with
	 * the given {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} on which the {@link GudFunction} will be executed.
	 * @throws IllegalArgumentException if {@link GudRegion} is {@literal null}.
	 * @see GudRegion
	 */
	public GemfireOnRegionFunctionTemplate(GudRegion<?, ?> region) {

		Assert.notNull(region, "Region must not be null");

		this.region = region;
	}

	@Override
	protected OnRegionFunctionExecution getFunctionExecution() {
		return new OnRegionFunctionExecution(getRegion());
	}

	protected GudRegion<?, ?> getRegion() {
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
