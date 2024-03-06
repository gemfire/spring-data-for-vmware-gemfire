/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin.functions;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeCollection;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.query.Index;

import org.springframework.data.gemfire.function.annotation.GemfireFunction;

/**
 * The ListIndexesFunction class...
 *
 * @author John Blum
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class ListIndexesFunction {

	public static final String LIST_INDEXES_FUNCTION_ID = "ListQqlIndexesFunction";

	@GemfireFunction(id = LIST_INDEXES_FUNCTION_ID)
	public Set<String> listIndexes() {

		return Optional.ofNullable(resolveCache())
			.map(cache -> cache.getQueryService())
			.map(queryService ->
				nullSafeCollection(queryService.getIndexes()).stream().map(Index::getName).collect(Collectors.toSet()))
			.orElseGet(Collections::emptySet);
	}

	protected Cache resolveCache() {
		return CacheFactory.getAnyInstance();
	}
}
