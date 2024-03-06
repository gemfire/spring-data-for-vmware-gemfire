/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Arrays;
import java.util.function.Function;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionDestroyedException;

import org.springframework.data.gemfire.RegionResolver;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RegionResolver} implementation used to compose a collection of {@link RegionResolver RegionResolvers}.
 *
 * @author John Blum
 * @see Region
 * @see RegionResolver
 * @see AbstractCachingRegionResolver
 * @since 2.3.0
 */
public class ComposableRegionResolver extends AbstractCachingRegionResolver {

	/**
	 * Factory method used to compose an array of {@link RegionResolver RegionResolvers} in a composition.
	 *
	 * @param regionResolvers array of {@link RegionResolver RegionResolvers} to compose; may be {@literal null}.
	 * @return a composition from the array of {@link RegionResolver RegionResolvers}; may be {@literal null}.
	 * @see RegionResolver
	 * @see #compose(Iterable)
	 */
	public static RegionResolver compose(@Nullable RegionResolver... regionResolvers) {
		return compose(Arrays.asList(ArrayUtils.nullSafeArray(regionResolvers, RegionResolver.class)));
	}

	/**
	 * Factory method used to compose an {@literal Iterable} collection of {@link RegionResolver RegionResolvers}
	 * in a composition.
	 *
	 * @param regionResolvers {@link Iterable} collection of {@link RegionResolver RegionResolvers} to compose;
	 * may be {@literal null}.
	 * @return a composition from the {@link Iterable} collection of {@link RegionResolver RegionResolvers};
	 * may be {@literal null}.
	 * @see #compose(RegionResolver, RegionResolver)
	 * @see RegionResolver
	 * @see Iterable
	 */
	public static @Nullable RegionResolver compose(@Nullable Iterable<RegionResolver> regionResolvers) {

		RegionResolver current = null;

		for (RegionResolver regionResolver : CollectionUtils.nullSafeIterable(regionResolvers)) {
			current = compose(current, regionResolver);
		}

		return current;
	}

	/**
	 * Composes two {@link RegionResolver RegionResolvers} in a composition.
	 *
	 * @param one first {@link RegionResolver} in the composition.
	 * @param two second {@link RegionResolver} in the composition.
	 * @return a {@link ComposableRegionResolver} composed of the {@link RegionResolver} arguments.
	 * Returns the first {@link RegionResolver} if the second is {@literal null}.
	 * Returns the second {@link RegionResolver} if the first is {@literal null}.
	 * Returns {@literal null} if both {@link RegionResolver} arguments are {@literal null}.
	 * @see #ComposableRegionResolver(RegionResolver, RegionResolver)
	 * @see RegionResolver
	 */
	public static @Nullable RegionResolver compose(@Nullable RegionResolver one, @Nullable RegionResolver two) {
		return one == null ? two : two == null ? one : new ComposableRegionResolver(one, two);
	}

	private final RegionResolver regionResolverOne;
	private final RegionResolver regionResolverTwo;

	/**
	 * Constructs a new instance of {@link ComposableRegionResolver} initialized and composed with
	 * the given {@link RegionResolver RegionResolvers} forming the composition.
	 *
	 * @param regionResolverOne first {@link RegionResolver} in the composition; must not be {@literal null}.
	 * @param regionResolverTwo second {@link RegionResolver} in the composition; must not be {@literal null}.
	 * @throws IllegalArgumentException if either {@link RegionResolver} argument is {@literal null}.
	 * @see RegionResolver
	 */
	protected ComposableRegionResolver(@NonNull RegionResolver regionResolverOne,
			@NonNull RegionResolver regionResolverTwo) {

		Assert.notNull(regionResolverOne, "RegionResolver 1 must not be null");
		Assert.notNull(regionResolverTwo, "RegionResolver 2 must not be null");

		this.regionResolverOne = regionResolverOne;
		this.regionResolverTwo = regionResolverTwo;
	}

	/**
	 * Returns a reference to the first, non-null, configured {@link RegionResolver} in the composition.
	 *
	 * @return a reference to the first {@link RegionResolver} in the composition.
	 * @see RegionResolver
	 */
	protected @NonNull RegionResolver getRegionResolverOne() {
		return this.regionResolverOne;
	}

	/**
	 * Returns a reference to the second, non-null, configured {@link RegionResolver} in the composition.
	 *
	 * @return a reference to the second {@link RegionResolver} in the composition.
	 * @see RegionResolver
	 */
	protected @NonNull RegionResolver getRegionResolverTwo() {
		return this.regionResolverTwo;
	}

	/**
	 * Resolves the first {@literal non-null} reference to cache {@link Region} identified by
	 * the given {@link String name}, handling any {@link Exception Exceptions} throwing by
	 * the composed {@link RegionResolver RegionResolvers}, such as a {@link RegionDestroyedException}.
	 *
	 * This method may ultimately still result in a thrown {@link Exception}, but it will make a best effort to
	 * exhaustively consult all composed {@link RegionResolver RegionResolvers}.
	 *
	 * @param <K> {@link Class type} of the {@link Region} key.
	 * @param <V> {@link Class type} of the {@link Region} value.
	 * @param regionName {@link String name} of the {@link Region} to resolve.
	 * @return the first, resolved reference to a cache {@link Region} identified by the given {@link String name}.
	 * @see RegionResolver#resolve(String)
	 * @see Region
	 * @see Function
	 * @see #getRegionResolverOne()
	 * @see #getRegionResolverTwo()
	 */
	@Nullable @Override
	protected <K, V> Region<K, V> doResolve(@Nullable String regionName) {

		Function<String, Region<K, V>> regionResolverFunction = getRegionResolverTwo()::resolve;

		try {

			Region<K, V> region = getRegionResolverOne().resolve(regionName);

			return region != null ? region : regionResolverFunction.apply(regionName);
		}
		catch (Throwable ignore) {
			return regionResolverFunction.apply(regionName);
		}
	}
}
