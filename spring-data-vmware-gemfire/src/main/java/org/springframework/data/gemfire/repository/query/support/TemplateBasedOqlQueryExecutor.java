/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query.support;

import org.apache.geode.cache.query.SelectResults;

import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * {@link OqlQueryExecutor} implementation using SDG's {@link GemfireTemplate} to execute Apache Geode
 * {@link String OQL queries}.
 *
 * @author John Blum
 * @see SelectResults
 * @see GemfireTemplate
 * @see OqlQueryExecutor
 * @see QueryMethod
 * @since 2.4.0
 */
public class TemplateBasedOqlQueryExecutor implements OqlQueryExecutor {

	private final GemfireTemplate template;

	/**
	 * Constructs a new instance of {@link TemplateBasedOqlQueryExecutor} initialized with the given,
	 * required {@link GemfireTemplate} used to execute Apache Geode {@link String OQL queries}.
	 *
	 * @param template {@link GemfireTemplate} used to execute Apache Geode {@link String OQL queries};
	 * must not be {@literal null}.
	 * @throws IllegalArgumentException if {@link GemfireTemplate} is {@literal null}.
	 * @see GemfireTemplate
	 */
	public TemplateBasedOqlQueryExecutor(@NonNull GemfireTemplate template) {

		Assert.notNull(template, "GemfireTemplate must not be null");

		this.template = template;
	}

	/**
	 * Gets the configured {@link GemfireTemplate} used to execute Apache Geode {@link String OQL queries}.
	 *
	 * @return the configured {@link GemfireTemplate} used to execute Apache Geode {@link String OQL queries}.
	 * @see GemfireTemplate
	 */
	protected @NonNull GemfireTemplate getTemplate() {
		return this.template;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public SelectResults execute(QueryMethod queryMethod, @NonNull String query, @NonNull Object... arguments) {
		return getTemplate().find(query, arguments);
	}
}
