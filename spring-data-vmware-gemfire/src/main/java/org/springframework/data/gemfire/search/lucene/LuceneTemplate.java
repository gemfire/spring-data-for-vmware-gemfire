/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene;

import java.util.Collection;
import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQuery;
import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;

import org.springframework.data.gemfire.search.lucene.support.LuceneAccessorSupport;

/**
 * {@link LuceneTemplate} is a Lucene data access operations class encapsulating functionality
 * for performing Lucene queries and other Lucene data access operations.
 *
 * @author John Blum
 * @see LuceneAccessor
 * @see LuceneOperations
 * @see LuceneAccessorSupport
 * @see Region
 * @see LuceneIndex
 * @see LuceneQuery
 * @see LuceneQueryFactory
 * @see LuceneQueryProvider
 * @see LuceneResultStruct
 * @see PageableLuceneQueryResults
 * @since 1.1.0
 * @deprecated To be removed in GemFire 10 integration
 */
@SuppressWarnings("unused")
public class LuceneTemplate extends LuceneAccessorSupport implements LuceneOperations {

	/**
	 * Constructs an uninitialized instance of {@link LuceneTemplate}.
	 */
	public LuceneTemplate() {
	}

	/**
	 * Constructs an instance of {@link LuceneTemplate} initialized with the given {@link LuceneIndex}.
	 *
	 * @param luceneIndex {@link LuceneIndex} used in Lucene queries.
	 * @see LuceneIndex
	 */
	public LuceneTemplate(LuceneIndex luceneIndex) {
		super(luceneIndex);
	}

	/**
	 * Constructs an instance of {@link LuceneTemplate} initialized with the given Lucene {@link String index name}
	 * and {@link Region}.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex}.
	 * @param region {@link Region} on which the Lucene query is executed.
	 * @see Region
	 */
	public LuceneTemplate(String indexName, Region<?, ?> region) {
		super(indexName, region);
	}

	/**
	 * Constructs an instance of {@link LuceneTemplate} initialized with the given Lucene {@link String index name}
	 * and {@link String fully-qualified Region path}.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex}.
	 * @param regionPath {@link String} containing the fully-qualified path of the {@link Region}.
	 */
	public LuceneTemplate(String indexName, String regionPath) {
		super(indexName, regionPath);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> List<LuceneResultStruct<K, V>> query(String query, String defaultField, int resultLimit) {
		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit);

		LuceneQuery<K, V> queryWrapper = queryFactory.create(indexName, regionPath, query, defaultField);

		return doFind(queryWrapper::findResults, query, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> PageableLuceneQueryResults<K, V> query(String query, String defaultField,
			int resultLimit, int pageSize) {

		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit, pageSize);

		LuceneQuery<K, V> queryWrapper = queryFactory.create(indexName, regionPath, query, defaultField);

		return doFind(queryWrapper::findPages, query, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> List<LuceneResultStruct<K, V>> query(LuceneQueryProvider queryProvider, int resultLimit) {
		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit);

		LuceneQuery<K, V> queryWrapper = queryFactory.create(indexName, regionPath, queryProvider);

		return doFind(queryWrapper::findResults, queryProvider, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> PageableLuceneQueryResults<K, V> query(LuceneQueryProvider queryProvider,
			int resultLimit, int pageSize) {

		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit, pageSize);

		LuceneQuery<K, V> queryWrapper = queryFactory.create(indexName, regionPath, queryProvider);

		return doFind(queryWrapper::findPages, queryProvider, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K> Collection<K> queryForKeys(String query, String defaultField, int resultLimit) {
		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit);

		LuceneQuery<K, ?> queryWrapper = queryFactory.create(indexName, regionPath, query, defaultField);

		return doFind(queryWrapper::findKeys, query, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K> Collection<K> queryForKeys(LuceneQueryProvider queryProvider, int resultLimit) {
		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit);

		LuceneQuery<K, ?> queryWrapper = queryFactory.create(indexName, regionPath, queryProvider);

		return doFind(queryWrapper::findKeys, queryProvider, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <V> Collection<V> queryForValues(String query, String defaultField, int resultLimit) {
		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit);

		LuceneQuery<?, V> queryWrapper = queryFactory.create(indexName, regionPath, query, defaultField);

		return doFind(queryWrapper::findValues, query, regionPath, indexName);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <V> Collection<V> queryForValues(LuceneQueryProvider queryProvider, int resultLimit) {
		String indexName = resolveIndexName();
		String regionPath = resolveRegionPath();

		LuceneQueryFactory queryFactory = createLuceneQueryFactory(resultLimit);

		LuceneQuery<?, V> queryWrapper = queryFactory.create(indexName, regionPath, queryProvider);

		return doFind(queryWrapper::findValues, queryProvider, regionPath, indexName);
	}
}
