/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.projection.ProjectionFactory;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Unit tests for {@link ProjectingLuceneTemplate}.
 *
 * @author John Blum
 * @see Test
 * @see RunWith
 * @see Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see MockitoJUnitRunner
 * @since 1.1.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectingLuceneTemplateUnitTests {

	@Mock
	private LuceneQueryProvider mockQueryProvider;

	// SUT
	private ProjectingLuceneTemplate luceneTemplate;

	@Mock
	private ProjectionFactory mockProjectionFactory;

	@Before
	public void setup() {
		luceneTemplate = spy(new ProjectingLuceneTemplate());
		luceneTemplate.setProjectionFactory(mockProjectionFactory);
	}

	protected LuceneResultStruct<Long, String> mockLuceneResultStruct(Book book) {
		return mockLuceneResultStruct(book.getIsbn(), book.getTitle());
	}

	@SuppressWarnings("unchecked")
	protected <K, V> LuceneResultStruct<K, V> mockLuceneResultStruct(K key, V value) {
		LuceneResultStruct<K, V> mockLuceneResultStruct =
			mock(LuceneResultStruct.class, String.format("MockLuceneResultStruct%s", key));

		when(mockLuceneResultStruct.getValue()).thenReturn(value);

		return mockLuceneResultStruct;
	}

	@Test
	public void queryWithString() {
		List<Book> books = asList(
			Book.newBook(4L, "Star Wars - Episode IV New Hope"),
			Book.newBook(5L, "Star Wars - Episode V Empire Strikes Back"),
			Book.newBook(6L, "Star Wars - Episode VI Return of the Jedi")
		);

		List<LuceneResultStruct<Long, String>> queryResults = asList(
			mockLuceneResultStruct(books.get(0)),
			mockLuceneResultStruct(books.get(1)),
			mockLuceneResultStruct(books.get(2))
		);

		doReturn(queryResults).when(luceneTemplate).query(anyString(), anyString(), anyInt());

		when(mockProjectionFactory.createProjection(eq(Book.class), anyString())).thenAnswer( invocationOnMock ->
			books.stream().filter(book ->
				book.getTitle().equals(invocationOnMock.getArgument(1))).findFirst().orElse(null)
		);

		assertThat(luceneTemplate.query("title : Star Wars Episode *V*", "title", Book.class))
			.containsAll(books);

		verify(luceneTemplate, times(1))
			.query(eq("title : Star Wars Episode *V*"), eq("title"),
				eq(ProjectingLuceneTemplate.DEFAULT_RESULT_LIMIT), eq(Book.class));
		verify(mockProjectionFactory, times(1))
			.createProjection(eq(Book.class), eq(books.get(0).getTitle()));
		verify(mockProjectionFactory, times(1))
			.createProjection(eq(Book.class), eq(books.get(1).getTitle()));
		verify(mockProjectionFactory, times(1))
			.createProjection(eq(Book.class), eq(books.get(2).getTitle()));
	}

	@Test
	public void queryWithQueryProvider() {
		List<Book> books = asList(
			Book.newBook(1L, "Star Wars - Episode I Phantom Menace"),
			Book.newBook(2L, "Star Wars - Episode II Attack of the Clones"),
			Book.newBook(3L, "Star Wars - Episode III Revenge of the Sith")
		);

		List<LuceneResultStruct<Long, String>> queryResults = asList(
			mockLuceneResultStruct(books.get(0)),
			mockLuceneResultStruct(books.get(1)),
			mockLuceneResultStruct(books.get(2))
		);

		doReturn(queryResults).when(luceneTemplate).query(any(LuceneQueryProvider.class), anyInt());

		when(mockProjectionFactory.createProjection(eq(Book.class), anyString())).thenAnswer( invocationOnMock ->
			books.stream().filter(book ->
				book.getTitle().equals(invocationOnMock.getArgument(1))).findFirst().orElse(null)
		);

		assertThat(luceneTemplate.query(mockQueryProvider, Book.class)).containsAll(books);

		verify(luceneTemplate, times(1))
			.query(eq(mockQueryProvider), eq(ProjectingLuceneTemplate.DEFAULT_RESULT_LIMIT), eq(Book.class));
		verify(mockProjectionFactory, times(1))
			.createProjection(eq(Book.class), eq(books.get(0).getTitle()));
		verify(mockProjectionFactory, times(1))
			.createProjection(eq(Book.class), eq(books.get(1).getTitle()));
		verify(mockProjectionFactory, times(1))
			.createProjection(eq(Book.class), eq(books.get(2).getTitle()));
	}

	@Data
	@Region("Books")
	@RequiredArgsConstructor(staticName = "newBook")
	static class Book {
		@NonNull @Id Long isbn;
		@NonNull String title;
	}
}
