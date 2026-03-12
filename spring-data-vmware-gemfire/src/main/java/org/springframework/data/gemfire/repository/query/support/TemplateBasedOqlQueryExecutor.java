/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.repository.query.support;

import org.springframework.data.gemfire.gud.api.GudSelectResults;

import org.springframework.data.gemfire.GemfireTemplate;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * {@link OqlQueryExecutor} implementation using SDG's {@link GemfireTemplate} to execute Apache Geode
 * {@link String OQL queries}.
 *
 * @author John Blum
 * @see GudSelectResults
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
	 * {@inheritDoc}
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public GudSelectResults execute(QueryMethod queryMethod, @NonNull String query, @NonNull Object... arguments) {
		return getTemplate().find(query, arguments);
	}
}
