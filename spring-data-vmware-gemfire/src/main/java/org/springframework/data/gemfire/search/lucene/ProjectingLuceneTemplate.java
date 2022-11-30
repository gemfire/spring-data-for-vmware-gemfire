// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.search.lucene;

import static org.springframework.data.gemfire.search.lucene.support.LucenePage.newLucenePage;

import java.util.List;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.lucene.LuceneIndex;
import org.apache.geode.cache.lucene.LuceneQueryProvider;

import org.springframework.data.domain.Page;

/**
 * {@link ProjectingLuceneTemplate} is a Lucene data access operations class encapsulating functionality
 * for performing Lucene queries and other Lucene data access operations and returning the query results
 * as application-specific domain object views.
 *
 * @author John Blum
 * @see ProjectingLuceneAccessor
 * @see ProjectingLuceneOperations
 * @see Region
 * @see LuceneIndex
 * @see org.apache.geode.cache.lucene.LuceneQuery
 * @see org.apache.geode.cache.lucene.LuceneQueryFactory
 * @see LuceneQueryProvider
 * @see org.apache.geode.cache.lucene.LuceneResultStruct
 * @see org.apache.geode.cache.lucene.PageableLuceneQueryResults
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public class ProjectingLuceneTemplate extends ProjectingLuceneAccessor {

	/**
	 * Constructs a default, uninitialized instance of the {@link ProjectingLuceneTemplate}.
	 */
	public ProjectingLuceneTemplate() {
	}

	/**
	 * Constructs an instance of the {@link ProjectingLuceneTemplate} initialized with the given {@link LuceneIndex}
	 * used to perform Lucene queries (searches).
	 *
	 * @param luceneIndex {@link LuceneIndex} used in Lucene queries.
	 * @see LuceneIndex
	 */
	public ProjectingLuceneTemplate(LuceneIndex luceneIndex) {
		super(luceneIndex);
	}

	/**
	 * Constructs an instance of the {@link ProjectingLuceneTemplate} initialized with the given Lucene index name
	 * and {@link Region} reference upon which Lucene queries are executed.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex} used in Lucene queries.
	 * @param region {@link Region} on which Lucene queries are executed.
	 * @see Region
	 */
	public ProjectingLuceneTemplate(String indexName, Region<?, ?> region) {
		super(indexName, region);
	}

	/**
	 * Constructs an instance of the {@link ProjectingLuceneTemplate} initialized with the given Lucene index name
	 * and {@link Region} reference upon which Lucene queries are executed.
	 *
	 * @param indexName {@link String} containing the name of the {@link LuceneIndex} used in Lucene queries.
	 * @param regionPath {@link String} containing the name of the {@link Region} on which Lucene queries are executed.
	 */
	public ProjectingLuceneTemplate(String indexName, String regionPath) {
		super(indexName, regionPath);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> List<T> query(String query, String defaultField, int resultLimit, Class<T> projectionType) {
		return project(query(query, defaultField, resultLimit), projectionType);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> Page<T> query(String query, String defaultField, int resultLimit, int pageSize,
			Class<T> projectionType) {

		return newLucenePage(this, query(query, defaultField, resultLimit, pageSize), pageSize, projectionType);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> List<T> query(LuceneQueryProvider queryProvider, int resultLimit, Class<T> projectionType) {
		return project(query(queryProvider, resultLimit), projectionType);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> Page<T> query(LuceneQueryProvider queryProvider, int resultLimit, int pageSize,
			Class<T> projectionType) {

		return newLucenePage(this, query(queryProvider, resultLimit, pageSize), pageSize, projectionType);
	}
}
