/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */
package org.springframework.data.gemfire.tests.mock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudIndexStatistics;
import org.springframework.data.gemfire.gud.api.GudIndexType;

/**
 * The {@link IndexMockObjects} class is a mock objects class allowing users to manually mock Apache Geode
 * or VMware GemFire {@link GudIndex} objects and related objects in the {@literal org.apache.geode.cache.query} package.
 *
 * @author John Blum
 * @see GudIndex
 * @see GudIndexStatistics
 * @see org.mockito.Mockito
 * @see MockObjectsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class IndexMockObjects extends MockObjectsSupport {

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static GudIndex mockGudIndex(String name, String fromClause, String indexedExpression, String projectionAttributes,
			GudRegion region, GudIndexStatistics statistics, GudIndexType type) {

		GudIndex mockGudIndex = mock(GudIndex.class, withSettings().name(name).lenient());

		when(mockGudIndex.getName()).thenReturn(name);
		when(mockGudIndex.getCanonicalizedFromClause()).thenReturn(fromClause);
		when(mockGudIndex.getCanonicalizedIndexedExpression()).thenReturn(indexedExpression);
		when(mockGudIndex.getCanonicalizedProjectionAttributes()).thenReturn(projectionAttributes);
		when(mockGudIndex.getFromClause()).thenReturn(fromClause);
		when(mockGudIndex.getIndexedExpression()).thenReturn(indexedExpression);
		when(mockGudIndex.getProjectionAttributes()).thenReturn(projectionAttributes);
		when(mockGudIndex.getRegion()).thenReturn(region);
		when(mockGudIndex.getStatistics()).thenReturn(statistics);
		when(mockGudIndex.getType()).thenReturn(type);

		return mockGudIndex;
	}

	public static GudIndexStatistics mockGudIndexStatistics(long numberOfBucketIndexes, long numberOfKeys,
			long numberOfMapIndexKeys, long numberOfValues, long numberOfUpdates, long readLockCount,
			long totalUpdateTime, long totalUses) {

		GudIndexStatistics mockGudIndexStatistics = mock(GudIndexStatistics.class, withSettings().lenient());

		when(mockGudIndexStatistics.getNumberOfBucketIndexes()).thenReturn(numberOfBucketIndexes);
		when(mockGudIndexStatistics.getNumberOfKeys()).thenReturn(numberOfKeys);
		when(mockGudIndexStatistics.getNumberOfMapIndexKeys()).thenReturn(numberOfMapIndexKeys);
		when(mockGudIndexStatistics.getNumberOfValues()).thenReturn(numberOfValues);
		when(mockGudIndexStatistics.getNumUpdates()).thenReturn(numberOfUpdates);
		when(mockGudIndexStatistics.getReadLockCount()).thenReturn(readLockCount);
		when(mockGudIndexStatistics.getTotalUpdateTime()).thenReturn(totalUpdateTime);
		when(mockGudIndexStatistics.getTotalUses()).thenReturn(totalUses);

		return mockGudIndexStatistics;
	}
}
