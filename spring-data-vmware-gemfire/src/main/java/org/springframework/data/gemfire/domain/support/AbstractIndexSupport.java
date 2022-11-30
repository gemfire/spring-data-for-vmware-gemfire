// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.domain.support;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexStatistics;
import org.apache.geode.cache.query.IndexType;

/**
 * {@link AbstractIndexSupport} is an abstract base class supporting the implementation
 * of the Pivotal GemFire / Apache Geode {@link Index} interface.
 *
 * @author John Blum
 * @see Index
 * @since 2.0.0
 */
public abstract class AbstractIndexSupport implements Index {

	private static final String NOT_IMPLEMENTED = "Not Implemented";

	@Override
	public String getCanonicalizedFromClause() {
		return getFromClause();
	}

	@Override
	public String getCanonicalizedIndexedExpression() {
		return getIndexedExpression();
	}

	@Override
	public String getCanonicalizedProjectionAttributes() {
		return getProjectionAttributes();
	}

	@Override
	public String getFromClause() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public String getIndexedExpression() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public String getName() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public String getProjectionAttributes() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public Region<?, ?> getRegion() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public IndexStatistics getStatistics() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public IndexType getType() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}
}
