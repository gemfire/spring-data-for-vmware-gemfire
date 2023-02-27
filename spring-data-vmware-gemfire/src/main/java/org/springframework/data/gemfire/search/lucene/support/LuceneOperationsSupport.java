/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.search.lucene.support;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newUnsupportedOperationException;

import java.util.Collection;
import java.util.List;

import org.apache.geode.cache.lucene.LuceneQueryProvider;
import org.apache.geode.cache.lucene.LuceneResultStruct;
import org.apache.geode.cache.lucene.PageableLuceneQueryResults;

import org.springframework.data.gemfire.search.lucene.LuceneOperations;
import org.springframework.data.gemfire.util.RuntimeExceptionFactory;

/**
 * {@link LuceneOperationsSupport} is a abstract supporting class for implementations
 * of the {@link LuceneOperations} interface.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.search.lucene.LuceneOperations
 * @since 1.1.0
 * @deprecated To be removed in GemFire 10 integration
 */
@SuppressWarnings("unused")
public abstract class LuceneOperationsSupport implements LuceneOperations {

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> List<LuceneResultStruct<K, V>> query(String query, String defaultField, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> PageableLuceneQueryResults<K, V> query(String query, String defaultField,
			int resultLimit, int pageSize) {

		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> List<LuceneResultStruct<K, V>> query(LuceneQueryProvider queryProvider, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K, V> PageableLuceneQueryResults<K, V> query(LuceneQueryProvider queryProvider,
			int resultLimit, int pageSize) {

		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K> Collection<K> queryForKeys(String query, String defaultField, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <K> Collection<K> queryForKeys(LuceneQueryProvider queryProvider, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <V> Collection<V> queryForValues(String query, String defaultField, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public <V> Collection<V> queryForValues(LuceneQueryProvider queryProvider, int resultLimit) {
		throw newUnsupportedOperationException(RuntimeExceptionFactory.NOT_IMPLEMENTED);
	}
}
