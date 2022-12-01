/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query;

import org.apache.geode.cache.Region;

import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.gemfire.repository.query.support.OqlKeyword;
import org.springframework.data.repository.query.parser.PartTree;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * The QueryBuilder class is used to build a {@link QueryString}.
 *
 * @author Oliver Gierke
 * @author David Turanski
 * @author John Blum
 * @see QueryString
 */
class QueryBuilder {

	static final String DEFAULT_ALIAS = "x";
	static final String SELECT_OQL_TEMPLATE = "SELECT %1$s * FROM /%2$s %3$s";
	static final String WHERE_CLAUSE_TEMPLATE = "%1$s WHERE %2$s";

	private final String query;

	static String asQuery(GemfirePersistentEntity<?> entity, PartTree tree) {

		String distinctKeyword = tree.isDistinct() ? OqlKeyword.DISTINCT.toString() : "";
		String regionName = entity.getRegionName();
		String query = String.format(SELECT_OQL_TEMPLATE, distinctKeyword, regionName, DEFAULT_ALIAS)
			.replaceAll("\\s{2,}", " "); // single space tokens

		return query;
	}

	static String validateQuery(String query) {
		Assert.hasText(query, "Query is required");
		return query;
	}

	/**
	 * Constructs a new instance of {@link QueryBuilder} initialized with the given {@link String query}.
	 *
	 * @param query {@link String} containing the base {@link String OQL query}.
	 * @see #validateQuery(String)
	 */
	public QueryBuilder(String query) {
		this.query = validateQuery(query);
	}

	/**
	 * Constructs a new instance of {@link QueryBuilder} initialized with the given {@link GemfirePersistentEntity}
	 * and {@link PartTree} used to determine the {@link Region} to query and whether the query
	 * should capture unique results.
	 *
	 * @param entity {@link GemfirePersistentEntity} used to determine the {@link Region} to query.
	 * @param tree {@link PartTree} containing parts of the OQL Query for determining things like uniqueness.
	 * @see GemfirePersistentEntity
	 * @see PartTree
	 * @see #QueryBuilder(String)
	 */
	public QueryBuilder(GemfirePersistentEntity<?> entity, PartTree tree) {
		this(asQuery(entity, tree));
	}

	/**
	 * Constructs a {@link QueryString} with the given {@link Predicate}.
	 *
	 * @param predicate {@link Predicate} to append to the query.
	 * @return a {@link QueryString} with the base OQL query and {@link Predicate}.
	 * @see Predicate
	 * @see #withPredicate(String, Predicate)
	 */
	public QueryString create(@Nullable Predicate predicate) {
		return new QueryString(withPredicate(this.query, predicate));
	}

	/**
	 * Appends the given {@link Predicate} to the given {@code query} {@link String}.
	 *
	 * @param query {@link String} containing the query.
	 * @param predicate {@link Predicate} to append to the query.
	 * @return a {@link String} containing the query with the {@link Predicate} appended,
	 * or just a {@link String} containing the query if the {@link Predicate} is {@literal null}.
	 * @see Predicate
	 */
	protected String withPredicate(String query, @Nullable Predicate predicate) {

		return predicate != null
			? String.format(WHERE_CLAUSE_TEMPLATE, query, predicate.toString(DEFAULT_ALIAS))
			: query;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String toString() {
		return this.query;
	}
}
