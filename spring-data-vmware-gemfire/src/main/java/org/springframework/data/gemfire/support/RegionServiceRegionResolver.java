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

package org.springframework.data.gemfire.support;

import java.util.Optional;

import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.RegionResolver;
import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionService;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RegionResolver} implementation used to resolve a cache {@link GudRegion} identified by {@link String name}
 * using the configured {@link GudRegionService}.
 *
 * @author John Blum
 * @see GudClientCache
 * @see GudRegion
 * @see GudRegionService
 * @see GudClientCache
 * @see CacheResolver
 * @see RegionResolver
 * @see AbstractCachingRegionResolver
 * @since 2.3.0
 */
public class RegionServiceRegionResolver<T extends GudRegionService> extends AbstractCachingRegionResolver {

	/**
	 * Factory method used to construct a {@link RegionServiceRegionResolver} from a {@link CacheResolver}.
	 *
	 * The {@link CacheResolver} will resolve an instance of {@link GudClientCache}, such as a {@link GudClientCache},
	 * which is a {@link GudRegionService} capable of resolving a {@link GudRegion}
	 * identified by {@link String name}.
	 *
	 * @param <S> {@link Class subclass} of {@link GudClientCache}.
	 * @param cacheResolver {@link CacheResolver} used to resolve the {@link GudRegionService}.
	 * @return a new instance of {@link RegionServiceRegionResolver}.
	 * @throws IllegalArgumentException if {@link CacheResolver} is {@literal null}.
	 * @see #RegionServiceRegionResolver(RegionServiceResolver)
	 * @see CacheResolver
	 */
	@NonNull
	public static <S extends GudClientCache> RegionServiceRegionResolver<S> from(@NonNull CacheResolver<S> cacheResolver) {

		Assert.notNull(cacheResolver, "CacheResolver must not be null");

		return new RegionServiceRegionResolver<>(() -> Optional.ofNullable(cacheResolver.resolve()));
	}

	/**
	 * Factory method used to construct a {@link RegionServiceRegionResolver} initialized with
	 * the given {@link GudRegionService}.
	 *
	 * The {@link GudRegionService} may be an instance of {@link GudClientCache}, such as a {@link GudClientCache}.
	 *
	 * @param regionService {@link GudRegionService} used to resolve cache {@link GudRegion Regions}
	 * identified by {@link String name}; may be {@literal null}.
	 * @return a new instance of {@link RegionServiceRegionResolver}.
	 * @see #RegionServiceRegionResolver(RegionServiceResolver)
	 * @see GudRegionService
	 */
	@NonNull
	public static RegionServiceRegionResolver<GudRegionService> from(@Nullable GudRegionService regionService) {
		return new RegionServiceRegionResolver<>(() -> Optional.ofNullable(regionService));
	}

	private final RegionServiceResolver<T> resolver;

	/**
	 * Constructs a new instance of {@link RegionServiceRegionResolver} initialized with
	 * the given {@link RegionServiceResolver}.
	 *
	 * @param resolver {@link RegionServiceResolver} used to resolve the {@link GudRegionService} that is used to resolve
	 * cache {@link GudRegion Regions} by {@link String name}.
	 * @throws IllegalArgumentException if {@link RegionServiceResolver} is {@literal null}.
	 * @see RegionServiceResolver
	 */
	public RegionServiceRegionResolver(RegionServiceResolver<T> resolver) {

		Assert.notNull(resolver, "RegionServiceResolver must not be null");

		this.resolver = resolver;
	}

	/**
	 * Returns the configured {@link RegionServiceResolver} used to resolve the {@link GudRegionService} that is then used
	 * to resolve cache {@link GudRegion Regions} by {@link String name}.
	 *
	 * @return the configured {@link RegionServiceResolver}.
	 * @see RegionServiceResolver
	 */
	protected RegionServiceResolver<T> getRegionServiceResolver() {
		return this.resolver;
	}

	/**
	 * Resolves a cache {@link GudRegion} identified by the given {@link String name} using the configured
	 * {@link GudRegionService} resolved from the {@link RegionServiceResolver}.
	 *
	 * @param <K> {@link Class type} of the {@link GudRegion} key.
	 * @param <V> {@link Class type} of the {@link GudRegion} value.
	 * @param regionName {@link String name} of the {@link GudRegion} to resolve.
	 * @return the resolved cache {@link GudRegion} identified by the given {@link String name}; may be {@literal null}.
	 * @see GudRegionService#getRegion(String)
	 * @see #getRegionServiceResolver()
	 */
	@Nullable @Override
	protected <K, V> GudRegion<K, V> doResolve(@Nullable String regionName) {

		return getRegionServiceResolver().resolve()
			.<GudRegion<K, V>>map(regionService -> regionService.getRegion(regionName))
			.orElse(null);
	}

	@FunctionalInterface
	protected interface RegionServiceResolver<T extends GudRegionService> {
		Optional<T> resolve();
	}
}
