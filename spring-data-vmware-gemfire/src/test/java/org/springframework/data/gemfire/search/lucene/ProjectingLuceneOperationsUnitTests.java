/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.lucene.LuceneQueryProvider;

/**
 * Unit tests for {@link ProjectingLuceneOperations}.
 *
 * @author John Blum
 * @see Test
 * @see RunWith
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see ProjectingLuceneOperations
 * @since 1.1.0
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

	abstract class TestProjectingLuceneOperations implements ProjectingLuceneOperations {
	}
}
