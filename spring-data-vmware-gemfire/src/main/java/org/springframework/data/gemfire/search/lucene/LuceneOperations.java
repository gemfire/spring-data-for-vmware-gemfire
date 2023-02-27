/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene;

import java.util.Collection;
import java.util.List;

import org.apache.geode.cache.lucene.LuceneQueryFactory;
import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;

/**
 * The {@link LuceneOperations} interface defines a contract for implementations defining data access operations
 * using Lucene queries.
 *
 * @author John Blum
 * @see LuceneQueryFactory
 * @see LuceneQueryProvider
 * @see LuceneResultStruct
 * @see PageableLuceneQueryResults
 * @since 1.1.0
 * @deprecated To be removed in GemFire 10 integration
 */
@SuppressWarnings("unused")
public interface LuceneOperations {

	int DEFAULT_PAGE_SIZE = LuceneQueryFactory.DEFAULT_PAGESIZE;
	int DEFAULT_RESULT_LIMIT = LuceneQueryFactory.DEFAULT_LIMIT;

	/**
	 * Executes the given Lucene {@link String query}.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param <V> {@link Class} type of the value.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @return a {@link List} of {@link LuceneResultStruct} containing the query results.
	 * @see LuceneResultStruct
	 * @see #query(String, String, int)
	 * @see List
	 */
	default <K, V> List<LuceneResultStruct<K, V>> query(String query, String defaultField) {
		return query(query, defaultField, DEFAULT_RESULT_LIMIT);
	}

	/**
	 * Executes the given Lucene {@link String query} with a limit on the number of results returned.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param <V> {@link Class} type of the value.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @param resultLimit limit on the number of query results to return.
	 * @return a {@link List} of {@link LuceneResultStruct} containing the query results.
	 * @see LuceneResultStruct
	 * @see List
	 */
	<K, V> List<LuceneResultStruct<K, V>> query(String query, String defaultField, int resultLimit);

	/**
	 * Executes the given Lucene {@link String query} with a limit on the number of results returned
	 * along with a page size for paging.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param <V> {@link Class} type of the value.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @param resultLimit limit on the number of query results to return.
	 * @param pageSize number of results per page.
	 * @return a {@link PageableLuceneQueryResults} data structure containing the results of the Lucene query.
	 * @see PageableLuceneQueryResults
	 */
	<K, V> PageableLuceneQueryResults<K, V> query(String query, String defaultField, int resultLimit, int pageSize);

	/**
	 * Executes the given Lucene {@link String query}.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param <V> {@link Class} type of the value.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @return a {@link List} of {@link LuceneResultStruct} containing the query results.
	 * @see LuceneQueryProvider
	 * @see LuceneResultStruct
	 * @see #query(LuceneQueryProvider, int)
	 * @see List
	 */
	default <K, V> List<LuceneResultStruct<K, V>> query(LuceneQueryProvider queryProvider) {
		return query(queryProvider, DEFAULT_RESULT_LIMIT);
	}

	/**
	 * Executes the given Lucene {@link String query} with a limit on the number of results returned.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param <V> {@link Class} type of the value.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @param resultLimit limit on the number of query results to return.
	 * @return a {@link List} of {@link LuceneResultStruct} containing the query results.
	 * @see LuceneQueryProvider
	 * @see LuceneResultStruct
	 * @see List
	 */
	<K, V> List<LuceneResultStruct<K, V>> query(LuceneQueryProvider queryProvider, int resultLimit);

	/**
	 * Executes the given Lucene {@link String query} with a limit on the number of results returned
	 * along with a page size for paging.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param <V> {@link Class} type of the value.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @param resultLimit limit on the number of query results to return.
	 * @param pageSize number of results per page.
	 * @return a {@link PageableLuceneQueryResults} data structure containing the results of the Lucene query.
	 * @see LuceneQueryProvider
	 * @see PageableLuceneQueryResults
	 */
	<K, V> PageableLuceneQueryResults<K, V> query(LuceneQueryProvider queryProvider, int resultLimit, int pageSize);

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of keys
	 * matching the query clause/predicate.
	 *
	 * The number of keys returned is limited by {@link LuceneQueryFactory#DEFAULT_LIMIT}.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @return a {@link Collection} of keys matching the Lucene query clause (predicate).
	 * @see #queryForKeys(String, String, int)
	 * @see Collection
	 */
	default <K> Collection<K> queryForKeys(String query, String defaultField) {
		return queryForKeys(query, defaultField, DEFAULT_RESULT_LIMIT);
	}

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of keys
	 * matching the query clause/predicate with a limit on the number of keys returned.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @param resultLimit limit on the number of keys returned.
	 * @return a {@link Collection} of keys matching the Lucene query clause (predicate).
	 * @see #queryForKeys(String, String, int)
	 * @see Collection
	 */
	<K> Collection<K> queryForKeys(String query, String defaultField, int resultLimit);

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of keys
	 * matching the query clause/predicate.
	 *
	 * The number of keys returned is limited by {@link LuceneQueryFactory#DEFAULT_LIMIT}.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @return a {@link Collection} of keys matching the Lucene query clause (predicate).
	 * @see LuceneQueryProvider
	 * @see #queryForKeys(String, String, int)
	 * @see Collection
	 */
	default <K> Collection<K> queryForKeys(LuceneQueryProvider queryProvider) {
		return queryForKeys(queryProvider, DEFAULT_RESULT_LIMIT);
	}

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of keys
	 * matching the query clause/predicate with a limit on the number of keys returned.
	 *
	 * @param <K> {@link Class} type of the key.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @param resultLimit limit on the number of keys returned.
	 * @return a {@link Collection} of keys matching the Lucene query clause (predicate).
	 * @see LuceneQueryProvider
	 * @see #queryForKeys(String, String, int)
	 * @see Collection
	 */
	<K> Collection<K> queryForKeys(LuceneQueryProvider queryProvider, int resultLimit);

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of values
	 * matching the query clause/predicate.
	 *
	 * The number of values returned is limited by {@link LuceneQueryFactory#DEFAULT_LIMIT}.
	 *
	 * @param <V> {@link Class} type of the value.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @return a {@link Collection} of values matching Lucene query clause (predicate).
	 * @see #queryForValues(String, String, int)
	 * @see Collection
	 */
	default <V> Collection<V> queryForValues(String query, String defaultField) {
		return queryForValues(query, defaultField, DEFAULT_RESULT_LIMIT);
	}

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of values
	 * matching the query clause/predicate.
	 *
	 * @param <V> {@link Class} type of the value.
	 * @param query {@link String} containing the Lucene query to execute.
	 * @param defaultField {@link String} specifying the default field used in Lucene queries when a field
	 * is not explicitly defined in the Lucene query clause.
	 * @param resultLimit limit on the number of values returned.
	 * @return a {@link Collection} of values matching Lucene query clause (predicate).
	 * @see #queryForValues(String, String, int)
	 * @see Collection
	 */
	<V> Collection<V> queryForValues(String query, String defaultField, int resultLimit);

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of values
	 * matching the query clause/predicate.
	 *
	 * The number of values returned is limited by {@link LuceneQueryFactory#DEFAULT_LIMIT}.
	 *
	 * @param <V> {@link Class} type of the value.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @return a {@link Collection} of values matching Lucene query clause (predicate).
	 * @see LuceneQueryProvider
	 * @see #queryForValues(String, String, int)
	 * @see Collection
	 */
	default <V> Collection<V> queryForValues(LuceneQueryProvider queryProvider) {
		return queryForValues(queryProvider, DEFAULT_RESULT_LIMIT);
	}

	/**
	 * Executes the given Lucene {@link String query} returning a {@link Collection} of values
	 * matching the query clause/predicate.
	 *
	 * @param <V> {@link Class} type of the value.
	 * @param queryProvider {@link LuceneQueryProvider} is a provider implementation supplying the Lucene query
	 * to execute as well as de/serialize to distribute across the cluster.
	 * @param resultLimit limit on the number of values returned.
	 * @return a {@link Collection} of values matching Lucene query clause (predicate).
	 * @see LuceneQueryProvider
	 * @see #queryForValues(String, String, int)
	 * @see Collection
	 */
	<V> Collection<V> queryForValues(LuceneQueryProvider queryProvider, int resultLimit);

}
