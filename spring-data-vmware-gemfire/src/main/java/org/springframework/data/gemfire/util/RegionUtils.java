/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.util;

import java.util.Optional;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.internal.cache.LocalRegion;

import org.springframework.data.gemfire.client.ClientRegionShortcutWrapper;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * The {@link RegionUtils} class is an abstract utility class for working with {@link Region Regions}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionAttributes
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public abstract class RegionUtils extends CacheUtils {

	/**
	 * Assert that the configuration settings for {@link ClientRegionShortcut} and the {@literal persistent} attribute
	 * in &lt;gfe:*-region&gt; elements are compatible.
	 *
	 * @param clientRegionShortcut {@link ClientRegionShortcut} resolved from the SDG XML namespace.
	 * @param persistent boolean indicating the value of the {@literal persistent} configuration attribute.
	 * @see org.springframework.data.gemfire.client.ClientRegionShortcutWrapper
	 * @see org.apache.geode.cache.client.ClientRegionShortcut
	 */
	public static void assertClientRegionShortcutAndPersistentAttributeAreCompatible(
			ClientRegionShortcut clientRegionShortcut, Boolean persistent) {

		boolean persistentUnspecified = persistent == null;

		if (ClientRegionShortcutWrapper.valueOf(clientRegionShortcut).isPersistent()) {
			Assert.isTrue(persistentUnspecified || Boolean.TRUE.equals(persistent),
				String.format("Client Region Shortcut [%s] is not valid when persistent is false", clientRegionShortcut));
		}
		else {
			Assert.isTrue(persistentUnspecified || Boolean.FALSE.equals(persistent),
				String.format("Client Region Shortcut [%s] is not valid when persistent is true", clientRegionShortcut));
		}
	}

	/**
	 * Assert that the configuration settings for {@link DataPolicy} and the {@literal persistent} attribute
	 * in &lt;gfe:*-region&gt; elements are compatible.
	 *
	 * @param dataPolicy {@link DataPolicy} resolved from the SDG XML namespace.
	 * @param persistent boolean indicating the value of the {@literal persistent} configuration attribute.
	 * @see org.apache.geode.cache.DataPolicy
	 */
	public static void assertDataPolicyAndPersistentAttributeAreCompatible(DataPolicy dataPolicy, Boolean persistent) {

		boolean persistentUnspecified = persistent == null;

		if (dataPolicy.withPersistence()) {
			Assert.isTrue(persistentUnspecified || Boolean.TRUE.equals(persistent),
				String.format("Data Policy [%s] is not valid when persistent is false", dataPolicy));
		}
		else {
			Assert.isTrue(persistentUnspecified || Boolean.FALSE.equals(persistent),
				String.format("Data Policy [%s] is not valid when persistent is true", dataPolicy));
		}
	}

	/**
	 * Safely closes the target {@link Region}.
	 *
	 * @param region {@link Region} to close
	 * @return a boolean indicating whether the {@link Region} was successfully closed or not.
	 * @see org.apache.geode.cache.Region#close
	 */
	public static boolean close(Region<?, ?> region) {

		try {

			region.close();

			return true;
		}
		catch (Throwable ignore) {
			return false;
		}
	}

	/**
	 * Determines whether the target {@link Region} is a {@literal client} {@link Region}.
	 *
	 * @param region {@link Region} to evaluate.
	 * @return a boolean indicating whether the target {@link Region} is a {@literal client} {@link Region}.
	 * @see org.apache.geode.cache.Region
	 */
	public static boolean isClient(@Nullable Region<?, ?> region) {

		return Optional.ofNullable(region)
			.map(Region::getAttributes)
			.map(RegionAttributes::getPoolName)
			.filter(StringUtils::hasText)
			.isPresent();
	}

	/**
	 * Determines whether the given {@link Region} is closeable.
	 *
	 * @param region {@link Region} to evaluate.
	 * @return a boolean value indicating whether the {@link Region} is closeable or not.
	 * @see org.apache.geode.cache.Region
	 */
	public static boolean isCloseable(Region<?, ?> region) {

		return Optional.ofNullable(region)
			.map(Region::getRegionService)
			.filter(regionService -> !regionService.isClosed())
			.isPresent();
	}

	/**
	 * Determines whether the given {@link Region} is a non-distributed, {@literal local} {@link Region}.
	 *
	 * @param region {@link Region} to evaluate.
	 * @return a boolean value indicating whether the given {@link Region} is a non-distributed,
	 * {@literal local} {@link Region}.
	 * @see org.apache.geode.cache.Region
	 */
	public static boolean isLocal(@Nullable Region<?, ?> region) {
		return region instanceof LocalRegion;
	}

	@Nullable
	public static String toRegionName(@Nullable Region<?, ?> region) {
		return Optional.ofNullable(region).map(Region::getName).orElse(null);
	}

	@Nullable
	public static String toRegionName(String regionPath) {

		return Optional.ofNullable(regionPath)
			.filter(StringUtils::hasText)
			.map(StringUtils::trimWhitespace)
			.map(it -> it.lastIndexOf(Region.SEPARATOR))
			.filter(index -> index > -1)
			.map(index -> regionPath.substring(index + 1))
			.orElse(regionPath);
	}

	@Nullable
	public static String toRegionPath(@Nullable Region<?, ?> region) {
		return Optional.ofNullable(region).map(Region::getFullPath).orElse(null);
	}

	@NonNull
	public static String toRegionPath(String regionName) {
		return String.format("%1$s%2$s", Region.SEPARATOR, regionName);
	}

	/**
	 * Determines whether the target {@link Region} is a {@literal server-side} {@link Region}.
	 *
	 * @param region {@link Region} to evaluate.
	 * @return a boolean indicating whether the target {@link Region} is a {@literal server-side} {@link Region}.
	 * @see org.apache.geode.cache.Region
	 */
	public static boolean isServer(@Nullable Region<?, ?> region) {
		return region != null && !isClient(region);
	}
}
