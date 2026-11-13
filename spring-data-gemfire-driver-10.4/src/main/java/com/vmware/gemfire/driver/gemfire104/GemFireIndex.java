/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.function.Supplier;

import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.IndexType;

import com.vmware.gemfire.driver.spi.DriverIndex;
import com.vmware.gemfire.driver.spi.DriverIndexStatistics;
import com.vmware.gemfire.driver.spi.DriverIndexType;

/** Holds the native index so it can be removed through the service that produced it. */
final class GemFireIndex implements DriverIndex {

	private final Index index;
	private final GemFireExceptionMapper exceptions;

	GemFireIndex(Index index, GemFireExceptionMapper exceptions) {
		this.index = index;
		this.exceptions = exceptions;
	}

	Index nativeIndex() { return index; }

	@SuppressWarnings("deprecation")
	static DriverIndexType toDriver(IndexType type) {
		return switch (type) {
			case FUNCTIONAL -> DriverIndexType.FUNCTIONAL;
			case HASH -> DriverIndexType.HASH;
			case PRIMARY_KEY -> DriverIndexType.PRIMARY_KEY;
		};
	}

	@SuppressWarnings("deprecation")
	static IndexType toNative(DriverIndexType type) {
		return switch (type) {
			case FUNCTIONAL -> IndexType.FUNCTIONAL;
			case HASH -> IndexType.HASH;
			case PRIMARY_KEY -> IndexType.PRIMARY_KEY;
		};
	}

	@Override public String name() { return invoke("name", index::getName); }
	@Override public DriverIndexType type() { return invoke("type", () -> toDriver(index.getType())); }
	@Override public String regionPath() { return invoke("regionPath", () -> index.getRegion().getFullPath()); }
	@Override public String fromClause() { return invoke("fromClause", index::getFromClause); }
	@Override public String canonicalizedFromClause() { return invoke("canonicalizedFromClause", index::getCanonicalizedFromClause); }
	@Override public String indexedExpression() { return invoke("indexedExpression", index::getIndexedExpression); }
	@Override public String canonicalizedIndexedExpression() { return invoke("canonicalizedIndexedExpression", index::getCanonicalizedIndexedExpression); }
	@Override public String projectionAttributes() { return invoke("projectionAttributes", index::getProjectionAttributes); }
	@Override public String canonicalizedProjectionAttributes() { return invoke("canonicalizedProjectionAttributes", index::getCanonicalizedProjectionAttributes); }
	@Override public boolean isValid() { return invoke("isValid", index::isValid); }
	@Override public DriverIndexStatistics statistics() {
		return invoke("statistics", () -> new GemFireIndexStatistics(index.getStatistics(), exceptions));
	}

	private <T> T invoke(String operation, Supplier<T> action) {
		try {
			return action.get();
		}
		catch (RuntimeException ex) {
			throw exceptions.translate("index." + operation, ex);
		}
	}
}
