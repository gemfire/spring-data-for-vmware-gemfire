/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link ArrayUtils} is an abstract utility class used to work with {@link Object} arrays.
 *
 * @author David Turanski
 * @author John Blum
 * @see Array
 * @see Arrays
 */
public abstract class ArrayUtils {

	/**
	 * Returns the given varargs {@code element} as an array.
	 *
	 * @param <T> Class type of the elements.
	 * @param elements variable list of arguments to return as an array.
	 * @return an arry for the given varargs {@code elements}.
	 */
	@SafeVarargs
	public static <T> T[] asArray(T... elements) {
		return elements;
	}

	/**
	 * Returns the given {@code array} if not {@literal null} or empty, otherwise returns the {@code defaultArray}.
	 *
	 * @param <T> {@link Class} type of the array elements.
	 * @param array array to evaluate.
	 * @param defaultArray array to return if the given {@code array} is {@literal null} or empty.
	 * @return the given {@code array} if not {@literal null} or empty otherwise return the {@code defaultArray}.
	 */
	public static @Nullable <T> T[] defaultIfEmpty(@Nullable T[] array, @Nullable T[] defaultArray) {
		return isNotEmpty(array) ? array : defaultArray;
	}

	/**
	 * Null-safe method to return the first element in the array or {@literal null}
	 * if the array is {@literal null} or empty.
	 *
	 * @param <T> Class type of the array elements.
	 * @param array the array from which to extract the first element.
	 * @return the first element in the array or {@literal null} if the array is null or empty.
	 * @see #getFirst(Object[], Object)
	 */
	public static @Nullable <T> T getFirst(@Nullable T[] array) {
		return getFirst(array, null);
	}

	/**
	 * Null-safe method to return the first element in the array or the {@code defaultValue}
	 * if the array is {@literal null} or empty.
	 *
	 * @param <T> Class type of the array elements.
	 * @param array the array from which to extract the first element.
	 * @param defaultValue value to return if the array is {@literal null} or empty.
	 * @return the first element in the array or {@code defaultValue} if the array is {@literal null} or empty.
	 * @see #getFirst(Object[])
	 */
	public static @Nullable <T> T getFirst(@Nullable T[] array, @Nullable T defaultValue) {
		return isEmpty(array) ? defaultValue : array[0];
	}

	/**
	 * Insert an element into the given array at position (index).  The element is inserted at the given position
	 * and all elements afterwards are moved to the right.
	 *
	 * @param originalArray the array in which to insert the element.
	 * @param position an integer index (position) at which to insert the element in the array.
	 * @param element the element to insert into the array.
	 * @return a new array with the element inserted at position.
	 * @see System#arraycopy(Object, int, Object, int, int)
	 * @see Array#newInstance(Class, int)
	 */
	public static @NonNull Object[] insert(@NonNull Object[] originalArray, int position, Object element) {

		Object[] newArray =
			(Object[]) Array.newInstance(originalArray.getClass().getComponentType(), originalArray.length + 1);


		// copy all elements before the given position (here, position refers to the length, or number of elements
		// to be copied, excluding the element at originalArray[position]
		if (position > 0) {
			System.arraycopy(originalArray, 0, newArray, 0, position);
		}

		// insert
		newArray[position] = element;

		// copy remaining elements from originalArray, starting at position, to new array
		if (position < originalArray.length) {
			System.arraycopy(originalArray, position, newArray, position + 1,
				originalArray.length - position);
		}

		return newArray;
	}

	/**
	 * Determines whether the given array is empty or not.
	 *
	 * @param array the array to evaluate for emptiness.
	 * @return a boolean value indicating whether the given array is empty.
	 * @see #length(Object...)
	 */
	public static boolean isEmpty(@Nullable Object[] array) {
		return length(array) == 0;
	}

	/**
	 * Determines whether the given array is empty or not.
	 *
	 * @param array the array to evaluate for emptiness.
	 * @return a boolean value indicating whether the given array is empty.
	 * @see #isEmpty(Object[])
	 */
	public static boolean isNotEmpty(@Nullable Object[] array) {
		return !isEmpty(array);
	}

	/**
	 * Null-safe operation to determine an array's length.
	 *
	 * @param array the array to determine it's length.
	 * @return the length of the given array or 0 if the array reference is null.
	 */
	public static int length(@Nullable Object[] array) {
		return array != null ? array.length : 0;
	}

	/**
	 * Null-safe, empty array operation returning the given object array if not null or an empty object array
	 * if the array argument is null.
	 *
	 * @param <T> Class type of the array elements.
	 * @param array array of objects on which a null check is performed.
	 * @param componentType Class type of the array elements.
	 * @return the given object array if not null, otherwise return an empty object array.
	 * @see Array#newInstance(Class, int)
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] nullSafeArray(@Nullable T[] array, @NonNull Class<T> componentType) {
		return array != null ? array : (T[]) Array.newInstance(componentType, 0);
	}

	/**
	 * Remove an element from the given array at position (index).  The element is removed at the specified position
	 * and all remaining elements are shifted to the left.
	 *
	 * @param originalArray the array from which to remove the element.
	 * @param position the integer index (position) indicating the element to remove from the array.
	 * @return a new array with the element at position in the originalArray removed.
	 * @see System#arraycopy(Object, int, Object, int, int)
	 * @see Array#newInstance(Class, int)
	 */
	public static Object[] remove(@NonNull Object[] originalArray, int position) {

		Object[] newArray =
			(Object[]) Array.newInstance(originalArray.getClass().getComponentType(), originalArray.length - 1);

		// copy all elements before position (here, position refers to the length, or number of elements to be copied
		if (position > 0) {
			System.arraycopy(originalArray, 0, newArray, 0, position);
		}

		// copy remaining elements after position from the originalArray
		if (position < originalArray.length - 1) {
			System.arraycopy(originalArray, position + 1, newArray, position,
				originalArray.length - 1 - position);
		}

		return newArray;
	}

	/**
	 * Sort the array of elements according to the elements natural ordering.
	 *
	 * @param <T> {@link Comparable} class type of the array elements.
	 * @param array array of elements to sort.
	 * @return the sorted array of elements.
	 * @see Arrays#sort(Object[])
	 */
	public static @NonNull <T extends Comparable<T>> T[] sort(@NonNull T[] array) {

		Arrays.sort(array);

		return array;
	}

	/**
	 * Converts the given array into an {@link Iterable} object.
	 *
	 * @param <T> {@link Class type} of the array elements; mut not be {@literal null}.
	 * @param array array to convert to an {@link Iterable}.
	 * @return an {@link Iterable} object from the given array.
	 * @throws IllegalArgumentException if the array is {@literal null}.
	 * @see Iterable
	 */
	@SuppressWarnings("unchecked")
	public static @NonNull <T> Iterable<T> toIterable(@NonNull T... array) {
		return IterableArray.of(array);
	}

	protected static class IterableArray<T> implements Iterable<T> {

		@SuppressWarnings("unchecked")
		protected static <T> IterableArray<T> of(@NonNull T... array) {
			return new IterableArray<>(array);
		}

		private final T[] array;

		protected IterableArray(@NonNull T[] array) {

			Assert.notNull(array, "Array must not be null");

			this.array = array;
		}

		@Override
		public Iterator<T> iterator() {

			return new Iterator<T>() {

				int index = 0;

				@Override
				public boolean hasNext() {
					return this.index < IterableArray.this.array.length;
				}

				@Override
				public T next() {
					return IterableArray.this.array[this.index++];
				}
			};
		}
	}
}
