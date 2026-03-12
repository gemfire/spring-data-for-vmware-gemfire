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

package org.springframework.data.gemfire.mapping;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.gemfire.gud.api.GudRegion;

import org.springframework.data.mapping.context.MappingContext;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * Simple value object to abstract access to {@link GudRegion Regions} by {@link String name} and mapped {@link Class type}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see Iterable
 * @see GudRegion
 */
public class Regions implements Iterable<GudRegion<?, ?>> {

	private final Map<String, GudRegion<?, ?>> regions;

	private final MappingContext<? extends GemfirePersistentEntity<?>, ?> mappingContext;

	/**
	 * Constructs a new instance of the {@link Regions} wrapper for the given {@link GudRegion Regions}
	 * and {@link MappingContext}.
	 *
	 * @param regions {@link Iterable} of cache {@link GudRegion Regions}; must not be {@literal null}.
	 * @param mappingContext Spring Data {@link MappingContext} used for data mapping; must not be {@literal null}.
	 * @see MappingContext
	 * @see GudRegion
	 * @see Iterable
	 */
	public Regions(@NonNull Iterable<GudRegion<?, ?>> regions,
			@NonNull MappingContext<? extends GemfirePersistentEntity<?>, ?> mappingContext) {

		Assert.notNull(regions, "Regions must not be null");
		Assert.notNull(mappingContext, "MappingContext must not be null");

		Map<String, GudRegion<?, ?>> regionMap = new HashMap<>();

		for (GudRegion<?, ?> region : regions) {
			regionMap.put(region.getName(), region);
			regionMap.put(region.getFullPath(), region);
		}

		this.regions = Collections.unmodifiableMap(regionMap);
		this.mappingContext = mappingContext;
	}

	/**
	 * Returns the {@link GudRegion} the given type is mapped to. Will try to find
	 * a {@link GudRegion} with the simple class name in case no mapping
	 * information is found.
	 *
	 * @param <T> the Region value class type.
	 * @param entityType must not be {@literal null}.
	 * @return the {@link GudRegion} the given type is mapped to.
	 */
	@SuppressWarnings("unchecked")
	public <T> GudRegion<?, T> getRegion(Class<T> entityType) {

		Assert.notNull(entityType, "Entity type must not be null");

		String regionName = Optional.ofNullable(this.mappingContext.getPersistentEntity(entityType))
			.map(entity -> entity.getRegionName())
			.orElseGet(entityType::getSimpleName);

		return (GudRegion<?, T>) this.regions.get(regionName);
	}

	/**
	 * Returns the {@link GudRegion} with the given name or path.
	 *
	 * @param <S> the Region key class type.
	 * @param <T> the Region value class type.
	 * @param namePath must not be {@literal null}, and either identifies the Region by name or the fully-qualified path.
	 * @return the {@link GudRegion} with the given name or path.
	 */
	@SuppressWarnings("unchecked")
	public <S, T> GudRegion<S, T> getRegion(String namePath) {

		Assert.hasText(namePath, "Region name/path is required");

		return (GudRegion<S, T>) this.regions.get(namePath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<GudRegion<?, ?>> iterator() {
		return this.regions.values().iterator();
	}
}
