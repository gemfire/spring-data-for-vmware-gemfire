/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.domain.support;

import org.springframework.data.gemfire.gud.api.GudIndex;
import org.springframework.data.gemfire.gud.api.GudIndexStatistics;
import org.springframework.data.gemfire.gud.api.GudIndexType;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * {@link AbstractIndexSupport} is an abstract base class supporting the implementation
 * of the Pivotal GemFire / Apache Geode {@link GudIndex} interface.
 *
 * @author John Blum
 * @see GudIndex
 * @since 2.0.0
 */
public abstract class AbstractIndexSupport implements GudIndex {

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
	public GudRegion<?, ?> getRegion() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	public GudIndexStatistics getStatistics() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}

	@Override
	@SuppressWarnings("deprecation")
	public GudIndexType getType() {
		throw new UnsupportedOperationException(NOT_IMPLEMENTED);
	}
}
