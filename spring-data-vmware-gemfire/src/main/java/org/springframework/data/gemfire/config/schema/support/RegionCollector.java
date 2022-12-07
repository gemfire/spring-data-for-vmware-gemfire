/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.config.schema.SchemaObjectCollector;

/**
 * The {@link RegionCollector} class is an implementation of the {@link SchemaObjectCollector} that is capable of
 * inspecting a context and finding all {@link Region} schema object instances that have been declared in that context.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectCollector
 * @since 2.0.0
 */
public class RegionCollector implements SchemaObjectCollector<Region> {

	@Override
	public Set<Region> collectFrom(ApplicationContext applicationContext) {
		return applicationContext.getBeansOfType(Region.class).values().stream().collect(Collectors.toSet());
	}

	@Override
	public Set<Region> collectFrom(GemFireCache gemfireCache) {
		return gemfireCache.rootRegions().stream().collect(Collectors.toSet());
	}
}
