/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;

import java.util.List;

import org.apache.geode.cache.lucene.LuceneQueryProvider;

import org.springframework.data.domain.Page;
import org.springframework.data.gemfire.search.lucene.ProjectingLuceneAccessor;
import org.springframework.data.gemfire.util.RuntimeExceptionFactory;

/**
 * {@link ProjectingLuceneAccessorSupport} is a {@link ProjectingLuceneAccessor} class implementation providing support
 * for extending classes.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.search.lucene.ProjectingLuceneAccessor
 * @since 1.1.0
 */
@SuppressWarnings("unused")
public abstract class ProjectingLuceneAccessorSupport extends ProjectingLuceneAccessor {

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> List<T> query(String query, String defaultField, int resultLimit, Class<T> projectionType) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> Page<T> query(String query, String defaultField, int resultLimit, int pageSize,
			Class<T> projectionType) {

		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> List<T> query(LuceneQueryProvider queryProvider, int resultLimit, Class<T> projectionType) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <T> Page<T> query(LuceneQueryProvider queryProvider, int resultLimit, int pageSize,
			Class<T> projectionType) {

		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}
}