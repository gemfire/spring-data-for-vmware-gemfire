/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
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
 * Unit Tests for {@link ProjectingLuceneOperations}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.search.lucene.ProjectingLuceneOperations
 * @since 2.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectingLuceneOperationsUnitTests {

	@Mock
	private LuceneQueryProvider mockQueryProvider;

	@Mock
	private TestProjectingLuceneOperations projectingLuceneOperations;

	@Test
	@SuppressWarnings("unchecked")
	public void stringQueryCallsQueryWithResultLimit() {

		when(projectingLuceneOperations.query(anyString(), anyString(), any(Class.class))).thenCallRealMethod();

		projectingLuceneOperations.query("title : Up Shit Creek Without A Paddle",
			"title", Book.class);

		verify(projectingLuceneOperations, times(1))
			.query(eq("title : Up Shit Creek Without A Paddle"), eq("title"),
				eq(ProjectingLuceneOperations.DEFAULT_RESULT_LIMIT), eq(Book.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void queryProviderQueryCallsQueryWithResultLimit() {

		when(projectingLuceneOperations.query(any(LuceneQueryProvider.class), any(Class.class)))
			.thenCallRealMethod();

		projectingLuceneOperations.query(mockQueryProvider, Book.class);

		verify(projectingLuceneOperations, times(1))
			.query(eq(mockQueryProvider), eq(ProjectingLuceneOperations.DEFAULT_RESULT_LIMIT), eq(Book.class));
	}

	static class Book {}

	static abstract class TestProjectingLuceneOperations implements ProjectingLuceneOperations { }

}
