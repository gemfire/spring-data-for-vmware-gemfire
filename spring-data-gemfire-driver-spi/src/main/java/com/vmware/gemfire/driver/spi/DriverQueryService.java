/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Queries and indexes for one routing scope. A local service runs against local data and supports indexes;
 * a pool-backed service sends queries to servers and rejects index operations.
 */
public interface DriverQueryService {

	DriverQuery newQuery(String queryString);

	/** Creates an index now. {@code imports} may be null; primary-key indexes accept no imports. */
	DriverIndex createIndex(String name, DriverIndexType type, String indexedExpression, String fromClause, String imports);

	/** Records an index for {@link #createDefinedIndexes()}; nothing is built yet. */
	void defineIndex(String name, DriverIndexType type, String indexedExpression, String fromClause, String imports);

	/** Builds all defined indexes; on failure throws {@link DriverMultiIndexCreationException} with each failure. */
	java.util.List<DriverIndex> createDefinedIndexes();

	/** Discards the defined indexes; true when any were defined. */
	boolean clearDefinedIndexes();

	/** The named index of the region, or null when absent. */
	DriverIndex getIndex(String regionPath, String indexName);

	java.util.Collection<DriverIndex> getIndexes();

	java.util.Collection<DriverIndex> getIndexes(String regionPath);

	java.util.Collection<DriverIndex> getIndexes(String regionPath, DriverIndexType type);

	void removeIndex(DriverIndex index);

	void removeIndexes();

	void removeIndexes(String regionPath);
}
