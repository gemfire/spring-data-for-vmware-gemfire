/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.admin.functions;

import static org.springframework.data.gemfire.util.CollectionUtils.nullSafeCollection;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.query.QueryException;
import org.apache.geode.cache.query.QueryService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.gemfire.GemfireCacheUtils;
import org.springframework.data.gemfire.config.schema.definitions.IndexDefinition;

/**
 * The CreateIndexFunction class...
 *
 * @author John Blum
 * @since 1.0.0
 */
public class CreateIndexFunction {

	public static final String CREATE_INDEX_FUNCTION_ID = "CreateOqlIndexFunction";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	public boolean createIndex(IndexDefinition indexDefinition) {

		Cache gemfireCache = resolveCache();

		if (isNonExistingIndex(gemfireCache, indexDefinition)) {

			if (logger.isInfoEnabled()) {
				logger.info("Creating Index with name [{}] having expression [{}] on Region [{}] with type [{}]",
					indexDefinition.getName(), indexDefinition.getExpression(), indexDefinition.getFromClause(),
						indexDefinition.getIndexType());
			}

			QueryService queryService = gemfireCache.getQueryService();

			try {
				switch (indexDefinition.getIndexType()) {
					case KEY:
					case PRIMARY_KEY:
						queryService.createKeyIndex(indexDefinition.getName(),
							indexDefinition.getExpression(), indexDefinition.getFromClause());
						return true;
					case HASH:
						queryService.createHashIndex(indexDefinition.getName(),
							indexDefinition.getExpression(), indexDefinition.getFromClause());
						return true;
					case FUNCTIONAL:
						queryService.createIndex(indexDefinition.getName(),
							indexDefinition.getExpression(), indexDefinition.getFromClause());
						return true;
					default:
						return false;
				}
			}
			catch (QueryException cause) {
				throw GemfireCacheUtils.convertGemfireAccessException(cause);
			}
		}
		else {

			if (logger.isInfoEnabled()) {
				logger.info("Index with name [{}] already exists", indexDefinition.getName());
			}

			return false;
		}
	}

	protected Cache resolveCache() {
		return CacheFactory.getAnyInstance();
	}

	protected boolean isNonExistingIndex(GemFireCache gemfireCache, IndexDefinition indexDefinition) {
		return !nullSafeCollection(gemfireCache.getQueryService().getIndexes()).stream()
			.anyMatch(index -> index.getName().equals(indexDefinition.getName()));
	}
}
