/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.query.Index;
import org.apache.geode.cache.query.QueryService;

import com.vmware.gemfire.driver.spi.DriverIndex;
import com.vmware.gemfire.driver.spi.DriverIndexType;
import com.vmware.gemfire.driver.spi.DriverQuery;
import com.vmware.gemfire.driver.spi.DriverQueryService;

/** Wraps one native query service; region paths are resolved through the injected lookup. */
final class GemFireQueryService implements DriverQueryService {

	private final QueryService service;
	private final Function<String, Region<?, ?>> regions;
	private final GemFireExceptionMapper exceptions;

	GemFireQueryService(QueryService service, Function<String, Region<?, ?>> regions, GemFireExceptionMapper exceptions) {
		this.service = service;
		this.regions = regions;
		this.exceptions = exceptions;
	}

	@Override public DriverQuery newQuery(String queryString) {
		return invoke("newQuery", () -> new GemFireQuery(service.newQuery(Objects.requireNonNull(queryString, "queryString")), exceptions));
	}

	@Override @SuppressWarnings("deprecation")
	public DriverIndex createIndex(String name, DriverIndexType type, String indexedExpression, String fromClause, String imports) {
		return invoke("createIndex", () -> {
			check(type, imports);
			Index index = switch (type) {
				case FUNCTIONAL -> imports == null ? service.createIndex(name, indexedExpression, fromClause)
					: service.createIndex(name, indexedExpression, fromClause, imports);
				case HASH -> imports == null ? service.createHashIndex(name, indexedExpression, fromClause)
					: service.createHashIndex(name, indexedExpression, fromClause, imports);
				case PRIMARY_KEY -> service.createKeyIndex(name, indexedExpression, fromClause);
			};
			return new GemFireIndex(index, exceptions);
		});
	}

	@Override @SuppressWarnings("deprecation")
	public void defineIndex(String name, DriverIndexType type, String indexedExpression, String fromClause, String imports) {
		invoke("defineIndex", () -> {
			check(type, imports);
			switch (type) {
				case FUNCTIONAL -> {
					if (imports == null) service.defineIndex(name, indexedExpression, fromClause);
					else service.defineIndex(name, indexedExpression, fromClause, imports);
				}
				case HASH -> {
					if (imports == null) service.defineHashIndex(name, indexedExpression, fromClause);
					else service.defineHashIndex(name, indexedExpression, fromClause, imports);
				}
				case PRIMARY_KEY -> service.defineKeyIndex(name, indexedExpression, fromClause);
			}
			return null;
		});
	}

	@Override public List<DriverIndex> createDefinedIndexes() {
		return invoke("createDefinedIndexes", () -> wrap(service.createDefinedIndexes()));
	}

	@Override public boolean clearDefinedIndexes() { return invoke("clearDefinedIndexes", service::clearDefinedIndexes); }

	@Override public DriverIndex getIndex(String regionPath, String indexName) {
		return invoke("getIndex", () -> {
			Index index = service.getIndex(region(regionPath), indexName);
			return index == null ? null : new GemFireIndex(index, exceptions);
		});
	}

	@Override public Collection<DriverIndex> getIndexes() { return invoke("getIndexes", () -> wrap(service.getIndexes())); }

	@Override public Collection<DriverIndex> getIndexes(String regionPath) {
		return invoke("getIndexes", () -> wrap(service.getIndexes(region(regionPath))));
	}

	@Override public Collection<DriverIndex> getIndexes(String regionPath, DriverIndexType type) {
		return invoke("getIndexes", () -> {
			Objects.requireNonNull(type, "type");
			return wrap(service.getIndexes(region(regionPath), GemFireIndex.toNative(type)));
		});
	}

	@Override public void removeIndex(DriverIndex index) {
		invoke("removeIndex", () -> {
			if (!(index instanceof GemFireIndex own)) throw new IllegalArgumentException("Index was not produced by this driver");
			service.removeIndex(own.nativeIndex());
			return null;
		});
	}

	@Override public void removeIndexes() { invoke("removeIndexes", () -> { service.removeIndexes(); return null; }); }

	@Override public void removeIndexes(String regionPath) {
		invoke("removeIndexes", () -> { service.removeIndexes(region(regionPath)); return null; });
	}

	private static void check(DriverIndexType type, String imports) {
		Objects.requireNonNull(type, "type");
		if (type == DriverIndexType.PRIMARY_KEY && imports != null) {
			throw new IllegalArgumentException("Primary-key indexes accept no imports");
		}
	}

	private Region<?, ?> region(String regionPath) {
		Objects.requireNonNull(regionPath, "regionPath");
		Region<?, ?> region = regions.apply(regionPath);
		if (region == null) throw new IllegalArgumentException("Region does not exist");
		return region;
	}

	private List<DriverIndex> wrap(Collection<Index> indexes) {
		List<DriverIndex> wrapped = new ArrayList<>();
		for (Index index : indexes) wrapped.add(new GemFireIndex(index, exceptions));
		return wrapped;
	}

	private <T> T invoke(String operation, Callable<T> action) {
		try {
			return action.call();
		}
		catch (Exception ex) {
			throw exceptions.translate("queryService." + operation, ex);
		}
	}
}
