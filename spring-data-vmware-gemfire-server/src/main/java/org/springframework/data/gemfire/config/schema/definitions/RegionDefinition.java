/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.definitions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;

import java.util.Optional;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

/**
 * {@link RegionDefinition} is an Abstract Data Type (ADT) encapsulating the configuration meta-data used to
 * define a new Apache Geode /  Pivotal GemFire cache {@link Region}.
 *
 * @author John Blum
 * @see Region
 * @see RegionShortcut
 * @since 2.0.0
 */
public class RegionDefinition {

	protected static final int ORDER = 1;

	public static final RegionShortcut DEFAULT_REGION_SHORTCUT = RegionShortcut.PARTITION;

	/**
	 * Factory method used to construct a new instance of {@link RegionDefinition} defined from
	 * the given {@link Region}.
	 *
	 * @param region {@link Region} from which the new definition will be defined.
	 * @return a new instance of {@link RegionDefinition} defined from the given {@link Region}.
	 * @throws IllegalArgumentException if {@link Region} is {@literal null}.
	 * @see Region
	 * @see #RegionDefinition(Region)
	 */
	public static RegionDefinition from(Region<?, ?> region) {
		return Optional.ofNullable(region).map(RegionDefinition::new)
			.orElseThrow(() -> newIllegalArgumentException("Region is required"));
	}

	private final transient Region<?, ?> region;

	private RegionShortcut regionShortcut;

	private String name;

	/**
	 * Constructs a new instance of {@link RegionDefinition} defined with the given {@link Region}.
	 *
	 * @param region {@link Region} on which this definition is defined; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link Region} is {@literal null}.
	 * @see Region
	 */
	protected RegionDefinition(Region<?, ?> region) {
		this.region = region;
	}

	/**
	 * Get the order value of this object.
	 *
	 * @return the order value of this object.
	 * @see org.springframework.core.Ordered
	 */
	public int getOrder() {
		return ORDER;
	}

	/**
	 * Returns a reference to the {@link Region} from which this definition is defined.
	 *
	 * @return a reference to the {@link Region} from which this definition is defined.
	 * @see Region
	 */
	protected Region<?, ?> getRegion() {
		return this.region;
	}

	public String getName() {
		return this.name;
	}

	public RegionShortcut getRegionShortcut() {
		return Optional.ofNullable(this.regionShortcut).orElse(DEFAULT_REGION_SHORTCUT);
	}

	public RegionDefinition having(RegionShortcut regionShortcut) {
		this.regionShortcut = regionShortcut;
		return this;
	}

	public RegionDefinition with(String name) {
		this.name = name;
		return this;
	}
}
