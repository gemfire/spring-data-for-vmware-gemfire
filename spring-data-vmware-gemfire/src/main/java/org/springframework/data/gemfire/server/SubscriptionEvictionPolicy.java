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

package org.springframework.data.gemfire.server;

import org.springframework.data.gemfire.gud.api.GudCacheServer;
import org.springframework.data.gemfire.gud.api.GudClientSubscriptionConfig;

/**
 * Enumeration of the various client subscription policies for {@link GudCacheServer}.
 *
 * @author Costin Leau
 * @author John Blum
 * @since 1.1.0
 */
public enum SubscriptionEvictionPolicy {
	ENTRY,
	MEM,
	NONE;

	public static final SubscriptionEvictionPolicy DEFAULT = SubscriptionEvictionPolicy.valueOfIgnoreCase(
		GudClientSubscriptionConfig.DEFAULT_EVICTION_POLICY);

	/**
	 * Returns the value of the given String name as a SubscriptionEvictionPolicy enum using a case-insensitive,
	 * equality comparison.
	 *
	 * @param name the String name of a SubscriptionEvictionPolicy enumerated value.
	 * @return a SubscriptionEvictionPolicy enumerated value given a String name or null if no enum value
	 * with name was found.
	 * @see SubscriptionEvictionPolicy
	 * @see String#equalsIgnoreCase(String)
	 * @see #values()
	 * @see #name()
	 */
	public static SubscriptionEvictionPolicy valueOfIgnoreCase(final String name) {
		for (SubscriptionEvictionPolicy subscriptionEvictionPolicy : values()) {
			if (subscriptionEvictionPolicy.name().equalsIgnoreCase(name)) {
				return subscriptionEvictionPolicy;
			}
		}

		return null;
	}

	/**
	 * Null-safe utility method for setting the client's subscription eviction policy on the configuration meta-data.
	 *
	 * @param config a GemFire GudClientSubscriptionConfig object holding the configuration setting and meta-data
	 * about the client's subscription configuration.
	 * @return the GudClientSubscriptionConfig object.
	 * @see GudClientSubscriptionConfig#setEvictionPolicy(String)
	 */
	public GudClientSubscriptionConfig setEvictionPolicy(final GudClientSubscriptionConfig config) {
		if (config != null) {
			config.setEvictionPolicy(name().toLowerCase());
		}

		return config;
	}

}
