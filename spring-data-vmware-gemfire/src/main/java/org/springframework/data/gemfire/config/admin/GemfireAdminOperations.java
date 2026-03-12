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

package org.springframework.data.gemfire.config.admin;

import static java.util.Arrays.stream;
import static org.springframework.data.gemfire.util.ArrayUtils.nullSafeArray;
import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeIterable;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefinition;
import org.springframework.data.gemfire.gud.api.GudDiskStore;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link GemfireAdminOperations} interface defines a set of operations to define schema objects in a remote
 * Apache Geode or Pivotal GemFire cluster.
 *
 * @author John Blum
 * @see GudDiskStore
 * @see GudRegion
 * @see GudIndex
 * @see SchemaObjectDefinition
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public interface GemfireAdminOperations {

	/**
	 * Returns a {@link Iterable collection} of {@link GudRegion} names defined on the GemFire Servers in the cluster.
	 *
	 * @return an {@link Iterable} of {@link GudRegion} names defined on the GemFire Servers in the cluster.
	 * @see GudRegion#getName()
	 * @see Iterable
	 */
	Iterable<String> getAvailableServerRegions();

	/**
	 * Returns an {@link Iterable} of all the server {@link GudRegion} {@link GudIndex Indexes}.
	 *
	 * @return an {@link Iterable} of all the server {@link GudRegion} {@link GudIndex Indexes}.
	 * @see GudIndex#getName()
	 * @see Iterable
	 */
	Iterable<String> getAvailableServerRegionIndexes();
}
