/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.springframework.lang.NonNull;

/**
 * Abstract class defining useful Java {@link Function Functions} for Apache Geode
 *
 * @author John Blum
 * @see Function
 * @see ClientCache
 * @see Region
 * @since 2.7.0
 */
@SuppressWarnings("unused")
public abstract class GemfireFunctions {

	public static @NonNull <K, V> Function<ClientCache, Region<K, V>> getRegionFromCache(String regionName) {
		return cache -> cache.getRegion(regionName);
	}

	public static @NonNull <K, V> Supplier<Region<K, V>> getRegionFromCache(@NonNull ClientCache cache,
			String regionName) {

		return () -> cache.getRegion(regionName);
	}

	public static @NonNull <K, V> Function<Region<?, ?>, Region<K, V>> getSubregionFromRegion(String regionName) {
		return parentRegion -> parentRegion.getSubregion(regionName);
	}
}
