/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Unit Tests for {@link StreamUtils}.
 *
 * @author John Blum
 * @see java.util.stream.Stream
 * @see org.junit.Test
 * @see org.mockito.Mockito
 * @see org.springframework.data.gemfire.util.StreamUtils
 * @since 2.0.0
 */
public class StreamUtilsUnitTests {

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void concatNullStreamArray() {

		Stream stream = StreamUtils.concat((Stream[]) null);

		assertThat(stream).isNotNull();
		assertThat(stream.count()).isZero();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void concatNoStreams() {

		Stream<Object> stream = StreamUtils.concat();

		assertThat(stream).isNotNull();
		assertThat(stream.count()).isZero();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void concatOneStream() {

		Stream<Integer> stream = StreamUtils.concat(Stream.of(1, 2, 3));

		assertThat(stream).isNotNull();
		assertThat(stream.collect(Collectors.toList())).containsExactly(1, 2, 3);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void concatTwoStreams() {

		Stream<Integer> stream = StreamUtils.concat(Stream.of(1, 2, 3), Stream.of(4, 5, 6));

		assertThat(stream).isNotNull();
		assertThat(stream.collect(Collectors.toList())).containsExactly(1, 2, 3, 4, 5, 6);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void concatThreeStreams() {

		Stream<Integer> stream =
			StreamUtils.concat(Stream.of(1, 2, 3), Stream.of(4, 5, 6), Stream.of(7, 8, 9));

		assertThat(stream).isNotNull();
		assertThat(stream.collect(Collectors.toList())).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9);
	}

	@Test
	public void countNullStream() {
		assertThat(StreamUtils.nullSafeCount(null)).isZero();
	}

	@Test
	public void countEmptyStream() {
		assertThat(StreamUtils.nullSafeCount(Stream.empty())).isZero();
	}

	@Test
	public void countOneElementStream() {
		assertThat(StreamUtils.nullSafeCount(Stream.of(1))).isOne();
	}

	@Test
	public void countTwoElementStream() {
		assertThat(StreamUtils.nullSafeCount(Stream.of(1, 2))).isEqualTo(2);
	}

	@Test
	public void isEmptyWithNullStream() {
		assertThat(StreamUtils.nullSafeIsEmpty(null)).isTrue();
	}

	@Test
	public void isEmptyWithEmptyStream() {
		assertThat(StreamUtils.nullSafeIsEmpty(Stream.empty())).isTrue();
	}

	@Test
	public void isEmptyWithOneElementStream() {
		assertThat(StreamUtils.nullSafeIsEmpty(Stream.of(1))).isFalse();
	}

	@Test
	public void isEmptyWithTwoElementStream() {
		assertThat(StreamUtils.nullSafeIsEmpty(Stream.of(1, 2))).isFalse();
	}

	@Test
	public void nullSafeStreamWithStream() {

		Stream<?> mockStream = mock(Stream.class);

		assertThat(StreamUtils.nullSafeStream(mockStream)).isSameAs(mockStream);
	}

	@Test
	public void nullSafeStreamWithNull() {

		Stream<?> stream = StreamUtils.nullSafeStream(null);

		assertThat(stream).isNotNull();
		assertThat(stream.count()).isZero();
	}
}
