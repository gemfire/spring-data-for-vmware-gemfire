/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.client.support;

import java.util.Arrays;

import org.springframework.data.gemfire.client.PoolResolver;
import org.springframework.data.gemfire.gud.api.GudPool;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Composite of {@link PoolResolver PoolResolvers} functioning as a single {@link PoolResolver}.
 *
 * @author John Blum
 * @see GudPool
 * @see PoolResolver
 * @since 2.3.0
 */
public class ComposablePoolResolver implements PoolResolver {

	/**
	 * Null-safe factory method to compose an array of {@link PoolResolver} objects.
	 *
	 * Preserves order in the composition.
	 *
	 * @param poolResolvers array of {@link PoolResolver} objects to compose.
	 * @return a composition from the array of {@link PoolResolver} objects; may return {@literal null}.
	 * @see PoolResolver
	 * @see #compose(Iterable)
	 */
	public static @Nullable PoolResolver compose(@Nullable PoolResolver... poolResolvers) {
		return compose(Arrays.asList(ArrayUtils.nullSafeArray(poolResolvers, PoolResolver.class)));
	}

	/**
	 * Null-safe factory method to compose an {@link Iterable} of {@link PoolResolver} objects.
	 *
	 * Preserves order in the composition if the {@link Iterable} collection-like data structure is ordered,
	 * like a {@link java.util.List}).
	 *
	 * @param poolResolvers {@link Iterable} of {@link PoolResolver} objects to compose.
	 * @return a composition from the {@link Iterable} of {@link PoolResolver} objects; may return {@literal null}.
	 * @see PoolResolver
	 * @see Iterable
	 * @see #compose(PoolResolver, PoolResolver)
	 */
	public static @Nullable PoolResolver compose(@Nullable Iterable<PoolResolver> poolResolvers) {

		PoolResolver current = null;

		for (PoolResolver poolResolver : CollectionUtils.nullSafeIterable(poolResolvers)) {
			current = compose(current, poolResolver);
		}

		return current;
	}

	/**
	 * Null-safe factory method to compose two {@link PoolResolver} objects in a composition.
	 *
	 * @param one first {@link PoolResolver} in the composition.
	 * @param two second {@link PoolResolver} in the composition.
	 * @return a composition from the two {@link PoolResolver} objects.  Returns the first {@link PoolResolver}
	 * if the second {@link PoolResolver} is {@literal null}.  Returns the second {@link PoolResolver} if the first
	 * {@link PoolResolver} is {@literal null}.  Returns {@literal null} if both {@link PoolResolver} arguments
	 * are {@literal null}.
	 * @see PoolResolver
	 * @see #ComposablePoolResolver(PoolResolver, PoolResolver)
	 */
	public static @Nullable PoolResolver compose(@Nullable PoolResolver one, @Nullable PoolResolver two) {
		return one == null ? two : two == null ? one : new ComposablePoolResolver(one, two);
	}

	private final PoolResolver poolResolverOne;
	private final PoolResolver poolResolverTwo;

	/**
	 * Constructs a new instance of {@link ComposablePoolResolver} initialized and composed of two {@link PoolResolver}
	 * implementations that will function as one.
	 *
	 * @param poolResolverOne first {@link PoolResolver} in the composition order.
	 * @param poolResolverTwo second {@link PoolResolver} in the composition order.
	 * @throws IllegalArgumentException if either the first or second {@link PoolResolver} are {@literal null}.
	 * @see PoolResolver
	 */
	protected ComposablePoolResolver(PoolResolver poolResolverOne, PoolResolver poolResolverTwo) {

		Assert.notNull(poolResolverOne, "PoolResolver 1 must not be null");
		Assert.notNull(poolResolverTwo, "PoolResolver 2 must not be null");

		this.poolResolverOne = poolResolverOne;
		this.poolResolverTwo = poolResolverTwo;
	}

	/**
	 * Returns a reference to the first {@link PoolResolver} in the composition.
	 *
	 * @return a reference to the first {@link PoolResolver} in the composition.
	 * @see PoolResolver
	 */
	protected @NonNull PoolResolver getPoolResolverOne() {
		return this.poolResolverOne;
	}

	/**
	 * Returns a reference to the second {@link PoolResolver} in the composition.
	 *
	 * @return a reference to the second {@link PoolResolver} in the composition.
	 * @see PoolResolver
	 */
	protected @NonNull PoolResolver getPoolResolverTwo() {
		return this.poolResolverTwo;
	}

	/**
	 * Attempts to resolve a {@link GudPool} with the given {@link String name} by delegating to the composed
	 * {@link PoolResolver} objects.
	 *
	 * The first {@link PoolResolver} in the composition to resolve a {@link GudPool} with the given {@link String name}
	 * stops the resolution process and returns the target {@link GudPool}.  If no {@link GudPool} with the given
	 * {@link String name} can be resolved by any {@link PoolResolver} in the composition, then {@literal null}
	 * will be returned.
	 *
	 * @param poolName {@link String name} of the {@link GudPool} to resolve.
	 * @return the resolved {@link GudPool} or {@literal null} if a {@link GudPool} with {@link String name}
	 * cannot be resolved.
	 * @see GudPool
	 * @see #getPoolResolverOne()
	 * @see #getPoolResolverTwo()
	 */
	@Nullable @Override
	public GudPool resolve(@Nullable String poolName) {

		GudPool pool = getPoolResolverOne().resolve(poolName);

		return pool != null ? pool : getPoolResolverTwo().resolve(poolName);
	}
}
