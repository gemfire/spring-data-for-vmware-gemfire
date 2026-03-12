/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.admin.remote;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.gemfire.config.admin.AbstractGemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.function.execution.GemfireFunctionOperations;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.util.Assert;

/**
 * The {@link FunctionGemfireAdminTemplate} class is an implementation of the {@link GemfireAdminOperations} interface
 * supporting the Pivotal GemFire / Apache Geode administrative functions/operations via {@link GudFunction} execution
 * in the cluster.
 *
 * Note: any schema changing functionality does not get recorded by
 * the GemFire/Geode Cluster Configuration Service using this strategy.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudFunction
 * @see AbstractGemfireAdminOperations
 * @since 2.0.0
 */
public abstract class FunctionGemfireAdminTemplate extends AbstractGemfireAdminOperations {

	private final GudClientCache clientCache;

	/**
	 * Constructs a new instance of the {@link FunctionGemfireAdminTemplate} initialized with
	 * a {@link GudClientCache} instance.
	 *
	 * @param clientCache reference to a {@link GudClientCache} instance.
	 * @throws IllegalArgumentException if {@link GudClientCache} is {@literal null}.
	 * @see GudClientCache
	 */
	public FunctionGemfireAdminTemplate(GudClientCache clientCache) {

		Assert.notNull(clientCache, "ClientCache is required");

		this.clientCache = clientCache;
	}

	/**
	 * Returns a reference to the configured {@link GudClientCache} instance.
	 *
	 * @return a reference to the configured {@link GudClientCache} instance.
	 * @see GudClientCache
	 */
	protected GudClientCache getClientCache() {
		return this.clientCache;
	}

	/**
	 * Lists all available {@link GudRegion Regions} configured for all servers in the remote Pivotal GemFire
	 * / Apache Geode cluster.
	 *
	 * @return an {@link Iterable} of servers-side {@link GudRegion} names for all {@link GudRegion Regions} defined
	 * across all servers in the remote GemFire/Geode cluster.
	 * @see Iterable
	 */
	@Override
	public abstract Iterable<String> getAvailableServerRegions();

	/**
	 * Executes a {@link GudFunction} with the given arguments.
	 *
	 * @param <T> the return type.
	 * @param gemfireFunction the {@link GudFunction} to execute.
	 * @param arguments the arguments for the function.
	 * @return the result of the function execution.
	 */
	protected abstract <T> T execute(GudFunction gemfireFunction, Object... arguments);

	/**
	 * Creates a new {@link GemfireFunctionOperations} for the configured {@link GudClientCache}.
	 *
	 * @return a new {@link GemfireFunctionOperations}.
	 */
	protected abstract GemfireFunctionOperations newGemfireFunctionOperations();

	/**
	 * Creates a new {@link GemfireFunctionOperations} for the given {@link GudClientCache}.
	 *
	 * @param clientCache the {@link GudClientCache} to use.
	 * @return a new {@link GemfireFunctionOperations}.
	 */
	protected abstract GemfireFunctionOperations newGemfireFunctionOperations(GudClientCache clientCache);
}
