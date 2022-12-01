/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 * Unit tests for {@link Filter}.
 *
 * @author John Blum
 * @see Test
 * @see org.mockito.Mockito
 * @see Filter
 * @since 1.0.0
 */
public class FilterUnitTests {

	@Test
	@SuppressWarnings("unchecked")
	public void andIsCorrect() {

		Filter mockFilterOne = mock(Filter.class);
		Filter mockFilterTwo = mock(Filter.class);
		Filter mockFilterThree = mock(Filter.class);
		Filter mockFilterFour = mock(Filter.class);

		when(mockFilterOne.accept(any())).thenReturn(false);
		when(mockFilterOne.and(any())).thenCallRealMethod();
		when(mockFilterOne.test(any())).thenCallRealMethod();
		when(mockFilterTwo.accept(any())).thenReturn(true);
		when(mockFilterTwo.and(any())).thenCallRealMethod();
		when(mockFilterTwo.test(any())).thenCallRealMethod();
		when(mockFilterThree.accept(any())).thenReturn(false);
		when(mockFilterThree.and(any())).thenCallRealMethod();
		when(mockFilterThree.test(any())).thenCallRealMethod();
		when(mockFilterFour.accept(any())).thenReturn(true);
		when(mockFilterFour.and(any())).thenCallRealMethod();
		when(mockFilterFour.test(any())).thenCallRealMethod();

		assertThat(mockFilterOne.and(mockFilterTwo).test("test")).isFalse();
		assertThat(mockFilterOne.and(mockFilterThree).test("test")).isFalse();
		assertThat(mockFilterTwo.and(mockFilterThree).test("test")).isFalse();
		assertThat(mockFilterTwo.and(mockFilterFour).test("test")).isTrue();

		verify(mockFilterOne, times(2)).accept(eq("test"));
		verify(mockFilterTwo, times(2)).accept(eq("test"));
		verify(mockFilterThree, times(1)).accept(eq("test"));
		verify(mockFilterFour, times(1)).accept(eq("test"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void orIsCorrect() {

		Filter mockFilterOne = mock(Filter.class);
		Filter mockFilterTwo = mock(Filter.class);
		Filter mockFilterThree = mock(Filter.class);
		Filter mockFilterFour = mock(Filter.class);

		when(mockFilterOne.accept(any())).thenReturn(false);
		when(mockFilterOne.or(any())).thenCallRealMethod();
		when(mockFilterOne.test(any())).thenCallRealMethod();
		when(mockFilterTwo.accept(any())).thenReturn(true);
		when(mockFilterTwo.or(any())).thenCallRealMethod();
		when(mockFilterTwo.test(any())).thenCallRealMethod();
		when(mockFilterThree.accept(any())).thenReturn(false);
		when(mockFilterThree.or(any())).thenCallRealMethod();
		when(mockFilterThree.test(any())).thenCallRealMethod();
		when(mockFilterFour.accept(any())).thenReturn(true);
		when(mockFilterFour.or(any())).thenCallRealMethod();
		when(mockFilterFour.test(any())).thenCallRealMethod();

		assertThat(mockFilterOne.or(mockFilterTwo).test("test")).isTrue();
		assertThat(mockFilterOne.or(mockFilterThree).test("test")).isFalse();
		assertThat(mockFilterTwo.or(mockFilterThree).test("test")).isTrue();
		assertThat(mockFilterTwo.or(mockFilterFour).test("test")).isTrue();

		verify(mockFilterOne, times(2)).accept(eq("test"));
		verify(mockFilterTwo, times(3)).accept(eq("test"));
		verify(mockFilterThree, times(1)).accept(eq("test"));
		verify(mockFilterFour, never()).accept(eq("test"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void negateReturnsFalseForTrue() {

		Filter<Object> mockFilter = mock(Filter.class);

		when(mockFilter.accept(any())).thenReturn(true);
		when(mockFilter.negate()).thenCallRealMethod();
		when(mockFilter.test(any())).thenCallRealMethod();

		assertThat(mockFilter.negate().test("test")).isFalse();

		verify(mockFilter, times(1)).accept(eq("test"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void negateReturnsTrueForFalse() {

		Filter<Object> mockFilter = mock(Filter.class);

		when(mockFilter.accept(any())).thenReturn(false);
		when(mockFilter.negate()).thenCallRealMethod();
		when(mockFilter.test(any())).thenCallRealMethod();

		assertThat(mockFilter.negate().test("test")).isTrue();

		verify(mockFilter, times(1)).accept(eq("test"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void predicateTestCallsFilterAcceptReturnsTrue() {

		Filter<Object> mockFilter = mock(Filter.class);

		when(mockFilter.accept(any())).thenReturn(true);
		when(mockFilter.test(any())).thenCallRealMethod();

		assertThat(mockFilter.test("test")).isTrue();

		verify(mockFilter, times(1)).accept(eq("test"));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void predicateTestCallsFilterAcceptReturnsFalse() {

		Filter<Object> mockFilter = mock(Filter.class);

		when(mockFilter.accept(any())).thenReturn(false);
		when(mockFilter.test(any())).thenCallRealMethod();

		assertThat(mockFilter.test("test")).isFalse();

		verify(mockFilter, times(1)).accept(eq("test"));
	}
}
