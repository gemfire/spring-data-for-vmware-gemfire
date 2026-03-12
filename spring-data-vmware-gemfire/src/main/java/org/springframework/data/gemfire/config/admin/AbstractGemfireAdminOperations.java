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

import org.springframework.data.gemfire.config.schema.SchemaObjectDefinition;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudRegion;

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
	 * Returns a {@link Iterable collection} of {@link GudRegion} names defined on the GemFire Servers in the cluster.
	 *
	 * @return an {@link Iterable} of {@link GudRegion} names defined on the GemFire Servers in the cluster.
	 * @see GudRegion#getName()
	 * @see Iterable
	 */
	@Override
	public Iterable<String> getAvailableServerRegions() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	/**
	 * Returns an {@link Iterable} of all the server {@link GudRegion} {@link GudIndex Indexes}.
	 *
	 * @return an {@link Iterable} of all the server {@link GudRegion} {@link GudIndex Indexes}.
	 * @see GudIndex#getName()
	 * @see Iterable
	 */
	@Override
	public Iterable<String> getAvailableServerRegionIndexes() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}
}
