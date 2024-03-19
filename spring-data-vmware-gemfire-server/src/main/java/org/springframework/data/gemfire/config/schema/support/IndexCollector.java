/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.support;

import java.util.Set;
import java.util.stream.Collectors;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.query.Index;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.config.schema.SchemaObjectCollector;

/**
 * The {@link IndexCollector} class is an implementation of the {@link SchemaObjectCollector} that is capable of
 * inspecting a context and finding all {@link Index} schema object instances that have been declared in that context.
 *
 * @author John Blum
 * @see GemFireCache
 * @see Index
 * @see ApplicationContext
 * @see SchemaObjectCollector
 * @since 2.0.0
 */
public class IndexCollector implements SchemaObjectCollector<Index> {

	@Override
	public Set<Index> collectFrom(ApplicationContext applicationContext) {
		return applicationContext.getBeansOfType(Index.class).values().stream().collect(Collectors.toSet());
	}

	@Override
	public Set<Index> collectFrom(GemFireCache gemfireCache) {
		return gemfireCache.getQueryService().getIndexes().stream().collect(Collectors.toSet());
	}
}
