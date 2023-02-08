/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.search.lucene;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.search.lucene.LuceneAccessor.LuceneQueryExecutor;

import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.LuceneService;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;

/**
 * Unit Tests for {@link LuceneTemplate}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see Mock
 * @see org.mockito.Mockito
 * @see Spy
 * @see MockitoJUnitRunner
 * @see LuceneQuery
 * @see LuceneQueryFactory
 * @see LuceneQueryProvider
 * @see LuceneResultStruct
 * @see LuceneService
 * @see PageableLuceneQueryResults
 * @see LuceneTemplate
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LuceneTemplateUnitTests {

	@Mock
	private LuceneQuery<Object, Object> mockLuceneQuery;

	@Mock
	private LuceneQueryFactory mockLuceneQueryFactory;

	@Mock
	private LuceneQueryProvider mockLuceneQueryProvider;

	@Mock
	private LuceneResultStruct<Object, Object> mockLuceneResultStructOne;

	@Mock
	private LuceneResultStruct<Object, Object> mockLuceneResultStructTwo;

	@Mock
	private LuceneService mockLuceneService;

	@Mock
	private PageableLuceneQueryResults<Object, Object> mockPageableLuceneQueryResults;

	@Spy
	private LuceneTemplate luceneTemplate;

	@Before
	public void setup() {

		luceneTemplate.setLuceneService(mockLuceneService);

		when(mockLuceneService.createLuceneQueryFactory()).thenReturn(mockLuceneQueryFactory);
		when(mockLuceneQueryFactory.setPageSize(anyInt())).thenReturn(mockLuceneQueryFactory);
		when(mockLuceneQueryFactory.setLimit(anyInt())).thenReturn(mockLuceneQueryFactory);
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void stringQueryReturnsList() throws LuceneQueryException {

		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"), anyString(), anyString()))
			.thenReturn(mockLuceneQuery);

		when(mockLuceneQuery.findResults()).thenReturn(asList(mockLuceneResultStructOne, mockLuceneResultStructTwo));

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		List<LuceneResultStruct<Object, Object>> results =  luceneTemplate.query(
			"title : Up Shit Creek Without a Paddle", "title", 100);

		assertThat(results).isNotNull();
		assertThat(results).hasSize(2);
		assertThat(results).containsAll(asList(mockLuceneResultStructOne, mockLuceneResultStructTwo));

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq("title : Up Shit Creek Without a Paddle"), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(LuceneOperations.DEFAULT_PAGE_SIZE));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq("title : Up Shit Creek Without a Paddle"), eq("title"));
		verify(mockLuceneQuery, times(1)).findResults();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void stringQueryWithPageSizeReturnsPageableResults() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"), anyString(), anyString()))
			.thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findPages()).thenReturn(mockPageableLuceneQueryResults);

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		PageableLuceneQueryResults<Object, Object> results =  luceneTemplate.query(
			"title : Up Shit Creek Without a Paddle", "title", 100, 20);

		assertThat(results).isSameAs(mockPageableLuceneQueryResults);

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq("title : Up Shit Creek Without a Paddle"), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(20));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq("title : Up Shit Creek Without a Paddle"), eq("title"));
		verify(mockLuceneQuery, times(1)).findPages();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void queryProviderQueryReturnsList() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"),
			any(LuceneQueryProvider.class))).thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findResults()).thenReturn(asList(mockLuceneResultStructOne, mockLuceneResultStructTwo));

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		List<LuceneResultStruct<Object, Object>> results =
			luceneTemplate.query(mockLuceneQueryProvider, 100);

		assertThat(results).isNotNull();
		assertThat(results).hasSize(2);
		assertThat(results).containsAll(asList(mockLuceneResultStructOne, mockLuceneResultStructTwo));

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq(mockLuceneQueryProvider), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(LuceneOperations.DEFAULT_PAGE_SIZE));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq(mockLuceneQueryProvider));
		verify(mockLuceneQuery, times(1)).findResults();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void queryProviderQueryWithPageSizeReturnsPageableResults() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"),
			any(LuceneQueryProvider.class))).thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findPages()).thenReturn(mockPageableLuceneQueryResults);

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		PageableLuceneQueryResults<Object, Object> results =  luceneTemplate.query(mockLuceneQueryProvider,
			100, 20);

		assertThat(results).isSameAs(mockPageableLuceneQueryResults);

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq(mockLuceneQueryProvider), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(20));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq(mockLuceneQueryProvider));
		verify(mockLuceneQuery, times(1)).findPages();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void stringQueryForKeysReturnsKeys() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"), anyString(), anyString()))
			.thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findKeys()).thenReturn(asList("keyOne", "keyTwo"));

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		Collection<String> keys =  luceneTemplate.queryForKeys(
			"title : Up Shit Creek Without a Paddle", "title", 100);

		assertThat(keys).isNotNull();
		assertThat(keys).hasSize(2);
		assertThat(keys).containsAll(asList("keyOne", "keyTwo"));

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq("title : Up Shit Creek Without a Paddle"), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(LuceneOperations.DEFAULT_PAGE_SIZE));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq("title : Up Shit Creek Without a Paddle"), eq("title"));
		verify(mockLuceneQuery, times(1)).findKeys();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void queryProviderQueryForKeysReturnsKeys() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"),
			any(LuceneQueryProvider.class))).thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findKeys()).thenReturn(asList("keyOne", "keyTwo"));

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		Collection<String> keys =  luceneTemplate.queryForKeys(mockLuceneQueryProvider, 100);

		assertThat(keys).isNotNull();
		assertThat(keys).hasSize(2);
		assertThat(keys).containsAll(asList("keyOne", "keyTwo"));

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq(mockLuceneQueryProvider), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(LuceneOperations.DEFAULT_PAGE_SIZE));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq(mockLuceneQueryProvider));
		verify(mockLuceneQuery, times(1)).findKeys();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void stringQueryForValuesReturnsValues() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"), anyString(), anyString()))
			.thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findValues()).thenReturn(asList("valueOne", "valueTwo"));

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		Collection<String> keys =  luceneTemplate.queryForValues(
			"title : Up Shit Creek Without a Paddle", "title", 100);

		assertThat(keys).isNotNull();
		assertThat(keys).hasSize(2);
		assertThat(keys).containsAll(asList("valueOne", "valueTwo"));

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq("title : Up Shit Creek Without a Paddle"), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(LuceneOperations.DEFAULT_PAGE_SIZE));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq("title : Up Shit Creek Without a Paddle"), eq("title"));
		verify(mockLuceneQuery, times(1)).findValues();
	}

	@Test
	@SuppressWarnings({ "unchecked" })
	public void queryProviderQueryForValuesReturnsValues() throws LuceneQueryException {
		when(mockLuceneQueryFactory.create(eq("TestIndex"), eq("/Example"),
			any(LuceneQueryProvider.class))).thenReturn(mockLuceneQuery);
		when(mockLuceneQuery.findValues()).thenReturn(asList("valueOne", "valueTwo"));

		doReturn("TestIndex").when(luceneTemplate).resolveIndexName();
		doReturn("/Example").when(luceneTemplate).resolveRegionPath();

		Collection<String> keys =  luceneTemplate.queryForValues(mockLuceneQueryProvider, 100);

		assertThat(keys).isNotNull();
		assertThat(keys).hasSize(2);
		assertThat(keys).containsAll(asList("valueOne", "valueTwo"));

		verify(luceneTemplate, times(1)).resolveIndexName();
		verify(luceneTemplate, times(1)).resolveRegionPath();
		verify(luceneTemplate, times(1)).doFind(isA(LuceneQueryExecutor.class),
			eq(mockLuceneQueryProvider), eq("/Example"), eq("TestIndex"));
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(100));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(LuceneOperations.DEFAULT_PAGE_SIZE));
		verify(mockLuceneQueryFactory, times(1)).create(eq("TestIndex"),
			eq("/Example"), eq(mockLuceneQueryProvider));
		verify(mockLuceneQuery, times(1)).findValues();
	}
}
