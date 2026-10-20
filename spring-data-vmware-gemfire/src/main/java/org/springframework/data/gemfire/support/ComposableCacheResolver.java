/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Arrays;
import com.vmware.gemfire.driver.spi.DriverConnection;
import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Composition of {@link CacheResolver CacheResolvers} using
 * the <a href="https://en.wikipedia.org/wiki/Composite_pattern">Composite Software Design Pattern</a> that acts,
 * and can be referred to, as a single instance of {@link CacheResolver}.
 *
 * This implementation also supports caching the result of the resolution of the {@link DriverConnection}
 * instance reference.
 *
 * @author John Blum
 * @see DriverConnection
 * @see CacheResolver
 * @see AbstractCachingCacheResolver
 * @since 2.3.0
 */
public class ComposableCacheResolver<T extends DriverConnection> extends AbstractCachingCacheResolver<T> {

	private final CacheResolver<T> cacheResolverOne;
	private final CacheResolver<T> cacheResolverTwo;

	/**
	 * Factory method used to compose an array of {@link CacheResolver CacheResolvers} in a composition.
	 *
	 * @param <T> {@link Class subclass} of {@link DriverConnection}.
	 * @param cacheResolvers array of {@link CacheResolver CacheResolvers} to compose; may be {@literal null}.
	 * @return a composition from the array of {@link CacheResolver CacheResolvers}; may be {@literal null}.
	 * @see CacheResolver
	 * @see #compose(Iterable)
	 */
	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends DriverConnection> CacheResolver<T> compose(@Nullable CacheResolver<T>... cacheResolvers) {
		return compose(Arrays.asList(ArrayUtils.nullSafeArray(cacheResolvers, CacheResolver.class)));
	}

	/**
	 * Factory method used to compose an {@link Iterable} collection of {@link CacheResolver CacheResolvers}
	 * in a composition.
	 *
	 * @param <T> {@link Class subclass} of {@link DriverConnection}.
	 * @param cacheResolvers {@link Iterable} collection of {@link CacheResolver CacheResolvers} to compose;
	 * may be {@literal null}.
	 * @return a composition from the {@link Iterable} collection of {@link CacheResolver CacheResolvers};
	 * may be {@literal null}.
	 * @see #compose(CacheResolver, CacheResolver)
	 * @see CacheResolver
	 * @see Iterable
	 */
	@Nullable
	public static <T extends DriverConnection> CacheResolver<T> compose(@Nullable Iterable<CacheResolver<T>> cacheResolvers) {

		CacheResolver<T> current = null;

		for (CacheResolver<T> cacheResolver : CollectionUtils.nullSafeIterable(cacheResolvers)) {
			current = compose(current, cacheResolver);
		}

		return current;
	}

	/**
	 * Null-safe factory method used to compose two {@link CacheResolver} objects in a composition.
	 *
	 * @param <T> {@link Class subclass} of {@link DriverConnection}.
	 * @param one first {@link CacheResolver} in the composition.
	 * @param two second {@link CacheResolver} in the composition.
	 * @return the first {@link CacheResolver} if the second {@link CacheResolver} is {@literal null}.
	 * Return the second {@link CacheResolver} if the first {@link CacheResolver} is {@literal null}.
	 * Otherwise, return a composition of both {@link CacheResolver} one and two as a {@link ComposableCacheResolver}.
	 * @see CacheResolver
	 * @see #ComposableCacheResolver(CacheResolver, CacheResolver)
	 */
	@Nullable
	public static <T extends DriverConnection> CacheResolver<T> compose(@Nullable CacheResolver<T> one,
			@Nullable CacheResolver<T> two) {

		return one == null ? two : two == null ? one : new ComposableCacheResolver<>(one, two);
	}

	/**
	 * Constructs a new instance of {@link ComposableCacheResolver} initialized and composed with
	 * the given {@link CacheResolver CacheResolvers} forming the composition.
	 *
	 * @param cacheResolverOne first {@link CacheResolver} in the composition.
	 * @param cacheResolverTwo second {@link CacheResolver} in the composition.
	 * @throws IllegalArgumentException if either {@link CacheResolver} argument is {@literal null}.
	 * @see CacheResolver
	 */
	protected ComposableCacheResolver(@NonNull CacheResolver<T> cacheResolverOne,
			@NonNull CacheResolver<T> cacheResolverTwo) {

		Assert.notNull(cacheResolverOne, "CacheResolver 1 must not be null");
		Assert.notNull(cacheResolverTwo, "CacheResolver 2 must not be null");

		this.cacheResolverOne = cacheResolverOne;
		this.cacheResolverTwo = cacheResolverTwo;
	}

	/**
	 * Returns a reference to the first, non-null, configured {@link CacheResolver} in the composition.
	 *
	 * @return a reference to the first {@link CacheResolver} in the composition.
	 * @see CacheResolver
	 */
	protected @NonNull CacheResolver<T> getCacheResolverOne() {
		return this.cacheResolverOne;
	}

	/**
	 * Returns a reference to the second, non-null, configured {@link CacheResolver} in the composition.
	 *
	 * @return a reference to the second {@link CacheResolver} in the composition.
	 * @see CacheResolver
	 */
	protected @NonNull CacheResolver<T> getCacheResolverTwo() {
		return this.cacheResolverTwo;
	}

	/** Resolves in order, falling through only on null. Failures propagate unchanged. */
	@Override
	public T doResolve() {
		T cache = getCacheResolverOne().resolve();
		return cache != null ? cache : getCacheResolverTwo().resolve();
	}
}
