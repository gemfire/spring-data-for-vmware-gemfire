/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Arrays;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * A Spring {@link TypeFilter} implementation using the {@literal Composite Software Design Pattern} to compose multiple
 * {@link TypeFilter TypeFilters} acting a single instance of {@link TypeFilter}.
 *
 * @author John Blum
 * @see TypeFilter
 * @see <a href="https://en.wikipedia.org/wiki/Composite_pattern">Composite Software Design Pattern</a>
 * @since 2.4.0
 */
@FunctionalInterface
@SuppressWarnings("unused")
public interface CompositeTypeFilter extends TypeFilter {

	CompositeTypeFilter ALLOW = (metadataReader, metadataReaderFactory) -> true;
	CompositeTypeFilter DENY = (metadataReader, metadataReaderFactory) -> false;

	/**
	 * Composes {@literal this} {@link TypeFilter} with the given {@link TypeFilter}
	 * using the {@literal logical AND operator}.
	 *
	 * @param typeFilter {@link TypeFilter} to compose with {@literal this} {@link TypeFilter};
	 * must not be {@literal null}.
	 * @return a composed {@link TypeFilter} consisting of {@literal this} {@link TypeFilter}
	 * and the given {@link TypeFilter}.
	 * @throws IllegalArgumentException if {@link TypeFilter} is {@literal null}.
	 * @see TypeFilter
	 * @see #orThen(TypeFilter)
	 */
	default @NonNull CompositeTypeFilter andThen(@NonNull TypeFilter typeFilter) {

		Assert.notNull(typeFilter, "TypeFilter must not be null");

		return (metadataReader, metadataReaderFactory) ->
			this.match(metadataReader, metadataReaderFactory)
				&& typeFilter.match(metadataReader, metadataReaderFactory);
	}

	/**
	 * Composes the array of {@link TypeFilter TypeFilters} into a {@literal Composite} {@link TypeFilter}
	 * that acts as a single instance of {@link TypeFilter}.
	 *
	 * The {@literal Composite} {@link TypeFilter} may be treated as a single instance of {@link TypeFilter}, too,
	 * given {@link CompositeTypeFilter} extends (i.e. "is a") {@link TypeFilter}.
	 *
	 * @param array array of {@link TypeFilter TypeFilters} to compose.
	 * @return a {@literal Composite} {@link TypeFilter} composed from the array of {@link TypeFilter TypeFilers}
	 * using the {@literal logical AND operator}; may return {@literal null}.
	 * @see TypeFilter
	 * @see #composeAnd(Iterable)
	 */
	static @Nullable TypeFilter composeAnd(@Nullable TypeFilter... array) {
		return composeAnd(Arrays.asList(ArrayUtils.nullSafeArray(array, TypeFilter.class)));
	}

	/**
	 * Composes the {@link Iterable} of {@link TypeFilter TypeFilters} into a {@literal Composite} {@link TypeFilter}
	 * that acts as a single instance of {@link TypeFilter}.
	 *
	 * The {@literal Composite} {@link TypeFilter} may be treated as a single instance of {@link TypeFilter}, too,
	 * given {@link CompositeTypeFilter} extends (i.e. "is a") {@link TypeFilter}.
	 *
	 * @param iterable {@link Iterable} of {@link TypeFilter TypeFilters} to compose.
	 * @return a {@literal Composite} {@link TypeFilter} composed from the {@link Iterable}
	 * of {@link TypeFilter TypeFilers} using the {@literal logical AND operator}; may return {@literal null}.
	 * @see TypeFilter
	 * @see Iterable
	 */
	static @Nullable TypeFilter composeAnd(@Nullable Iterable<TypeFilter> iterable) {

		CompositeTypeFilter current = null;

		for (TypeFilter typeFilter : CollectionUtils.nullSafeIterable(iterable)) {
			if (typeFilter != null) {
				current = current == null ? of(typeFilter) : current.andThen(typeFilter);
			}
		}

		return current;
	}

	/**
	 * Composes the array of {@link TypeFilter TypeFilters} into a {@literal Composite} {@link TypeFilter}
	 * that acts as a single instance of {@link TypeFilter}.
	 *
	 * The {@literal Composite} {@link TypeFilter} may be treated as a single instance of {@link TypeFilter}, too,
	 * given {@link CompositeTypeFilter} extends (i.e. "is a") {@link TypeFilter}.
	 *
	 * @param array array of {@link TypeFilter TypeFilters} to compose.
	 * @return a {@literal Composite} {@link TypeFilter} composed from the array of {@link TypeFilter TypeFilers}
	 * using the {@literal logical OR operator}; may return {@literal null}.
	 * @see TypeFilter
	 * @see #composeOr(Iterable)
	 */
	static @Nullable TypeFilter composeOr(@Nullable TypeFilter... array) {
		return composeOr(Arrays.asList(ArrayUtils.nullSafeArray(array, TypeFilter.class)));
	}

	/**
	 * Composes the {@link Iterable} of {@link TypeFilter TypeFilters} into a {@literal Composite} {@link TypeFilter}
	 * that acts as a single instance of {@link TypeFilter}.
	 *
	 * The {@literal Composite} {@link TypeFilter} may be treated as a single instance of {@link TypeFilter}, too,
	 * given {@link CompositeTypeFilter} extends (i.e. "is a") {@link TypeFilter}.
	 *
	 * @param iterable {@link Iterable} of {@link TypeFilter TypeFilters} to compose.
	 * @return a {@literal Composite} {@link TypeFilter} composed from the {@link Iterable}
	 * of {@link TypeFilter TypeFilers} using the {@literal logical OR operator}; may return {@literal null}.
	 * @see TypeFilter
	 * @see Iterable
	 */
	static @Nullable TypeFilter composeOr(@Nullable Iterable<TypeFilter> iterable) {

		CompositeTypeFilter current = null;

		for (TypeFilter typeFilter : CollectionUtils.nullSafeIterable(iterable)) {
			if (typeFilter != null) {
				current = current == null ? of(typeFilter) : current.orThen(typeFilter);
			}
		}

		return current;
	}

	/**
	 * Wraps an existing {@link TypeFilter} in an instance of {@link CompositeTypeFilter}.
	 *
	 * @param typeFilter {@link TypeFilter} to wrap; must not be {@literal null}.
	 * @return a {@link CompositeTypeFilter} wrapping the existing {@link TypeFilter}.
	 * @throws IllegalArgumentException if {@link TypeFilter} is {@literal null}.
	 * @see TypeFilter
	 */
	static @NonNull CompositeTypeFilter of(@NonNull TypeFilter typeFilter) {

		Assert.notNull(typeFilter, "TypeFilter to wrap must not be null");

		return typeFilter instanceof CompositeTypeFilter
			? (CompositeTypeFilter) typeFilter
			: (metadataReader, metadataReaderFactory) -> typeFilter.match(metadataReader, metadataReaderFactory);
	}

	/**
	 * Negates the result of {@literal this} {@link TypeFilter TypeFilter's}
	 * {@link #match(MetadataReader, MetadataReaderFactory)} operation.
	 *
	 * @return {@literal this} {@link TypeFilter} negated.
	 * @see CompositeTypeFilter
	 */
	default @NonNull CompositeTypeFilter negate() {
		return (metadataReader, metadataReaderFactory) -> !this.match(metadataReader, metadataReaderFactory);
	}

	/**
	 * Composes {@literal this} {@link TypeFilter} with the given {@link TypeFilter}
	 * using the {@literal logical OR operator}.
	 *
	 * @param typeFilter {@link TypeFilter} to compose with {@literal this} {@link TypeFilter};
	 * must not be {@literal null}.
	 * @return a composed {@link TypeFilter} consisting of {@literal this} {@link TypeFilter}
	 * and the given {@link TypeFilter}.
	 * @throws IllegalArgumentException if {@link TypeFilter} is {@literal null}.
	 * @see TypeFilter
	 * @see #andThen(TypeFilter)
	 */
	default @NonNull CompositeTypeFilter orThen(@NonNull TypeFilter typeFilter) {

		return (metadataReader, metadataReaderFactory) ->
			this.match(metadataReader, metadataReaderFactory)
				|| typeFilter.match(metadataReader, metadataReaderFactory);
	}
}
