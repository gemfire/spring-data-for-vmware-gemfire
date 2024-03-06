/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static org.springframework.data.gemfire.util.CollectionUtils.asSet;

import java.util.Optional;
import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionShortcut;

import org.springframework.data.gemfire.config.schema.SchemaObjectDefiner;
import org.springframework.data.gemfire.config.schema.SchemaObjectType;
import org.springframework.data.gemfire.config.schema.definitions.RegionDefinition;
import org.springframework.data.gemfire.util.RegionUtils;

/**
 * The {@link {RegionDefiner} class is responsible for defining a {@link Region}
 * given a reference to a {@link Region} instance.
 *
 * @author John Blum
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.RegionAttributes
 * @see org.apache.geode.cache.RegionShortcut
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectDefiner
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectType
 * @see org.springframework.data.gemfire.config.schema.definitions.RegionDefinition
 * @since 2.0.0
 */
public class RegionDefiner implements SchemaObjectDefiner {

	private final RegionShortcut regionShortcut;

	public RegionDefiner() {
		this(RegionDefinition.DEFAULT_REGION_SHORTCUT);
	}

	public RegionDefiner(RegionShortcut regionShortcut) {
		this.regionShortcut = regionShortcut;
	}

	protected RegionShortcut getRegionShortcut() {
		return Optional.ofNullable(this.regionShortcut).orElse(RegionDefinition.DEFAULT_REGION_SHORTCUT);
	}

	@Override
	public Set<SchemaObjectType> getSchemaObjectTypes() {
		return asSet(SchemaObjectType.REGION);
	}

	@Override
	public Optional<RegionDefinition> define(Object schemaObject) {

		return Optional.ofNullable(schemaObject)
			.filter(this::canDefine)
			.map(it -> (Region) it)
			.filter(RegionUtils::isClient)
			.map(it -> RegionDefinition.from(it).having(getRegionShortcut()));
	}
}
