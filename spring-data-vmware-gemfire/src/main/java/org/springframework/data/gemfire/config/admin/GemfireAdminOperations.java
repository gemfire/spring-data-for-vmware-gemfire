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

/**
 * The {@link GemfireAdminOperations} interface defines a set of operations to define schema objects in a remote
 * Apache Geode or Pivotal GemFire cluster.
 *
 * @author John Blum
 * @see DiskStore
 * @see Region
 * @see Index
 * @see SchemaObjectDefinition
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface GemfireAdminOperations {

	/**
	 * Returns a {@link Iterable collection} of {@link Region} names defined on the GemFire Servers in the cluster.
	 *
	 * @return an {@link Iterable} of {@link Region} names defined on the GemFire Servers in the cluster.
	 * @see Region#getName()
	 * @see Iterable
	 */
	Iterable<String> getAvailableServerRegions();

	/**
	 * Returns an {@link Iterable} of all the server {@link Region} {@link Index Indexes}.
	 *
	 * @return an {@link Iterable} of all the server {@link Region} {@link Index Indexes}.
	 * @see Index#getName()
	 * @see Iterable
	 */
	Iterable<String> getAvailableServerRegionIndexes();
}
