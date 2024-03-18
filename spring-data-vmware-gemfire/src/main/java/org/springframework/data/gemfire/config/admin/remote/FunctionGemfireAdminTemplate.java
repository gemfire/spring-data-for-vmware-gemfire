/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin.remote;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.query.Index;
import org.apache.geode.management.internal.cli.domain.RegionInformation;
import org.apache.geode.management.internal.cli.functions.GetRegionsFunction;

import org.springframework.data.gemfire.client.function.ListRegionsOnServerFunction;
import org.springframework.data.gemfire.config.admin.AbstractGemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.GemfireAdminOperations;
import org.springframework.data.gemfire.config.admin.functions.CreateIndexFunction;
import org.springframework.data.gemfire.config.admin.functions.CreateRegionFunction;
import org.springframework.data.gemfire.config.admin.functions.ListIndexesFunction;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;
import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;
import org.springframework.data.gemfire.function.execution.GemfireFunctionOperations;
import org.springframework.data.gemfire.function.execution.GemfireOnServersFunctionTemplate;
import org.springframework.util.Assert;

/**
 * The {@link FunctionGemfireAdminTemplate} class is an implementation of the {@link GemfireAdminOperations} interface
 * supporting the Pivotal GemFire / Apache Geode administrative functions/operations via {@link Function} execution
 * in the cluster.
 *
 * Note: any schema changing functionality (e.g. {@link #createRegion(RegionDefinition)}) does not get recorded by
 * the GemFire/Geode Cluster Configuration Service using this strategy.
 *
 * @author John Blum
 * @see ClientCache
 * @see Function
 * @see ListRegionsOnServerFunction
 * @see AbstractGemfireAdminOperations
 * @see GemfireOnServersFunctionTemplate
 * @since 2.0.0
 */
public class FunctionGemfireAdminTemplate extends AbstractGemfireAdminOperations {

	private final ClientCache clientCache;

	/**
	 * Constructs a new instance of the {@link FunctionGemfireAdminTemplate} initialized with
	 * a {@link ClientCache} instance.
	 *
	 * @param clientCache reference to a {@link ClientCache} instance.
	 * @throws IllegalArgumentException if {@link ClientCache} is {@literal null}.
	 * @see ClientCache
	 */
	public FunctionGemfireAdminTemplate(ClientCache clientCache) {

		Assert.notNull(clientCache, "ClientCache is required");

		this.clientCache = clientCache;
	}

	/**
	 * Returns a reference to the configured {@link ClientCache} instance.
	 *
	 * @return a reference to the configured {@link ClientCache} instance.
	 * @see ClientCache
	 */
	protected ClientCache getClientCache() {
		return this.clientCache;
	}

	/**
	 * Lists all available {@link Region Regions} configured for all servers in the remote Pivotal GemFire
	 * / Apache Geode cluster.
	 *
	 * @return an {@link Iterable} of servers-side {@link Region} names for all {@link Region Regions} defined
	 * across all servers in the remote GemFire/Geode cluster.
	 * @see Iterable
	 */
	@Override
	public Iterable<String> getAvailableServerRegions() {

		try {
			return execute(new ListRegionsOnServerFunction());
		}
		catch (Exception cause) {
			try {
				return Optional.ofNullable(execute(new GetRegionsFunction(), false))
					.filter(this::containsRegionInformation)
					.map(regionInformationArray ->
						stream(nullSafeArray((Object[]) regionInformationArray, Object.class))
							.map(regionInformation -> ((RegionInformation) regionInformation).getName())
							.collect(Collectors.toSet())
					)
					.orElse(Collections.emptySet());
			}
			catch (Exception ignore) {
				return Collections.emptySet();
			}
		}
	}

	/**
	 * Returns an {@link Iterable} of all the server {@link Region} {@link Index Indexes}.
	 *
	 * @return an {@link Iterable} of all the server {@link Region} {@link Index Indexes}.
	 * @see Index#getName()
	 * @see Iterable
	 */
	@Override
	public Iterable<String> getAvailableServerRegionIndexes() {
		return execute(ListIndexesFunction.LIST_INDEXES_FUNCTION_ID);
	}

	@Override
	public void createRegion(RegionDefinition regionDefinition) {
		execute(CreateRegionFunction.CREATE_REGION_FUNCTION_ID, regionDefinition);
	}

	@Override
	public void createIndex(IndexDefinition indexDefinition) {
		execute(CreateIndexFunction.CREATE_INDEX_FUNCTION_ID, indexDefinition);
	}

	<T> T execute(Function gemfireFunction, Object... arguments) {
		return newGemfireFunctionOperations().executeAndExtract(gemfireFunction, arguments);
	}

	<T> T execute(String gemfireFunctionId, Object... arguments) {
		return newGemfireFunctionOperations().executeAndExtract(gemfireFunctionId, arguments);
	}

	protected GemfireFunctionOperations newGemfireFunctionOperations() {
		return newGemfireFunctionOperations(getClientCache());
	}

	protected GemfireFunctionOperations newGemfireFunctionOperations(ClientCache clientCache) {
		return new GemfireOnServersFunctionTemplate(clientCache);
	}

	boolean containsRegionInformation(Object results) {

		return Optional.ofNullable(results)
			.filter(it -> it instanceof Object[])
			.filter(it -> ((Object[]) it).length > 0)
			.filter(it -> ((Object[]) it)[0] instanceof RegionInformation)
			.isPresent();
	}
}
