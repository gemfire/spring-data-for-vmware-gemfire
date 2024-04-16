/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefinition;

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
}
