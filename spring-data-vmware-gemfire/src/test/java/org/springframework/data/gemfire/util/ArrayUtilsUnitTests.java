/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit Tests for {@link ArrayUtils}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.util.ArrayUtils
 * @since 1.7.0
 */
public class ArrayUtilsUnitTests {

	@Test
	public void asArrayReturnsEmptyArray() {

		Object[] array = ArrayUtils.asArray();

		assertThat(array).isNotNull();
		assertThat(array.length).isEqualTo(0);
	}

	@Test
	public void asArrayReturnsMultiElementArray() {

		Object[] array = ArrayUtils.asArray(1, 2, 3);

		assertThat(array).isNotNull();
		assertThat(array.length).isEqualTo(3);
		assertThat(array).isEqualTo(new Object[] { 1, 2, 3 });
	}

	@Test
	public void asArrayReturnsSingleElementArray() {

		Object[] array = ArrayUtils.asArray(1);

		assertThat(array).isNotNull();
		assertThat(array.length).isEqualTo(1);
		assertThat(array).isEqualTo(new Object[] { 1 });
	}

	@Test
	public void defaultIfEmptyWithNonNullNonEmptyArrayReturnsArray() {

		Object[] array = { "test" };
		Object[] defaultArray = { "tested" };

		assertThat(ArrayUtils.defaultIfEmpty(array, defaultArray)).isSameAs(array);
	}

	@Test
	public void defaultIfEmptyWithEmptyArrayReturnsDefaultArray() {

		Object[] array = {};
		Object[] defaultArray = { "tested" };

		assertThat(ArrayUtils.defaultIfEmpty(array, defaultArray)).isSameAs(defaultArray);
	}

	@Test
	public void defaultIfEmptyWithNullArrayReturnsDefaultArray() {

		Object[] defaultArray = { "tested" };

		assertThat(ArrayUtils.defaultIfEmpty(null, defaultArray)).isSameAs(defaultArray);
	}

	@Test
	public void defaultIfEmptyWithNullArrayAndNullDefaultArrayReturnsNull() {
		assertThat(ArrayUtils.<Object[]>defaultIfEmpty(null, null)).isNull();
	}

	@Test
	public void getFirstWithNonNullArray() {
		assertThat(ArrayUtils.getFirst(ArrayUtils.asArray(1, 2, 3))).isEqualTo(1);
	}

	@Test
	public void getFirstWithNullOrEmptyArrayAndNoDefaultReturnsNull() {

		assertThat((Object) ArrayUtils.getFirst(null)).isNull();
		assertThat(ArrayUtils.getFirst(new Object[0])).isNull();
	}

	@Test
	public void getFirstWithNullOrEmptyArrayAndDefaultReturnsDefault() {

		assertThat(ArrayUtils.getFirst((Object[]) null, "test")).isEqualTo("test");
		assertThat(ArrayUtils.getFirst(new Object[0], "test")).isEqualTo("test");
	}

	@Test
	public void insertAtBeginning() {

		Object[] originalArray = { "testing", "tested" };
		Object[] newArray = ArrayUtils.insert(originalArray, 0, "test");

		assertThat(newArray).isNotSameAs(originalArray);
		assertThat(Arrays.equals(originalArray, newArray)).isFalse();
		assertThat(newArray).isEqualTo(new Object[] { "test", "testing", "tested" });
	}

	@Test
	public void insertInMiddle() {

		Object[] originalArray = { "test", "tested" };
		Object[] newArray = ArrayUtils.insert(originalArray, 1, "testing");

		assertThat(newArray).isNotSameAs(originalArray);
		assertThat(Arrays.equals(originalArray, newArray)).isFalse();
		assertThat(newArray).isEqualTo(new Object[] { "test", "testing", "tested" });
	}

	@Test
	public void insertAtEnd() {

		Object[] originalArray = { "test", "testing" };
		Object[] newArray = ArrayUtils.insert(originalArray, 2, "tested");

		assertThat(newArray).isNotSameAs(originalArray);
		assertThat(Arrays.equals(originalArray, newArray)).isFalse();
		assertThat(newArray).isEqualTo(new Object[] { "test", "testing", "tested" });
	}

	@Test
	public void isEmptyIsFalse() {

		assertThat(ArrayUtils.isEmpty(ArrayUtils.asArray("test", "testing", "tested"))).isFalse();
		assertThat(ArrayUtils.isEmpty(ArrayUtils.asArray("test"))).isFalse();
		assertThat(ArrayUtils.isEmpty(ArrayUtils.asArray(""))).isFalse();
		assertThat(ArrayUtils.isEmpty(ArrayUtils.asArray(null, null, null))).isFalse();
	}

	@Test
	public void isEmptyIsTrue() {

		assertThat(ArrayUtils.isEmpty(new Object[0])).isTrue();
		assertThat(ArrayUtils.isEmpty(null)).isTrue();
	}

	@Test
	public void length() {

		assertThat(ArrayUtils.length(ArrayUtils.asArray("test", "testing", "tested"))).isEqualTo(3);
		assertThat(ArrayUtils.length(ArrayUtils.asArray("test"))).isEqualTo(1);
		assertThat(ArrayUtils.length(ArrayUtils.asArray(""))).isEqualTo(1);
		assertThat(ArrayUtils.length(ArrayUtils.asArray(null, null, null))).isEqualTo(3);
		assertThat(ArrayUtils.length(new Object[0])).isEqualTo(0);
		assertThat(ArrayUtils.length(null)).isEqualTo(0);
	}

	@Test
	public void nullSafeArrayWithNonNullArray() {

		String[] stringArray = { "test", "testing", "tested" };

		assertThat(ArrayUtils.nullSafeArray(stringArray, String.class)).isSameAs(stringArray);

		Integer[] numberArray = { 1, 2, 3 };

		assertThat(ArrayUtils.nullSafeArray(numberArray, Integer.class)).isSameAs(numberArray);

		Double[] emptyDoubleArray = {};

		assertThat(ArrayUtils.nullSafeArray(emptyDoubleArray, Double.class)).isSameAs(emptyDoubleArray);

		Character[] characterArray = { 'A', 'B', 'C' };

		assertThat(ArrayUtils.nullSafeArray(characterArray, Character.class)).isSameAs(characterArray);
	}

	@Test
	public void nullSafeArrayWithNullArray() {

		Object array = ArrayUtils.nullSafeArray(null, String.class);

		assertThat(array).isInstanceOf(String[].class);
		assertThat(((String[]) array).length).isEqualTo(0);
	}

	@Test
	public void removeFromBeginning() {

		Object[] originalArray = { "test", "testing", "tested" };
		Object[] newArray = ArrayUtils.remove(originalArray, 0);

		assertThat(newArray).isNotSameAs(originalArray);
		assertThat(Arrays.equals(newArray, originalArray)).isFalse();
		assertThat(newArray).isEqualTo(new Object[] { "testing", "tested" });
	}

	@Test
	public void removeFromMiddle() {

		Object[] originalArray = { "test", "testing", "tested" };
		Object[] newArray = ArrayUtils.remove(originalArray, 1);

		assertThat(newArray).isNotSameAs(originalArray);
		assertThat(Arrays.equals(newArray, originalArray)).isFalse();
		assertThat(newArray).isEqualTo(new Object[] { "test", "tested" });
	}

	@Test
	public void removeFromEnd() {

		Object[] originalArray = { "test", "testing", "tested" };
		Object[] newArray = ArrayUtils.remove(originalArray, 2);

		assertThat(newArray).isNotSameAs(originalArray);
		assertThat(Arrays.equals(newArray, originalArray)).isFalse();
		assertThat(newArray).isEqualTo(new Object[] { "test", "testing" });
	}

	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sortIsSuccessful() {

		Comparable[] array = new Comparable[] { 2, 3, 1 };
		Comparable[] sortedArray = ArrayUtils.sort(array);

		assertThat(sortedArray).isSameAs(array);
		assertThat(sortedArray).isEqualTo(new Comparable[] { 1, 2, 3 });
	}

	@Test
	public void toIterableFromArray() {

		Integer[] array = { 1, 2, 3 };

		Iterable<Integer> iterable = ArrayUtils.toIterable(array);

		assertThat(iterable).isNotNull();
		assertThat(iterable).hasSize(array.length);
		assertThat(iterable).containsExactly(array);
	}

	@Test
	public void toIterableFromEmptyArray() {

		Iterable<?> iterable = ArrayUtils.toIterable();

		assertThat(iterable).isNotNull();
		assertThat(iterable).isEmpty();
	}

	@Test(expected = IllegalArgumentException.class)
	public void toIterableFromNullArrayThrowsIllegalArgumentException() {

		try {
			ArrayUtils.toIterable((Object[]) null);
		}
		catch (IllegalArgumentException expected) {

			assertThat(expected).hasMessage("Array must not be null");
			assertThat(expected).hasNoCause();

			throw expected;
		}
	}
}
