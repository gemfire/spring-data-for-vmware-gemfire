/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.tests.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexStatistics;
import org.apache.geode.cache.query.IndexType;

/**
 * The {@link IndexMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire {@link Index} objects and related objects in the {@literal org.apache.geode.cache.query} package.
 *
 * @author John Blum
 * @see Index
 * @see IndexStatistics
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class IndexMockObjects extends MockObjectsSupport {

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static Index mockIndex(String name, String fromClause, String indexedExpression, String projectionAttributes,
			Region region, IndexStatistics statistics, IndexType type) {

		Index mockIndex = mock(Index.class, withSettings().name(name).lenient());

		when(mockIndex.getName()).thenReturn(name);
		when(mockIndex.getCanonicalizedFromClause()).thenReturn(fromClause);
		when(mockIndex.getCanonicalizedIndexedExpression()).thenReturn(indexedExpression);
		when(mockIndex.getCanonicalizedProjectionAttributes()).thenReturn(projectionAttributes);
		when(mockIndex.getFromClause()).thenReturn(fromClause);
		when(mockIndex.getIndexedExpression()).thenReturn(indexedExpression);
		when(mockIndex.getProjectionAttributes()).thenReturn(projectionAttributes);
		when(mockIndex.getRegion()).thenReturn(region);
		when(mockIndex.getStatistics()).thenReturn(statistics);
		when(mockIndex.getType()).thenReturn(type);

		return mockIndex;
	}

	public static IndexStatistics mockIndexStatistics(int numberOfBucketIndexes, long numberOfKeys,
			long numberOfMapIndexKeys, long numberOfValues, long numberOfUpdates, int readLockCount,
			long totalUpdateTime, long totalUses) {

		IndexStatistics mockIndexStatistics = mock(IndexStatistics.class, withSettings().lenient());

		when(mockIndexStatistics.getNumberOfBucketIndexes()).thenReturn(numberOfBucketIndexes);
		when(mockIndexStatistics.getNumberOfKeys()).thenReturn(numberOfKeys);
		when(mockIndexStatistics.getNumberOfMapIndexKeys()).thenReturn(numberOfMapIndexKeys);
		when(mockIndexStatistics.getNumberOfValues()).thenReturn(numberOfValues);
		when(mockIndexStatistics.getNumUpdates()).thenReturn(numberOfUpdates);
		when(mockIndexStatistics.getReadLockCount()).thenReturn(readLockCount);
		when(mockIndexStatistics.getTotalUpdateTime()).thenReturn(totalUpdateTime);
		when(mockIndexStatistics.getTotalUses()).thenReturn(totalUses);

		return mockIndexStatistics;
	}
}
