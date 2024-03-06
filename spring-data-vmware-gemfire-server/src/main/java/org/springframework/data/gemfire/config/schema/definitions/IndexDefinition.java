/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.schema.definitions;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;
import org.springframework.data.gemfire.IndexType;
import org.springframework.util.StringUtils;

import java.util.Optional;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

/**
 * {@link IndexDefinition} is an Abstract Data Type (ADT) encapsulating the configuration meta-data used to define
 * a new Apache Geode / Pivotal GemFire {@link Region} {@link Index}.
 *
 * @author John Blum
 * @see Region
 * @see Index
 * @see IndexType
 * @since 2.0.0
 */
public class IndexDefinition {

	protected static final int ORDER = RegionDefinition.ORDER + 1;

	/**
	 * Factory method used to construct a new instance of {@link IndexDefinition} defined from the given {@link Index}.
	 *
	 * @param index {@link Index} on which the new {@link IndexDefinition} will be defined;
	 * must not be {@literal null}.
	 * @return a new instance of {@link IndexDefinition} defined from the given {@link Index}.
	 * @throws IllegalArgumentException if {@link Index} is {@literal null}.
	 * @see Index
	 */
	public static IndexDefinition from(Index index) {
		return new IndexDefinition(index);
	}

	private transient Index index;

	private IndexType indexType;

	private String expression;
	private String fromClause;
	private String name;

	/**
	 * Constructs a new instance of {@link IndexDefinition} defined with the given {@link Index}.
	 *
	 * @param index {@link Index} on which this definition is defined; must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link Index} is {@literal null}.
	 * @see Index
	 */
	protected IndexDefinition(Index index) {
		this.index = index;
	}

	/**
	 * Returns a reference to the {@link Index} on which this definition is defined.
	 *
	 * @return a reference to the {@link Index} on which this definition is defined.
	 * @see Index
	 */
	protected Index getIndex() {
		return this.index;
	}

	public String getName() {
		return this.name;
	}

	public String getExpression() {
		return Optional.ofNullable(this.expression).filter(StringUtils::hasText)
			.orElseGet(this.index::getIndexedExpression);
	}

	public String getFromClause() {
		return Optional.ofNullable(this.fromClause).filter(StringUtils::hasText)
			.orElseGet(this.index::getFromClause);
	}

	public IndexType getIndexType() {
		return Optional.ofNullable(this.indexType).orElseGet(() -> IndexType.valueOf(this.index.getType()));
	}

	/**
	 * Get the order value of this object.
	 *
	 * @return the order value of this object.
	 * @see org.springframework.core.Ordered
	 */
	public int getOrder() {
		return ORDER;
	}

	@SuppressWarnings("deprecation")
	public IndexDefinition as(org.apache.geode.cache.query.IndexType gemfireGeodeIndexType) {
		return as(IndexType.valueOf(gemfireGeodeIndexType));
	}

	public IndexDefinition as(IndexType indexType) {
		this.indexType = indexType;
		return this;
	}

	public IndexDefinition having(String expression) {

		this.expression = Optional.ofNullable(expression).filter(StringUtils::hasText)
			.orElseThrow(() -> newIllegalArgumentException("Expression is required"));

		return this;
	}

	public IndexDefinition on(Region<?, ?> region) {
		return on(Optional.ofNullable(region).map(Region::getFullPath)
			.orElseThrow(() -> newIllegalArgumentException("Region is required")));
	}

	public IndexDefinition on(String fromClause) {

		this.fromClause = Optional.ofNullable(fromClause).filter(StringUtils::hasText)
			.orElseThrow(() -> newIllegalArgumentException("From Clause is required"));

		return this;
	}

	public IndexDefinition with(String name) {
		this.name = name;
		return this;
	}
}
