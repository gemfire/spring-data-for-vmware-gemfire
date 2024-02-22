/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeSet;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.util.RegionUtils;

/**
 * The {@link ClientRegionCollector} class is an extension of the {@link RegionCollector} which applies additional
 * filtering to find only client {@link Region Regions} in a given context.
 *
 * @author John Blum
 * @see GemFireCache
 * @see Region
 * @see ApplicationContext
 * @see org.springframework.data.gemfire.config.schema.SchemaObjectCollector
 * @since 2.0.0
 */
public class ClientRegionCollector extends RegionCollector {

	@Override
	public Set<Region> collectFrom(ApplicationContext applicationContext) {
		return onlyClientRegions(super.collectFrom(applicationContext));
	}

	@Override
	public Set<Region> collectFrom(GemFireCache gemfireCache) {
		return onlyClientRegions(super.collectFrom(gemfireCache));
	}

	private Set<Region> onlyClientRegions(Set<Region> regions) {
		return nullSafeSet(regions).stream().filter(RegionUtils::isClient).collect(Collectors.toSet());
	}
}
