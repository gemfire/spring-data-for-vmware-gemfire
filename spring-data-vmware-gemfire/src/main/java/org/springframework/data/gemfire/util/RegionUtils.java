/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.util;

import org.springframework.data.gemfire.client.ClientRegionShortcutWrapper;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudRegionAttributes;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * The {@link RegionUtils} class is an abstract utility class for working with {@link GudRegion Regions}.
 *
 * @author John Blum
 * @see GudRegion
 * @see GudRegionAttributes
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public abstract class RegionUtils extends CacheUtils {

	/**
	 * Assert that the configuration settings for {@link GudClientRegionShortcut} and the {@literal persistent} attribute
	 * in &lt;gfe:*-region&gt; elements are compatible.
	 *
	 * @param clientRegionShortcut {@link GudClientRegionShortcut} resolved from the SDG XML namespace.
	 * @param persistent boolean indicating the value of the {@literal persistent} configuration attribute.
	 * @see ClientRegionShortcutWrapper
	 * @see GudClientRegionShortcut
	 */
	public static void assertClientRegionShortcutAndPersistentAttributeAreCompatible(
			GudClientRegionShortcut clientRegionShortcut, Boolean persistent) {

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
	 * Assert that the configuration settings for {@link org.springframework.data.gemfire.gud.api.GudDataPolicy} and the {@literal persistent} attribute
	 * in &lt;gfe:*-region&gt; elements are compatible.
	 *
	 * @param dataPolicy {@link org.springframework.data.gemfire.gud.api.GudDataPolicy} resolved from the SDG XML namespace.
	 * @param persistent boolean indicating the value of the {@literal persistent} configuration attribute.
	 * @see GudDataPolicy
	 */
	public static void assertDataPolicyAndPersistentAttributeAreCompatible(GudDataPolicy dataPolicy, Boolean persistent) {

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
	 * Safely closes the target {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} to close
	 * @return a boolean indicating whether the {@link GudRegion} was successfully closed or not.
	 * @see GudRegion#close
	 */
	public static boolean close(GudRegion<?, ?> region) {

		try {

			region.close();

			return true;
		}
		catch (Throwable ignore) {
			return false;
		}
	}

	/**
	 * Determines whether the target {@link GudRegion} is a {@literal client} {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} to evaluate.
	 * @return a boolean indicating whether the target {@link GudRegion} is a {@literal client} {@link GudRegion}.
	 * @see GudRegion
	 */
	public static boolean isClient(@Nullable GudRegion<?, ?> region) {

		return Optional.ofNullable(region)
			.map(GudRegion::getAttributes)
			.map(GudRegionAttributes::getPoolName)
			.filter(StringUtils::hasText)
			.isPresent();
	}

	/**
	 * Determines whether the given {@link GudRegion} is closeable.
	 *
	 * @param region {@link GudRegion} to evaluate.
	 * @return a boolean value indicating whether the {@link GudRegion} is closeable or not.
	 * @see GudRegion
	 */
	public static boolean isCloseable(GudRegion<?, ?> region) {

		return Optional.ofNullable(region)
			.map(GudRegion::getRegionService)
			.filter(regionService -> !regionService.isClosed())
			.isPresent();
	}

	/**
	 * Determines whether the given {@link GudRegion} is a non-distributed, {@literal local} {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} to evaluate.
	 * @return a boolean value indicating whether the given {@link GudRegion} is a non-distributed,
	 * {@literal local} {@link GudRegion}.
	 * @see GudRegion
	 */
	public static boolean isLocal(@Nullable GudRegion<?, ?> region) {
		return region != null && region.isLocalRegion();
	}

	@Nullable
	public static String toRegionName(@Nullable GudRegion<?, ?> region) {
		return Optional.ofNullable(region).map(GudRegion::getName).orElse(null);
	}

	@Nullable
	public static String toRegionName(String regionPath) {

		return Optional.ofNullable(regionPath)
			.filter(StringUtils::hasText)
			.map(StringUtils::trimWhitespace)
			.map(it -> it.lastIndexOf(GudRegion.SEPARATOR))
			.filter(index -> index > -1)
			.map(index -> regionPath.substring(index + 1))
			.orElse(regionPath);
	}

	@Nullable
	public static String toRegionPath(@Nullable GudRegion<?, ?> region) {
		return Optional.ofNullable(region).map(GudRegion::getFullPath).orElse(null);
	}

	@NonNull
	public static String toRegionPath(String regionName) {
		return String.format("%1$s%2$s", GudRegion.SEPARATOR, regionName);
	}

	/**
	 * Determines whether the target {@link GudRegion} is a {@literal server-side} {@link GudRegion}.
	 *
	 * @param region {@link GudRegion} to evaluate.
	 * @return a boolean indicating whether the target {@link GudRegion} is a {@literal server-side} {@link GudRegion}.
	 * @see GudRegion
	 */
	public static boolean isServer(@Nullable GudRegion<?, ?> region) {
		return region != null && !isClient(region);
	}
}
