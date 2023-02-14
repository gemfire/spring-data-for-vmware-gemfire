/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.cache.execute.ResultSender;

/**
 * Unit Tests for {@link ListRegionsOnServerFunction}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.client.function.ListRegionsOnServerFunction
 * @since 1.7.0
 */
public class ListRegionsOnServerFunctionUnitTests {

	private final ListRegionsOnServerFunction function = spy(new ListRegionsOnServerFunction());

	@Test
	@SuppressWarnings("unchecked")
	public void executeReturnsRootRegionNames() {

		Cache mockCache = mock(Cache.class, "MockGemFireCache");

		Region<?, ?> mockRegionOne = mock(Region.class, "MockGemFireRegionOne");
		Region<?, ?> mockRegionTwo = mock(Region.class, "MockGemFireRegionTwo");
		Region<?, ?> mockRegionThree = mock(Region.class, "MockGemFireRegionThree");

		FunctionContext<?> mockFunctionContext = mock(FunctionContext.class, "MockGemFireFunctionContext");

		ResultSender<Object> mockResultSender = mock(ResultSender.class, "MockGemFireResultSender");

		when(mockCache.rootRegions()).thenReturn(new HashSet<>(Arrays.asList(mockRegionOne, mockRegionTwo, mockRegionThree)));
		when(mockRegionOne.getName()).thenReturn("One");
		when(mockRegionTwo.getName()).thenReturn("Two");
		when(mockRegionThree.getName()).thenReturn("Three");
		when(mockFunctionContext.getResultSender()).thenReturn(mockResultSender);

		final AtomicReference<List<String>> regionNames = new AtomicReference<>(null);

		doAnswer(invocation -> {
			regionNames.compareAndSet(null, invocation.getArgument(0));
			return null;
		}).when(mockResultSender).lastResult(any(List.class));

		ListRegionsOnServerFunction function = spy(new ListRegionsOnServerFunction());

		doReturn(mockCache).when(function).getCache();

		function.execute(mockFunctionContext);

		List<String> actualRegionNames = regionNames.get();

		assertThat(actualRegionNames).isNotNull();
		assertThat(actualRegionNames.isEmpty()).isFalse();
		assertThat(actualRegionNames.size()).isEqualTo(3);
		assertThat(actualRegionNames.containsAll(Arrays.asList("One", "Two", "Three"))).isTrue();

		verify(mockCache, times(1)).rootRegions();
		verify(mockRegionOne, times(1)).getName();
		verify(mockRegionTwo, times(1)).getName();
		verify(mockRegionThree, times(1)).getName();
		verify(mockFunctionContext, times(1)).getResultSender();
		verify(mockResultSender, times(1)).lastResult(any(List.class));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void executeWithNoRegions() {

		Cache mockCache = mock(Cache.class, "MockGemFireCache");

		FunctionContext<?> mockFunctionContext = mock(FunctionContext.class, "MockGemFireFunctionContext");

		ResultSender<Object> mockResultSender = mock(ResultSender.class, "MockGemFireResultSender");

		when(mockCache.rootRegions()).thenReturn(Collections.emptySet());
		when(mockFunctionContext.getResultSender()).thenReturn(mockResultSender);

		final AtomicReference<List<String>> regionNames = new AtomicReference<>(null);

		doAnswer(invocation -> {
			regionNames.compareAndSet(null, invocation.getArgument(0));
			return null;
		}).when(mockResultSender).lastResult(any(List.class));

		ListRegionsOnServerFunction function = spy(new ListRegionsOnServerFunction());

		doReturn(mockCache).when(function).getCache();

		function.execute(mockFunctionContext);

		List<String> actualRegionNames = regionNames.get();

		assertThat(actualRegionNames).isNotNull();
		assertThat(actualRegionNames.isEmpty()).isTrue();

		verify(mockCache, times(1)).rootRegions();
		verify(mockFunctionContext, times(1)).getResultSender();
		verify(mockResultSender, times(1)).lastResult(any(List.class));
	}

	@Test
	public void getIdIsFullyQualifiedClassName() {
		assertThat(function.getId()).startsWith(ListRegionsOnServerFunction.class.getName());
	}

	@Test
	public void hasResultIsTrue() {
		assertThat(function.hasResult()).isTrue();
	}

	@Test
	public void isHighAvailabilityAndOptimizeForWriteAreFalse() {
		assertThat(function.isHA()).isFalse();
		assertThat(function.optimizeForWrite()).isFalse();
	}
}
