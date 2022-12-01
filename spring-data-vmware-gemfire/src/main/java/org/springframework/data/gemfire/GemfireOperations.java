/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Query;
import org.apache.geode.cache.query.QueryService;
import org.apache.geode.cache.query.SelectResults;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.gemfire.util.CollectionUtils;

/**
 * {@link GemfireOperations} defines the {{@link Region} data access operations that can be performed
 * using the {@literal Template software design pattern}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Region
 * @see QueryService
 */
public interface GemfireOperations {

	boolean containsKey(Object key);

	boolean containsKeyOnServer(Object key);

	boolean containsValue(Object value);

	boolean containsValueForKey(Object key);

	<K, V> void create(K key, V value);

	<K, V> V get(K key);

	@SuppressWarnings("unchecked")
	default <K, V> Map<K, V> getAll(Collection<?> keys) {

		return CollectionUtils.nullSafeCollection(keys).stream()
			.filter(Objects::nonNull)
			.collect(Collectors.toMap(key -> (K) key, this::get));
	}

	<K, V> V put(K key, V value);

	default <K, V> void putAll(Map<? extends K, ? extends V> map) {
		CollectionUtils.nullSafeMap(map).forEach((key, value) -> put(key, value));
	}

	<K, V> V putIfAbsent(K key, V value);

	<K, V> V remove(K key);

	default void removeAll(Collection<?> keys) {

		CollectionUtils.nullSafeCollection(keys).stream()
			.filter(Objects::nonNull)
			.forEach(this::remove);
	}

	<K, V> V replace(K key, V value);

	<K, V> boolean replace(K key, V oldValue, V newValue);

	/**
	 * Executes a GemFire query with the given (optional) parameters and returns the result. Note this method expects the query to return multiple results; for queries that return only one
	 * element use {@link #findUnique(String, Object...)}.
	 *
	 * As oppose, to the {@link #query(String)} method, this method allows for more generic queries (against multiple regions even) to be executed.
	 *
	 * Note that the local query service is used if the region is configured as a client without any pool configuration or server connectivity - otherwise the query service on the default pool
	 * is being used.
	 *
	 * @param <E> type parameter specifying the type of the select results.
	 * @param query the OQL query statement to execute.
	 * @param params an array of Object values used as arguments to bind to the OQL query parameters (such as $1).
	 * @return A {@link SelectResults} instance holding the objects matching the query
	 * @throws InvalidDataAccessApiUsageException in case the query returns a single result (not a {@link SelectResults}).
	 * @see QueryService#newQuery(String)
	 * @see Query#execute(Object[])
	 * @see SelectResults
	 */
	<E> SelectResults<E> find(String query, Object... params) throws InvalidDataAccessApiUsageException;

	/**
	 * Executes a GemFire query with the given (optional) parameters and returns the result. Note this method expects the query to return a single result; for queries that return multiple
	 * elements use {@link #find(String, Object...)}.
	 *
	 * As oppose, to the {@link #query(String)} method, this method allows for more generic queries (against multiple regions even) to be executed.
	 *
	 * Note that the local query service is used if the region is configured as a client without any pool configuration or server connectivity - otherwise the query service on the default pool
	 * is being used.
	 *
	 * @param <T> type parameter specifying the returned result type.
	 * @param query the OQL query statement to execute.
	 * @param params an array of Object values used as arguments to bind to the OQL query parameters (such as $1).
	 * @return The (single) object that represents the result of the query.
	 * @throws InvalidDataAccessApiUsageException in case the query returns multiple objects (through {@link SelectResults}).
	 * @see QueryService#newQuery(String)
	 * @see Query#execute(Object[])
	 */
	<T> T findUnique(String query, Object... params) throws InvalidDataAccessApiUsageException;

	/**
	 * Shortcut for {@link Region#query(String)} method. Filters the values of this region using the predicate given as a string with the syntax of the WHERE clause of the query language.
	 * The predefined variable this may be used inside the predicate to denote the current element being filtered.
	 * This method evaluates the passed in where clause and returns results. It is supported on servers as well as clients.
	 * When executed on a client, this method always runs on the server and returns results.
	 * When invoking this method from the client, applications can pass in a where clause or a complete query.
	 *
	 * @param <E> type parameter specifying the type of the select results.
	 * @param query an OQL Query language boolean query predicate.
	 * @return A SelectResults containing the values of this Region that match the predicate.
	 * @see Region#query(String)
	 */
	<E> SelectResults<E> query(String query);

	/**
	 * Execute the action specified by the given action object within a Region.
	 *
	 * @param <T> type parameter specifying the returned result type.
	 * @param action callback object that specifies the Gemfire action to execute.
	 * @return a result object returned by the action, or <code>null</code>.
	 * @throws DataAccessException in case of GemFire errors.
	 */
	<T> T execute(GemfireCallback<T> action) throws DataAccessException;

	/**
	 * Execute the action specified by the given action object within a Region.
	 *
	 * @param <T> type parameter specifying the returned result type.
	 * @param action callback object that specifies the Gemfire action to execute.
	 * @param exposeNativeRegion whether to expose the native GemFire region to callback code.
	 * @return a result object returned by the action, or <code>null</code>.
	 * @throws DataAccessException in case of GemFire errors.
	 */
	<T> T execute(GemfireCallback<T> action, boolean exposeNativeRegion) throws DataAccessException;

}
