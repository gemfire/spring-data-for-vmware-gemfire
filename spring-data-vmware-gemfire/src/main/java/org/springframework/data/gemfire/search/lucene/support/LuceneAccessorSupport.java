/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;

import java.util.Collection;
import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;

import org.springframework.data.gemfire.search.lucene.LuceneAccessor;
import org.springframework.data.gemfire.util.RuntimeExceptionFactory;

/**
 * {@link LuceneAccessorSupport} is a {@link LuceneAccessor} class implementation providing support
 * for extending classes.
 *
 * @author John Blum
 * @see LuceneAccessor
 * @since 1.1.0
 * @deprecated To be removed in GemFire 10 integration
 */
@SuppressWarnings("unused")
public abstract class LuceneAccessorSupport extends LuceneAccessor {

	/**
	 * Constructs an uninitialized instance of {@link LuceneAccessorSupport}.
	 */
	@SuppressWarnings("all")
	public LuceneAccessorSupport() {
	}

	/**
	 * Constructs an instance of {@link LuceneAccessorSupport} initialized with the given {@link LuceneIndex}.
	 *
	 * @param luceneIndex {@link LuceneIndex} used in Lucene queries.
	 * @see LuceneIndex
	 */
	public LuceneAccessorSupport(LuceneIndex luceneIndex) {
		super(luceneIndex);
	}

	/**
	 * Constructs an instance of {@link LuceneAccessorSupport} initialized with the given Lucene
	 * {@link String index name} and {@link Region}.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex}.
	 * @param region {@link Region} on which the Lucene query is executed.
	 * @see Region
	 */
	public LuceneAccessorSupport(String indexName, Region<?, ?> region) {
		super(indexName, region);
	}

	/**
	 * Constructs an instance of {@link LuceneAccessorSupport} initialized with the given Lucene
	 * {@link String index name} and {@link String fully-qualified Region path}.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex}.
	 * @param regionPath {@link String} containing the fully-qualified path of the {@link Region}.
	 */
	public LuceneAccessorSupport(String indexName, String regionPath) {
		super(indexName, regionPath);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K, V> List<LuceneResultStruct<K, V>> query(String query, String defaultField, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K, V> PageableLuceneQueryResults<K, V> query(String query, String defaultField,
			int resultLimit, int pageSize) {

		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K, V> List<LuceneResultStruct<K, V>> query(LuceneQueryProvider queryProvider, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K, V> PageableLuceneQueryResults<K, V> query(LuceneQueryProvider queryProvider,
			int resultLimit, int pageSize) {

		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K> Collection<K> queryForKeys(String query, String defaultField, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <K> Collection<K> queryForKeys(LuceneQueryProvider queryProvider, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <V> Collection<V> queryForValues(String query, String defaultField, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <V> Collection<V> queryForValues(LuceneQueryProvider queryProvider, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}
}
