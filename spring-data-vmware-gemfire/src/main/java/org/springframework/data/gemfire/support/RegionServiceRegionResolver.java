/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.Optional;
import com.vmware.gemfire.driver.spi.DriverRegion;
import com.vmware.gemfire.driver.spi.DriverConnection;
import org.springframework.data.gemfire.CacheResolver;
import org.springframework.data.gemfire.RegionResolver;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link RegionResolver} implementation used to resolve a cache {@link DriverRegion} identified by {@link String name}
 * using the configured {@link DriverConnection}.
 *
 * @author John Blum
 * @see DriverConnection
 * @see DriverRegion
 * @see CacheResolver
 * @see RegionResolver
 * @see AbstractCachingRegionResolver
 * @since 2.3.0
 */
public class RegionServiceRegionResolver<T extends DriverConnection> extends AbstractCachingRegionResolver {

	/**
	 * Factory method used to construct a {@link RegionServiceRegionResolver} from a {@link CacheResolver}.
	 *
	 * The {@link CacheResolver} supplies a borrowed {@link DriverConnection} capable of resolving a {@link DriverRegion}
	 * identified by {@link String name}.
	 *
	 * @param <S> {@link Class subclass} of {@link DriverConnection}.
	 * @param cacheResolver {@link CacheResolver} used to resolve the {@link DriverConnection}.
	 * @return a new instance of {@link RegionServiceRegionResolver}.
	 * @throws IllegalArgumentException if {@link CacheResolver} is {@literal null}.
	 * @see #RegionServiceRegionResolver(ConnectionResolver)
	 * @see CacheResolver
	 */
	@NonNull
	public static <S extends DriverConnection> RegionServiceRegionResolver<S> from(@NonNull CacheResolver<S> cacheResolver) {

		Assert.notNull(cacheResolver, "CacheResolver must not be null");

		return new RegionServiceRegionResolver<>(() -> Optional.ofNullable(cacheResolver.resolve()));
	}

	/**
	 * Factory method used to construct a {@link RegionServiceRegionResolver} initialized with
	 * the given {@link DriverConnection}.
	 *
	 * @param connection {@link DriverConnection} used to resolve cache {@link DriverRegion Regions}
	 * identified by {@link String name}; may be {@literal null}.
	 * @return a new instance of {@link RegionServiceRegionResolver}.
	 * @see #RegionServiceRegionResolver(ConnectionResolver)
	 * @see DriverConnection
	 */
	@NonNull
	public static RegionServiceRegionResolver<DriverConnection> from(@Nullable DriverConnection connection) {
		return new RegionServiceRegionResolver<>(() -> Optional.ofNullable(connection));
	}

	private final ConnectionResolver<T> resolver;

	/**
	 * Constructs a new instance of {@link RegionServiceRegionResolver} initialized with
	 * the given {@link ConnectionResolver}.
	 *
	 * @param resolver {@link ConnectionResolver} used to resolve the {@link DriverConnection} that is used to resolve
	 * cache {@link DriverRegion Regions} by {@link String name}.
	 * @throws IllegalArgumentException if {@link ConnectionResolver} is {@literal null}.
	 * @see ConnectionResolver
	 */
	public RegionServiceRegionResolver(ConnectionResolver<T> resolver) {

		Assert.notNull(resolver, "ConnectionResolver must not be null");

		this.resolver = resolver;
	}

	/**
	 * Returns the configured {@link ConnectionResolver} used to resolve the {@link DriverConnection} that is then used
	 * to resolve cache {@link DriverRegion Regions} by {@link String name}.
	 *
	 * @return the configured {@link ConnectionResolver}.
	 * @see ConnectionResolver
	 */
	protected ConnectionResolver<T> getConnectionResolver() {
		return this.resolver;
	}

	/**
	 * Resolves a cache {@link DriverRegion} identified by the given {@link String name} using the configured
	 * {@link DriverConnection} resolved from the {@link ConnectionResolver}.
	 *
	 * @param <K> {@link Class type} of the {@link DriverRegion} key.
	 * @param <V> {@link Class type} of the {@link DriverRegion} value.
	 * @param regionName {@link String name} of the {@link DriverRegion} to resolve.
	 * @return the resolved cache {@link DriverRegion} identified by the given {@link String name}; may be {@literal null}.
	 * @see DriverConnection#getRegion(String)
	 * @see #getConnectionResolver()
	 */
	@Nullable @Override
	protected <K, V> DriverRegion<K, V> doResolve(@Nullable String regionName) {

		return getConnectionResolver().resolve()
			.<DriverRegion<K, V>>map(connection -> connection.getRegion(regionName))
			.orElse(null);
	}

	@FunctionalInterface
	protected interface ConnectionResolver<T extends DriverConnection> {
		Optional<T> resolve();
	}
}
