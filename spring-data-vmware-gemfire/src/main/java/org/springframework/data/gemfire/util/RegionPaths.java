/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.util.Optional;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * String formatting helpers for region names and paths.
 *
 * <p>These helpers preserve the legacy formatting behavior without requiring a cache or driver.
 * They do not validate or normalize paths.
 */
public final class RegionPaths {

	private RegionPaths() {
	}

	/**
	 * Extracts the portion following the last separator, returning the input when no separator exists.
	 *
	 * <p>For compatibility, the separator index is calculated after trimming whitespace but applied
	 * to the original input. For example, {@code " /a/b "} produces {@code "/b "}.
	 *
	 * @param regionPath the path, possibly {@literal null}
	 * @return the extracted name, or {@literal null} for a null input
	 */
	@Nullable
	public static String toRegionName(@Nullable String regionPath) {

		return Optional.ofNullable(regionPath)
			.filter(StringUtils::hasText)
			.map(StringUtils::trimWhitespace)
			.map(it -> it.lastIndexOf("/"))
			.filter(index -> index > -1)
			.map(index -> regionPath.substring(index + 1))
			.orElse(regionPath);
	}

	/**
	 * Prepends a separator, including when the input already starts with one.
	 *
	 * @param regionName the name, possibly {@literal null}
	 * @return the prefixed input; {@code "/null"} for a null input
	 */
	@NonNull
	public static String toRegionPath(@Nullable String regionName) {
		return String.format("%1$s%2$s", "/", regionName);
	}
}
