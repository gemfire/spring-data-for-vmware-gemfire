/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;

import org.apache.geode.cache.DiskStore;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefinition;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;
import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;

/**
 * The {@link GemfireAdminOperations} interface defines a set of operations to define schema objects in a remote
 * Apache Geode or Pivotal GemFire cluster.
 *
 * @author John Blum
 * @see org.apache.geode.cache.DiskStore
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.query.Index
 * @see org.springframework.data.gemfire.config.schema.definitions.IndexDefinition
 * @see org.springframework.data.gemfire.config.schema.definitions.RegionDefinition
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectDefinition
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface GemfireAdminOperations {

	/**
	 * Returns a {@link Iterable collection} of {@link Region} names defined on the GemFire Servers in the cluster.
	 *
	 * @return an {@link Iterable} of {@link Region} names defined on the GemFire Servers in the cluster.
	 * @see org.apache.geode.cache.Region#getName()
	 * @see java.lang.Iterable
	 */
	Iterable<String> getAvailableServerRegions();

	/**
	 * Returns an {@link Iterable} of all the server {@link Region} {@link Index Indexes}.
	 *
	 * @return an {@link Iterable} of all the server {@link Region} {@link Index Indexes}.
	 * @see org.apache.geode.cache.query.Index#getName()
	 * @see java.lang.Iterable
	 */
	Iterable<String> getAvailableServerRegionIndexes();

	/**
	 * Creates a cache {@link Region} based on the given {@link RegionDefinition schema object definition}.
	 *
	 * @param regionDefinition {@link RegionDefinition} encapsulating configuration meta-data defining
	 * a cache {@link Region}.
	 * @see org.springframework.data.gemfire.config.schema.definitions.RegionDefinition
	 * @see org.apache.geode.cache.GemFireCache
	 * @see org.apache.geode.cache.Region
	 */
	void createRegion(RegionDefinition regionDefinition);

	default void createRegions(RegionDefinition... regionDefinitions) {
		stream(nullSafeArray(regionDefinitions, RegionDefinition.class)).forEach(this::createRegion);
	}

	default void createRegions(Iterable<RegionDefinition> regionDefinitions) {
		nullSafeIterable(regionDefinitions).forEach(this::createRegion);
	}

	/**
	 * Creates a {@link Region} OQL {@link Index} based on the given {@link IndexDefinition schema object definition}.
	 *
	 * @param indexDefinition {@link IndexDefinition} encapsulating the configuration meta-data
	 * defining a {@link Region} OQL {@link Index}.
	 * @see org.springframework.data.gemfire.config.schema.definitions.IndexDefinition
	 * @see org.apache.geode.cache.query.Index
	 * @see org.apache.geode.cache.Region
	 */
	void createIndex(IndexDefinition indexDefinition);

	default void createIndexes(IndexDefinition... indexDefinitions) {
		stream(nullSafeArray(indexDefinitions, IndexDefinition.class)).forEach(this::createIndex);
	}

	default void createIndexes(Iterable<IndexDefinition> indexDefinitions) {
		nullSafeIterable(indexDefinitions).forEach(this::createIndex);
	}

	/**
	 * Creates a {@link DiskStore} based on the given {@link SchemaObjectDefinition schema object definition}.
	 *
	 * @param diskStoreDefinition {@link SchemaObjectDefinition} encapsulating the configuration meta-data
	 * defining a {@link DiskStore}.
	 * @see org.springframework.data.gemfire.config.schema.SchemaObjectDefinition
	 * @see org.apache.geode.cache.DiskStore
	 */
	void createDiskStore(SchemaObjectDefinition diskStoreDefinition);

	default void createDiskStores(SchemaObjectDefinition... diskStoreDefinitions) {
		stream(nullSafeArray(diskStoreDefinitions, SchemaObjectDefinition.class)).forEach(this::createDiskStore);
	}

	default void createDiskStores(Iterable<SchemaObjectDefinition> diskStoreDefinitions) {
		nullSafeIterable(diskStoreDefinitions).forEach(this::createDiskStore);
	}
}
