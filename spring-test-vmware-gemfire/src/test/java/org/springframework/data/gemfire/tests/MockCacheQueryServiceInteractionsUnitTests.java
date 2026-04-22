/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Iterator;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudCollectionType;
import org.springframework.data.gemfire.gud.api.GudObjectType;
import org.springframework.data.gemfire.gud.api.GudQuery;
import org.springframework.data.gemfire.gud.api.GudQueryService;
import org.springframework.data.gemfire.gud.api.GudSelectResults;

import org.springframework.data.gemfire.tests.mock.GemFireMockObjectsSupport;

/**
 * Unit Tests for GemFire/Geode {@literal Mock} {@link GudClientCache} {@link GudQueryService}
 * and {@link GudQuery OQL Queries}.
 *
 * @author John Blum
 * @see Test
 * @see GudClientCache
 * @see GudQueryService
 * @see GudQuery
 * @see GudSelectResults
 * @see GudCollectionType
 * @see GudObjectType
 * @since 0.0.19
 */
public class MockCacheQueryServiceInteractionsUnitTests {

	private void assertSelectResults(GudSelectResults<Object> selectResults) {

		assertThat(selectResults).isNotNull();
		assertThat(selectResults.asList()).isEmpty();
		assertThat(selectResults.asSet()).isEmpty();
		assertThat(selectResults.isModifiable()).isFalse();
		assertThat(selectResults.occurrences("MOCK")).isZero();
		assertThat(selectResults.occurrences("TEST")).isZero();

		GudCollectionType collectionType = selectResults.getCollectionType();

		assertThat(collectionType).isNotNull();
		assertThat(collectionType.allowsDuplicates()).isFalse();
		assertThat(collectionType.isOrdered()).isFalse();

		GudObjectType objectType = collectionType.getElementType();

		assertThat(objectType).isNotNull();
		assertThat(objectType.getSimpleClassName()).isEqualTo(Object.class.getSimpleName());
		assertThat(objectType.isCollectionType()).isFalse();
		assertThat(objectType.isMapType()).isFalse();
		assertThat(objectType.isStructType()).isFalse();
		assertThat(objectType.resolveClass()).isEqualTo(Object.class);

		Iterator<?> iterator = selectResults.iterator();

		assertThat(iterator).isNotNull();
		assertThat(iterator.hasNext()).isFalse();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void queryServiceInteractionsAndQueryExecutionIsCorrect() throws Exception {

		String queryString = "SELECT * FROM /Example WHERE id = $1";

		GudClientCache mockClientCache = GemFireMockObjectsSupport.mockGudClientCache();

		assertThat(mockClientCache).isNotNull();

		GudQueryService mockQueryService = mockClientCache.getQueryService();

		assertThat(mockQueryService).isNotNull();

		GudQuery mockQuery = mockQueryService.newQuery(queryString);

		assertThat(mockQuery).isNotNull();
		assertThat(mockQuery.getQueryString()).isEqualTo(queryString);
		assertThat(mockQuery.getStatistics()).isNotNull();

		Object results = mockQuery.execute();

		assertThat(results).isInstanceOf(GudSelectResults.class);

		GudSelectResults<Object> mockSelectResults = (GudSelectResults<Object>) results;

		assertSelectResults(mockSelectResults);

		results = mockQuery.execute(1);

		assertThat(results).isInstanceOf(GudSelectResults.class);

		mockSelectResults = (GudSelectResults<Object>) results;

		assertSelectResults(mockSelectResults);
	}
}
