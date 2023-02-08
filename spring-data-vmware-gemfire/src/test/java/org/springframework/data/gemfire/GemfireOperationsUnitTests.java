/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import org.springframework.data.gemfire.test.support.MapBuilder;

/**
 * Unit Tests for the {@link GemfireOperations} interface.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see Mock
 * @see org.mockito.Mockito
 * @see MockitoJUnitRunner
 * @see GemfireOperations
 * @since 2.5.0
 */
@RunWith(MockitoJUnitRunner.class)
public class GemfireOperationsUnitTests {

	@Mock
	private GemfireOperations mockGemfireOperations;

	@Test
	public void defaultGetAllKeysCallsGetForEachKey() {

		Map<Integer, String> expected = MapBuilder.<Integer, String>newMapBuilder()
			.put(1, "one")
			.put(2, "two")
			.put(3, "three")
			.build();

		doAnswer(invocation -> expected.get(invocation.<Integer>getArgument(0)))
			.when(this.mockGemfireOperations).get(isA(Integer.class));

		doCallRealMethod().when(this.mockGemfireOperations).getAll(isA(Collection.class));

		Map<Integer, String> actual = this.mockGemfireOperations.getAll(expected.keySet());

		assertThat(actual).isNotNull();
		assertThat(actual).isEqualTo(expected);

		verify(this.mockGemfireOperations, times(1)).getAll(eq(expected.keySet()));

		expected.forEach((key, value) ->
			verify(this.mockGemfireOperations, times(1)).get(eq(key)));

		verifyNoMoreInteractions(this.mockGemfireOperations);
	}

	@Test
	public void defaultGetAllIsNullSafe() {

		doCallRealMethod().when(this.mockGemfireOperations).getAll(any());

		Map<?, ?> map = this.mockGemfireOperations.getAll(null);

		assertThat(map).isNotNull();
		assertThat(map).isEmpty();
	}

	@Test
	public void defaultPutAllCallsPutForEachKeyValue() {

		Map<Integer, String> expected = MapBuilder.<Integer, String>newMapBuilder()
			.put(1, "one")
			.put(2, "two")
			.put(3, "three")
			.build();

		doCallRealMethod().when(this.mockGemfireOperations).putAll(isA(Map.class));

		this.mockGemfireOperations.putAll(expected);

		verify(this.mockGemfireOperations, times(1)).putAll(eq(expected));

		expected.forEach((key, value) ->
			verify(this.mockGemfireOperations, times(1)).put(eq(key), eq(value)));

		verifyNoMoreInteractions(this.mockGemfireOperations);
	}

	@Test
	public void defaultPutAllIsNullSafe() {

		doCallRealMethod().when(this.mockGemfireOperations).putAll(any());

		this.mockGemfireOperations.putAll(null);
	}

	@Test
	public void defaultRemoveAllCallsRemoveForEachKey() {

		Collection<?> keys = Arrays.asList(1, 2, 3);

		doCallRealMethod().when(this.mockGemfireOperations).removeAll(isA(Collection.class));

		this.mockGemfireOperations.removeAll(keys);

		verify(this.mockGemfireOperations, times(1)).removeAll(eq(keys));

		keys.forEach(key -> verify(this.mockGemfireOperations, times(1)).remove(eq(key)));

		verifyNoMoreInteractions(this.mockGemfireOperations);
	}

	@Test
	public void defaultRemoveAllIsNullSafe() {

		doCallRealMethod().when(this.mockGemfireOperations).removeAll(any());

		this.mockGemfireOperations.removeAll(null);
	}
}
