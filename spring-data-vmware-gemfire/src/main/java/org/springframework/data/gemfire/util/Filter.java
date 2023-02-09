/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.util.function.Predicate;

import org.springframework.lang.Nullable;

/**
 * The {@link Filter} interface defines a contract for filtering {@link Object objects}.
 *
 * @author John Blum
 * @param <T> {@link Class type} of {@link Object objects} being filtered.
 * @see java.lang.FunctionalInterface
 * @see java.util.function.Predicate
 * @since 1.0.0
 */
@FunctionalInterface
public interface Filter<T> extends Predicate<T> {

	/**
	 * Evaluates the given {@link Object} and determines whether the {@link Object} is accepted
	 * based on the filter criteria.
	 *
	 * @param obj {@link Object} to filter.
	 * @return a boolean value indicating whether this {@link Filter} accepts the given {@link Object}
	 * based on the filter criteria.
	 */
	boolean accept(@Nullable T obj);

	/**
	 * Tests whether the given {@link Object} matches the criteria defined by this {@link Filter}.
	 *
	 * @param obj {@link Object} to test.
	 * @return a boolean value indicating whether the given {@link Object} matches the criteria
	 * defined by this {@link Filter}.
	 * @see #accept(Object)
	 */
	@Override
	default boolean test(T obj) {
		return accept(obj);
	}
}
