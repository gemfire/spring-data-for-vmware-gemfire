/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.search.lucene;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.lucene.LuceneQueryProvider;

/**
 * Unit Tests for the {@link LuceneOperations} interface.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see LuceneOperations
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LuceneOperationsUnitTests {

	@Mock
	private LuceneQueryProvider mockLuceneQueryProvider;

	@Mock
	private TestLuceneOperations mockLuceneOperations;

	@Test
	public void stringQueryCallsQueryWithResultLimit() {

		when(mockLuceneOperations.query(anyString(), anyString())).thenCallRealMethod();

		mockLuceneOperations.query("title : Up Shit Creek Without a Paddle", "title");

		verify(mockLuceneOperations, times(1)).query(
			eq("title : Up Shit Creek Without a Paddle"), eq("title"),
				eq(LuceneOperations.DEFAULT_RESULT_LIMIT));
	}

	@Test
	public void queryProviderQueryCallsQueryWithResultLimit() {

		when(mockLuceneOperations.query(any(LuceneQueryProvider.class))).thenCallRealMethod();

		mockLuceneOperations.query(mockLuceneQueryProvider);

		verify(mockLuceneOperations, times(1)).query(eq(mockLuceneQueryProvider),
			eq(LuceneOperations.DEFAULT_RESULT_LIMIT));
	}

	@Test
	public void stringQueryForKeysCallsQueryForKeysWithResultLimit() {

		when(mockLuceneOperations.queryForKeys(anyString(), anyString())).thenCallRealMethod();

		mockLuceneOperations.queryForKeys("title : Up Shit Creek Without a Paddle", "title");

		verify(mockLuceneOperations, times(1)).queryForKeys(
			eq("title : Up Shit Creek Without a Paddle"), eq("title"),
				eq(LuceneOperations.DEFAULT_RESULT_LIMIT));
	}

	@Test
	public void queryProviderQueryForKeysCallsQueryForKeysWithResultLimit() {

		when(mockLuceneOperations.queryForKeys(any(LuceneQueryProvider.class))).thenCallRealMethod();

		mockLuceneOperations.queryForKeys(mockLuceneQueryProvider);

		verify(mockLuceneOperations, times(1)).queryForKeys(
			eq(mockLuceneQueryProvider), eq(LuceneOperations.DEFAULT_RESULT_LIMIT));
	}

	@Test
	public void stringQueryForValuesCallsQueryForValuesWithResultLimit() {

		when(mockLuceneOperations.queryForValues(anyString(), anyString())).thenCallRealMethod();

		mockLuceneOperations.queryForValues("title : Up Shit Creek Without a Paddle", "title");

		verify(mockLuceneOperations, times(1)).queryForValues(
			eq("title : Up Shit Creek Without a Paddle"), eq("title"),
				eq(LuceneOperations.DEFAULT_RESULT_LIMIT));
	}

	@Test
	public void queryProviderQueryForValuesCallsQueryForValuesWithResultLimit() {

		when(mockLuceneOperations.queryForValues(any(LuceneQueryProvider.class))).thenCallRealMethod();

		mockLuceneOperations.queryForValues(mockLuceneQueryProvider);

		verify(mockLuceneOperations, times(1)).queryForValues(
			eq(mockLuceneQueryProvider), eq(LuceneOperations.DEFAULT_RESULT_LIMIT));
	}

	static abstract class TestLuceneOperations implements LuceneOperations { }

}
