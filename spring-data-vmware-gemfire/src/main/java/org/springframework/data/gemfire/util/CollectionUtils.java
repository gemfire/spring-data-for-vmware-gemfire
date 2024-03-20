/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import static java.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.StreamSupport;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Abstract utility class used to operate on Java Collections Framework and classes.
 *
 * @author John Blum
 * @see Collection
 * @see Collections
 * @see Enumeration
 * @see Iterator
 * @see List
 * @see Map
 * @see Set
 * @see org.springframework.util.CollectionUtils
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public abstract class CollectionUtils extends org.springframework.util.CollectionUtils {

	/**
	 * Adds all elements from the given {@link Iterable} to the {@link Collection}.
	 *
	 * @param <E> {@link Class} type of the elements in the {@link Collection} and {@link Iterable}.
	 * @param <T> concrete {@link Class} type of the {@link Collection}.
	 * @param collection {@link Collection} in which to add the elements from the {@link Iterable}.
	 * @param iterable {@link Iterable} containing the elements to add to the {@link Collection}.
	 * @return the given {@link Collection}.
	 * @throws IllegalArgumentException if {@link Collection} is {@literal null}.
	 * @see Iterable
	 * @see Collection
	 */
	public static @NonNull <E, T extends Collection<E>> T addAll(@NonNull T collection, @Nullable Iterable<E> iterable) {

		Assert.notNull(collection, "Collection is required");

		stream(nullSafeIterable(iterable).spliterator(), false).forEach(collection::add);

		return collection;
	}

	/**
	 * Returns an unmodifiable {@link Set} containing the elements from the given object array.
	 *
	 * @param <T> Class type of the elements.
	 * @param elements array of objects to add to the {@link Set}.
	 * @return an unmodifiable {@link Set} containing the elements from the given object array.
	 */
	@SafeVarargs
	public static @NonNull <T> Set<T> asSet(@NonNull T... elements) {

		Set<T> set = new HashSet<>(elements.length);

		Collections.addAll(set, elements);

		return Collections.unmodifiableSet(set);
	}

	/**
	 * Null-safe method to determines whether the given {@link Collection} contains any elements from the given array.
	 *
	 * @param collection {@link Collection} to evaluate
	 * @param elements array of elements to evaluate.
	 * @return a boolean value indicating whether the collection contains at least 1 element from the given array.
	 * @see Collection#contains(Object)
	 */
	public static boolean containsAny(@Nullable Collection<?> collection, @Nullable Object... elements) {

		return Arrays.asList(ArrayUtils.nullSafeArray(elements, Object.class)).stream()
			.anyMatch(element -> nullSafeCollection(collection).contains(element));
	}

	/**
	 * Returns an empty {@link Iterable} object.
	 *
	 * @param <T> {@link Class} type of the elements in the {@link Iterable}.
	 * @return an empty {@link Iterable}.
	 * @see Iterable
	 * @see #nullSafeIterable(Iterable)
	 */
	public static @NonNull <T> Iterable<T> emptyIterable() {
		return Collections::emptyIterator;
	}

	/**
	 * Adapts the given Enumeration as an Iterable object for use within a for each loop.
	 *
	 * @param <T> the class type of the Enumeration elements.
	 * @param enumeration the Enumeration to adapt as an Iterable object.
	 * @return an Iterable instance of the Enumeration.
	 * @see Iterable
	 * @see Enumeration
	 */
	public static @NonNull <T> Iterable<T> iterable(@Nullable Enumeration<T> enumeration) {
		return () -> toIterator(nullSafeEnumeration(enumeration));
	}

	/**
	 * Adapts the given Iterator as an Iterable object for use within a for each loop.
	 *
	 * @param <T> the class type of the Iterator elements.
	 * @param iterator the Iterator to adapt as an Iterable object.
	 * @return an Iterable instance of the Iterator.
	 * @see Iterable
	 * @see Iterator
	 */
	public static @NonNull <T> Iterable<T> iterable(@Nullable Iterator<T> iterator) {
		return () -> nullSafeIterator(iterator);
	}

	/**
	 * Null-safe operation returning the given {@link Collection} if not {@literal null}
	 * or an empty {@link Collection} (implemented with {@link List}) if {@literal null}.
	 *
	 * @param <T> Class type of the {@link Collection} elements.
	 * @param collection {@link Collection} to evaluate.
	 * @return the given {@link Collection} if not null or return an empty {@link Collection}
	 * (implemented with {@link List}).
	 * @see Collections#emptyList()
	 * @see Collection
	 */
	public static @NonNull <T> Collection<T> nullSafeCollection(@Nullable Collection<T> collection) {
		return collection != null ? collection : Collections.emptyList();
	}

	/**
	 * Null-safe operation returning the given {@link Enumeration} if not {@literal null}
	 * or an {@link Collections#emptyEnumeration() empty Enumeration} if {@literal null}.
	 *
	 * @param <T> {@link Class type} of elements contained in the {@link Enumeration}.
	 * @param enumeration {@link Enumeration} to evaluate.
	 * @return the given {@link Enumeration} if not {@literal null}
	 * or an {@link Collections#emptyEnumeration() empty Enumeration}.
	 * @see Collections#emptyEnumeration()
	 * @see Enumeration
	 */
	public static @NonNull <T> Enumeration<T> nullSafeEnumeration(@Nullable Enumeration<T> enumeration) {
		return enumeration != null ? enumeration : Collections.emptyEnumeration();
	}

	/**
	 * A null-safe operation returning the original Iterable object if non-null or a default, empty Iterable
	 * implementation if null.
	 *
	 * @param <T> the class type of the iterable elements.
	 * @param iterable the Iterable object evaluated for a null reference.
	 * @return the Iterable object if not null or a default, empty Iterable implementation otherwise.
	 * @see #emptyIterable()
	 * @see Iterable
	 */
	public static @NonNull <T> Iterable<T> nullSafeIterable(@Nullable Iterable<T> iterable) {
		return iterable != null ? iterable : emptyIterable();
	}

	/**
	 * Returns the given {@link Iterable} if not {@literal null} or {@literal empty},
	 * otherwise returns the {@code defaultIterable}.
	 *
	 * @param <T> concrete {@link Class} type of the {@link Iterable}.
	 * @param <E> {@link Class} type of the elements in the {@link Iterable Iterables}.
	 * @param iterable {@link Iterable} to evaluate.
	 * @param defaultIterable {@link Iterable} to return if the given {@code iterable} is {@literal null} or empty.
	 * @return {@code iterable} if not {@literal null} or empty otherwise return {@code defaultIterable}.
	 * @see Iterable
	 */
	public static @Nullable <E, T extends Iterable<E>> T nullSafeIterable(@Nullable T iterable,
			@Nullable T defaultIterable) {

		return Optional.ofNullable(iterable)
			.filter(it -> it.iterator().hasNext())
			.orElse(defaultIterable);
	}

	/**
	 * Null-safe operation returning the given {@link Iterator} if not {@literal null}
	 * or an {@link Collections#emptyIterator() empty Iterator} if {@literal null}.
	 *
	 * @param <T> {@link Class type} of elements contained in the {@link Iterator}.
	 * @param iterator {@link Iterator} to evaluate.
	 * @return the given {@link Iterator} if not {@literal null}
	 * or an {@link Collections#emptyIterator()  empty Iterator}.
	 * @see Collections#emptyIterator()
	 * @see Iterator
	 */
	public static @NonNull <T> Iterator<T> nullSafeIterator(@Nullable Iterator<T> iterator) {
		return iterator != null ? iterator : Collections.emptyIterator();
	}

	/**
	 * Null-safe operation returning the given {@link List} if not {@literal null}
	 * or an empty {@link List} if {@literal null}.
	 *
	 * @param <T> Class type of the {@link List} elements.
	 * @param list {@link List} to evaluate.
	 * @return the given {@link List} if not null or an empty {@link List}.
	 * @see Collections#emptyList()
	 * @see List
	 */
	public static @NonNull <T> List<T> nullSafeList(@Nullable List<T> list) {
		return list != null ? list : Collections.emptyList();
	}

	/**
	 * Null-safe operation returning the given {@link Map} if not {@literal null}
	 * or an empty {@link Map} if {@literal null}.
	 *
	 * @param <K> Class type of the {@link Map Map's} keys.
	 * @param <V> Class type of the {@link Map Map's} values.
	 * @param map {@link Map} to evaluate.
	 * @return the given {@link Map} if not null or an empty {@link Map}.
	 * @see Collections#emptyMap()
	 * @see Map
	 */
	@SuppressWarnings("all")
	public static @NonNull <K, V> Map<K, V> nullSafeMap(@Nullable Map<K, V> map) {
		return map != null ? map : Collections.<K, V>emptyMap();
	}

	/**
	 * Null-safe operation returning the given {@link Set} if not {@literal null}
	 * or an empty {@link Set} if {@literal null}.
	 *
	 * @param <T> Class type of the {@link Set} elements.
	 * @param set {@link Set} to evaluate.
	 * @return the given {@link Set} if not null or an empty {@link Set}.
	 * @see Collections#emptySet()
	 * @see Set
	 */
	public static @NonNull <T> Set<T> nullSafeSet(@Nullable Set<T> set) {
		return set != null ? set : Collections.emptySet();
	}

	/**
	 * Determines whether the given {@link Collection} is {@link Collection#isEmpty() empty}.
	 *
	 * @param collection {@link Collection} to evaluate.
	 * @return a boolean value indicating whether the given {@link Collection} is {@link Collection#isEmpty() empty}.
	 * @see #nullSafeCollection(Collection)
	 * @see Collection#isEmpty()
	 */
	public static boolean nullSafeIsEmpty(@Nullable Collection<?> collection) {
		return nullSafeCollection(collection).isEmpty();
	}

	/**
	 * Determines whether the given {@link Iterable} is empty.
	 *
	 * @param iterable {@link Iterable} to evaluate.
	 * @return a boolean value indicating whether the given {@link Iterable} is empty.
	 * @see Iterable
	 * @see #nullSafeIterable(Iterable)
	 */
	public static boolean nullSafeIsEmpty(@Nullable Iterable<?> iterable) {
		return !nullSafeIterable(iterable).iterator().hasNext();
	}

	/**
	 * Determines whether the given {@link Map} is {@link Map#isEmpty() empty}.
	 *
	 * @param map {@link Map} to evaluate.
	 * @return a boolean value indicating whether the given {@link Map} is {@link Map#isEmpty() empty}.
	 * @see #nullSafeMap(Map)
	 * @see Map#isEmpty()
	 */
	public static boolean nullSafeIsEmpty(@Nullable Map<?, ?> map) {
		return nullSafeMap(map).isEmpty();
	}

	/**
	 * Determines the {@link Collection#size()} of the given {@link Collection}.
	 *
	 * @param collection {@link Collection} to evaluate.
	 * @return the {@link Collection#size()} of the given {@link Collection}.
	 * @see #nullSafeCollection(Collection)
	 * @see Collection#size()
	 */
	public static int nullSafeSize(@Nullable Collection<?> collection) {
		return nullSafeCollection(collection).size();
	}

	/**
	 * Determines the {@link Long size} of the give {@link Iterable}.
	 *
	 * @param iterable {@link Iterable} to evaluate.
	 * @return the {@link Long size} indicating the number of elements contained by the given {@link Iterable}.
	 * If {@link Iterable} is {@literal null}, then returns {@literal 0}.
	 * @see Iterable
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static long nullSafeSize(@Nullable Iterable iterable) {
		return StreamSupport.stream(nullSafeIterable(iterable).spliterator(), false).count();
	}

	/**
	 * Determines the {@link Map#size()} of the given {@link Map}.
	 *
	 * @param map {@link Map} to evaluate.
	 * @return the {@link Map#size()} of the given {@link Map}.
	 * @see #nullSafeMap(Map)
	 * @see Map#size()
	 */
	public static int nullSafeSize(@Nullable Map<?, ?> map) {
		return nullSafeMap(map).size();
	}

	/**
	 * Sors the elements of the given {@link List} by their natural, {@link Comparable} ordering.
	 *
	 * @param <T> {@link Comparable} class type of the collection elements.
	 * @param list {@link List} of elements to sort.
	 * @return the {@link List} sorted.
	 * @see Collections#sort(List)
	 * @see List
	 */
	public static @NonNull <T extends Comparable<T>> List<T> sort(@NonNull List<T> list) {

		Assert.notNull(list, "List must not be null");

		Collections.sort(list);

		return list;
	}

	/**
	 * Returns a sub-list of elements from the given {@link List} based on the provided {@code indices}.
	 *
	 * @param <T> Class type of the elements in the list.
	 * @param source {@link List} from which the elements of the sub-list is constructed.
	 * @param indices array of indexes in the {@code source} {@link List} to the elements
	 * used to construct the sub-list.
	 * @return a sub-list of elements from the given {@link List} based on the provided {@code indices}.
	 * @throws IndexOutOfBoundsException if the array of indexes contains an index that is not within
	 * the bounds of the list.
	 * @throws NullPointerException if either the list or indexes are null.
	 * @see List
	 */
	public static @NonNull <T> List<T> subList(@NonNull List<T> source, int... indices) {

		Assert.notNull(source, "List must not be null");

		List<T> result = new ArrayList<>(indices.length);

		for (int index : indices) {
			result.add(source.get(index));
		}

		return result;
	}

	/**
	 * Returns a {@link String} representation of the given {@link Map}.
	 *
	 * @param map {@link Map} represent as a {@link String}.
	 * @return a {@link String} describing the given {@link Map}.
	 * @see #newSortedMap(Map)
	 * @see Map
	 */
	public static @NonNull String toString(@Nullable Map<?, ?> map) {

		StringBuilder builder = new StringBuilder("{\n");

		AtomicInteger count = new AtomicInteger(0);

		newSortedMap(map).forEach((key, value) -> {
			builder.append(count.incrementAndGet() > 1 ? ",\n" : "");
			builder.append("\t");
			builder.append(key);
			builder.append(" = ");
			builder.append(value);
		});

		builder.append("\n}");

		return builder.toString();
	}

	private static @NonNull SortedMap<?, ?> newSortedMap(@Nullable Map<?, ?> map) {
		return new TreeMap<>(nullSafeMap(map));
	}
}
