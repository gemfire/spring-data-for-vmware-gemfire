/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin;

import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefinition;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;
import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;

/**
 * {@link AbstractGemfireAdminOperations} is an abstract base class encapsulating common functionality
 * supporting administrative (management) operations against a Pivotal GemFire or Apache Geode cluster.
 *
 * @author John Blum
 * @see GemfireAdminOperations
 * @see SchemaObjectDefinition
 * @since 2.0.0
 */
public class AbstractGemfireAdminOperations implements GemfireAdminOperations {

	protected static final String NOT_IMPLEMENTED = "Not Implemented";

	/**
	 * Returns a {@link Iterable collection} of {@link Region} names defined on the GemFire Servers in the cluster.
	 *
	 * @return an {@link Iterable} of {@link Region} names defined on the GemFire Servers in the cluster.
	 * @see Region#getName()
	 * @see Iterable
	 */
	@Override
	public Iterable<String> getAvailableServerRegions() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
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
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	/**
	 * Creates a cache {@link Region} based on the given {@link RegionDefinition schema object definition}.
	 *
	 * @param regionDefinition {@link RegionDefinition} encapsulating configuration meta-data defining
	 * a cache {@link Region}.
	 * @see RegionDefinition
	 * @see org.apache.geode.cache.GemFireCache
	 * @see Region
	 */
	@Override
	public void createRegion(RegionDefinition regionDefinition) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	/**
	 * Creates a {@link Region} OQL {@link Index} based on the given {@link IndexDefinition schema object definition}.
	 *
	 * @param indexDefinition {@link IndexDefinition} encapsulating the configuration meta-data
	 * defining a {@link Region} OQL {@link Index}.
	 * @see IndexDefinition
	 * @see Index
	 * @see Region
	 */
	@Override
	public void createIndex(IndexDefinition indexDefinition) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	/**
	 * Creates a {@link DiskStore} based on the given {@link SchemaObjectDefinition schema object definition}.
	 *
	 * @param diskStoreDefinition {@link SchemaObjectDefinition} encapsulating the configuration meta-data
	 * defining a {@link DiskStore}.
	 * @see SchemaObjectDefinition
	 * @see DiskStore
	 */
	@Override
	public void createDiskStore(SchemaObjectDefinition diskStoreDefinition) {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}
}
