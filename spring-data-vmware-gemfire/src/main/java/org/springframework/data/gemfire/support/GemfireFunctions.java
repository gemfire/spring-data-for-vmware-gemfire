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

import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.lang.NonNull;

/**
 * Abstract class defining useful Java {@link Function Functions} for Apache Geode
 *
 * @author John Blum
 * @see Function
 * @see GudClientCache
 * @see GudRegion
 * @since 2.7.0
 */
@SuppressWarnings("unused")
public abstract class GemfireFunctions {

	public static @NonNull <K, V> Function<GudClientCache, GudRegion<K, V>> getRegionFromCache(String regionName) {
		return cache -> cache.getRegion(regionName);
	}

	public static @NonNull <K, V> Supplier<GudRegion<K, V>> getRegionFromCache(@NonNull GudClientCache cache,
			String regionName) {

		return () -> cache.getRegion(regionName);
	}

	public static @NonNull <K, V> Function<GudRegion<?, ?>, GudRegion<K, V>> getSubregionFromRegion(String regionName) {
		return parentRegion -> parentRegion.getSubregion(regionName);
	}
}
