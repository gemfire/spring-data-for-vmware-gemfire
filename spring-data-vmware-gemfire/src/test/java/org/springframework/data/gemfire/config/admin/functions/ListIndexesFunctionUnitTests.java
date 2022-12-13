/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin.functions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.QueryService;

/**
 * Unit tests for {@link ListIndexesFunction}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see Cache
 * @see Index
 * @see QueryService
 * @see ListIndexesFunction
 * @since 2.0.0
 */
@RunWith(MockitoJUnitRunner.class)
public class ListIndexesFunctionUnitTests {

	@Mock
	private Cache mockCache;

	@Mock
	private Index mockIndexOne;

	@Mock
	private Index mockIndexTwo;

	private ListIndexesFunction listIndexesFunction;

	@Mock
	private QueryService mockQueryService;

	@Before
	public void setup() {

		this.listIndexesFunction = spy(new ListIndexesFunction());

		doReturn(this.mockCache).when(this.listIndexesFunction).resolveCache();
		when(this.mockCache.getQueryService()).thenReturn(this.mockQueryService);
		when(this.mockIndexOne.getName()).thenReturn("MockIndexOne");
		when(this.mockIndexTwo.getName()).thenReturn("MockIndexTwo");
	}

	@Test
	public void listIndexesReturnsIndexNames() {

		when(this.mockQueryService.getIndexes()).thenReturn(Arrays.asList(this.mockIndexOne, this.mockIndexTwo));

		assertThat(this.listIndexesFunction.listIndexes()).contains("MockIndexOne", "MockIndexTwo");

		verify(this.listIndexesFunction, times(1)).resolveCache();
		verify(this.mockCache, times(1)).getQueryService();
		verify(this.mockQueryService, times(1)).getIndexes();
		verify(this.mockIndexOne, times(1)).getName();
		verify(this.mockIndexTwo, times(1)).getName();
	}

	@Test
	public void listIndexesReturnsEmptySetWhenCacheIsNull() {

		doReturn(null).when(this.listIndexesFunction).resolveCache();

		assertThat(this.listIndexesFunction.listIndexes()).isEmpty();

		verify(this.listIndexesFunction, times(1)).resolveCache();
	}

	@Test
	public void listIndexesReturnsEmptySetWhenQueryServiceIsNull() {

		when(this.mockCache.getQueryService()).thenReturn(null);

		assertThat(this.listIndexesFunction.listIndexes()).isEmpty();

		verify(this.listIndexesFunction, times(1)).resolveCache();
		verify(this.mockCache, times(1)).getQueryService();
	}

	@Test
	public void listIndexesReturnsEmptySetWhenQueryServiceGetIndexesIsNull() {

		when(this.mockQueryService.getIndexes()).thenReturn(null);

		assertThat(this.listIndexesFunction.listIndexes()).isEmpty();

		verify(this.listIndexesFunction, times(1)).resolveCache();
		verify(this.mockCache, times(1)).getQueryService();
		verify(this.mockQueryService, times(1)).getIndexes();
	}
}
