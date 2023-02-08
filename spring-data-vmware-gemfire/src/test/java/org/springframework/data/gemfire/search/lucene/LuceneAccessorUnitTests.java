/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.search.lucene;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.gemfire.search.lucene.LuceneAccessor.LuceneQueryExecutor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQueryException;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneService;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.data.gemfire.search.lucene.support.LuceneAccessorSupport;

/**
 * Unit Tests for {@link LuceneAccessor}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see MockitoJUnitRunner
 * @see LuceneAccessor
 * @since 1.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class LuceneAccessorUnitTests {

	@Mock
	private GemFireCache mockCache;

	// SUT
	private LuceneAccessor luceneAccessor;

	@Mock
	private LuceneIndex mockLuceneIndex;

	@Mock
	private LuceneQueryFactory mockLuceneQueryFactory;

	@Mock
	private LuceneService mockLuceneService;

	@Mock
	private Region<?, ?> mockRegion;

	@Before
	public void setup() {
		luceneAccessor = spy(new LuceneAccessorSupport() { });
	}

	@Test
	public void afterPropertiesSetInitializesLuceneAccessorProperly() throws Exception {
		assertThat(luceneAccessor.getCache()).isNull();
		assertThat(luceneAccessor.getLuceneService()).isNull();
		assertThat(luceneAccessor.getIndexName()).isNullOrEmpty();
		assertThat(luceneAccessor.getRegion()).isNull();
		assertThat(luceneAccessor.getRegionPath()).isNullOrEmpty();

		doReturn(mockCache).when(luceneAccessor).resolveCache();
		doReturn(mockLuceneService).when(luceneAccessor).resolveLuceneService();
		doReturn("TestIndex").when(luceneAccessor).resolveIndexName();
		doReturn("/Example").when(luceneAccessor).resolveRegionPath();

		luceneAccessor.afterPropertiesSet();

		assertThat(luceneAccessor.getCache()).isEqualTo(mockCache);
		assertThat(luceneAccessor.getLuceneService()).isEqualTo(mockLuceneService);
		assertThat(luceneAccessor.getIndexName()).isEqualTo("TestIndex");
		assertThat(luceneAccessor.getRegion()).isNull();
		assertThat(luceneAccessor.getRegionPath()).isEqualTo("/Example");

		verify(luceneAccessor, times(1)).resolveCache();
		verify(luceneAccessor, times(1)).resolveLuceneService();
		verify(luceneAccessor, times(1)).resolveIndexName();
		verify(luceneAccessor, times(1)).resolveRegionPath();
	}

	@Test
	public void createLuceneQueryFactory() {
		doReturn(mockLuceneService).when(luceneAccessor).resolveLuceneService();
		when(mockLuceneService.createLuceneQueryFactory()).thenReturn(mockLuceneQueryFactory);

		assertThat(luceneAccessor.createLuceneQueryFactory()).isEqualTo(mockLuceneQueryFactory);

		verify(luceneAccessor).resolveLuceneService();
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
	}

	@Test
	public void createLuceneQueryFactoryWithResultLimit() {

		doReturn(mockLuceneService).when(luceneAccessor).resolveLuceneService();
		when(mockLuceneService.createLuceneQueryFactory()).thenReturn(mockLuceneQueryFactory);
		when(mockLuceneQueryFactory.setPageSize(anyInt())).thenReturn(mockLuceneQueryFactory);
		when(mockLuceneQueryFactory.setLimit(anyInt())).thenReturn(mockLuceneQueryFactory);

		assertThat(luceneAccessor.createLuceneQueryFactory(1000)).isEqualTo(mockLuceneQueryFactory);

		verify(luceneAccessor).resolveLuceneService();
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(1000));
		verify(mockLuceneQueryFactory, times(1))
			.setPageSize(eq(LuceneAccessor.DEFAULT_PAGE_SIZE));
	}

	@Test
	public void createLuceneQueryFactoryWithResultLimitAndPageSize() {

		doReturn(mockLuceneService).when(luceneAccessor).resolveLuceneService();
		when(mockLuceneService.createLuceneQueryFactory()).thenReturn(mockLuceneQueryFactory);
		when(mockLuceneQueryFactory.setPageSize(anyInt())).thenReturn(mockLuceneQueryFactory);
		when(mockLuceneQueryFactory.setLimit(anyInt())).thenReturn(mockLuceneQueryFactory);

		assertThat(luceneAccessor.createLuceneQueryFactory(1000, 20))
			.isEqualTo(mockLuceneQueryFactory);

		verify(luceneAccessor).resolveLuceneService();
		verify(mockLuceneService, times(1)).createLuceneQueryFactory();
		verify(mockLuceneQueryFactory, times(1)).setLimit(eq(1000));
		verify(mockLuceneQueryFactory, times(1)).setPageSize(eq(20));
	}

	@Test
	public void resolveCacheReturnsConfiguredCache() {

		luceneAccessor.setCache(mockCache);

		assertThat(luceneAccessor.getCache()).isSameAs(mockCache);
		assertThat(luceneAccessor.resolveCache()).isSameAs(mockCache);
	}

	@Test
	public void resolveLuceneServiceReturnsConfiguredLuceneService() {

		luceneAccessor.setLuceneService(mockLuceneService);

		assertThat(luceneAccessor.getLuceneService()).isSameAs(mockLuceneService);
		assertThat(luceneAccessor.resolveLuceneService()).isSameAs(mockLuceneService);
	}

	@Test
	public void resolveLuceneServiceLooksUpLuceneService() {

		doReturn(mockCache).when(luceneAccessor).resolveCache();
		doReturn(mockLuceneService).when(luceneAccessor).resolveLuceneService(eq(mockCache));

		assertThat(luceneAccessor.getLuceneService()).isNull();
		assertThat(luceneAccessor.resolveLuceneService()).isSameAs(mockLuceneService);

		verify(luceneAccessor, times(1)).resolveCache();
		verify(luceneAccessor, times(1)).resolveLuceneService(eq(mockCache));
	}

	@Test(expected = IllegalArgumentException.class)
	public void resolveLuceneServiceThrowsIllegalArgumentExceptionWhenCacheIsNull() {

		try {
			luceneAccessor.resolveLuceneService(null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Cache reference was not properly configured");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void resolveIndexNameReturnsConfiguredIndexName() {

		luceneAccessor.setIndexName("TestIndex");

		assertThat(luceneAccessor.getIndexName()).isEqualTo("TestIndex");
		assertThat(luceneAccessor.resolveIndexName()).isEqualTo("TestIndex");

		verify(luceneAccessor, never()).getLuceneIndex();
	}

	@Test
	public void resolveIndexNameReturnsLuceneIndexName() {

		luceneAccessor.setLuceneIndex(mockLuceneIndex);

		when(mockLuceneIndex.getName()).thenReturn("MockIndex");

		assertThat(luceneAccessor.getIndexName()).isNullOrEmpty();
		assertThat(luceneAccessor.resolveIndexName()).isEqualTo("MockIndex");

		verify(luceneAccessor, times(1)).getLuceneIndex();
		verify(mockLuceneIndex, times(1)).getName();
	}

	@Test(expected = IllegalStateException.class)
	public void resolveIndexNameThrowsIllegalStateExceptionWhenIndexNameIsUnresolvable() {

		assertThat(luceneAccessor.getIndexName()).isNullOrEmpty();
		assertThat(luceneAccessor.getLuceneIndex()).isNull();

		try {
			luceneAccessor.resolveIndexName();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("The name of the Lucene Index could not be resolved");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	public void resolveRegionPathReturnsConfiguredRegionPath() {

		luceneAccessor.setRegionPath("/Example");

		assertThat(luceneAccessor.getRegionPath()).isEqualTo("/Example");
		assertThat(luceneAccessor.resolveRegionPath()).isEqualTo("/Example");
	}

	@Test
	public void resolveRegionPathReturnsRegionFullPath() {

		when(mockRegion.getFullPath()).thenReturn("/Example");

		luceneAccessor.setRegion(mockRegion);

		assertThat(luceneAccessor.resolveRegionPath()).isEqualTo("/Example");

		verify(luceneAccessor, times(1)).getRegionPath();
		verify(luceneAccessor, times(1)).getRegion();
		verify(mockRegion, times(1)).getFullPath();
	}

	@Test(expected = IllegalStateException.class)
	public void resolveRegionPathThrowsIllegalStatueExceptionWhenRegionPathIsUnresolvable() {

		assertThat(luceneAccessor.getRegion()).isNull();
		assertThat(luceneAccessor.getRegionPath()).isNullOrEmpty();

		try {
			luceneAccessor.resolveRegionPath();
		}
		catch (IllegalStateException expected) {

			assertThat(expected).hasMessage("Region path could not be resolved");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}

	@Test
	@SuppressWarnings("unchecked")
	public void doFind() throws LuceneQueryException {

		LuceneQueryExecutor<String> mockQueryExecutor = mock(LuceneQueryExecutor.class);

		when(mockQueryExecutor.execute()).thenReturn("test");

		assertThat(luceneAccessor.doFind(mockQueryExecutor, "title : 'Up Shit Creek Without a Paddle",
			"/Example", "ExampleIndex")).isEqualTo("test");

		verify(mockQueryExecutor, times(1)).execute();
	}

	@Test(expected = DataRetrievalFailureException.class)
	@SuppressWarnings("unchecked")
	public void doFindHandlesLuceneQueryException() throws LuceneQueryException {

		LuceneQueryExecutor<String> mockQueryExecutor = mock(LuceneQueryExecutor.class);

		when(mockQueryExecutor.execute()).thenThrow(new LuceneQueryException("test"));

		try {
			luceneAccessor.doFind(mockQueryExecutor, "title : Up Shit Creek Without a Paddle",
				"/Example", "ExampleIndex");
		}
		catch (DataRetrievalFailureException expected) {

			assertThat(expected)
				.hasMessageContaining("Failed to execute Lucene Query [title : Up Shit Creek Without a Paddle] on Region [/Example] with Lucene Index [ExampleIndex]");

			assertThat(expected).hasCauseInstanceOf(LuceneQueryException.class);

			throw expected;
		}
		finally {
			verify(mockQueryExecutor, times(1)).execute();
		}
	}

	@Test
	public void luceneAccessorInitializedCorrectly() {

		luceneAccessor.setCache(mockCache);
		luceneAccessor.setIndexName("ExampleIndex");
		luceneAccessor.setLuceneIndex(mockLuceneIndex);
		luceneAccessor.setLuceneService(mockLuceneService);
		luceneAccessor.setRegion(mockRegion);
		luceneAccessor.setRegionPath("/Example");

		assertThat(luceneAccessor.getCache()).isSameAs(mockCache);
		assertThat(luceneAccessor.getIndexName()).isEqualTo("ExampleIndex");
		assertThat(luceneAccessor.getLuceneIndex()).isSameAs(mockLuceneIndex);
		assertThat(luceneAccessor.getLuceneService()).isSameAs(mockLuceneService);
		assertThat(luceneAccessor.getRegion()).isSameAs(mockRegion);
		assertThat(luceneAccessor.getRegionPath()).isEqualTo("/Example");
	}
}
