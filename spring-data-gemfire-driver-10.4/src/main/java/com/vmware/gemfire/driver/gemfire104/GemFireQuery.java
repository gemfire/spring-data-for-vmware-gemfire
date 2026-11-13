/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import java.util.concurrent.Callable;

import org.apache.geode.cache.query.Query;

import com.vmware.gemfire.driver.spi.DriverQuery;
import com.vmware.gemfire.driver.spi.DriverQueryStatistics;

final class GemFireQuery implements DriverQuery {

	private final Query query;
	private final GemFireExceptionMapper exceptions;

	GemFireQuery(Query query, GemFireExceptionMapper exceptions) {
		this.query = query;
		this.exceptions = exceptions;
	}

	@Override public String queryString() { return invoke("queryString", query::getQueryString); }

	@Override public Object execute() {
		return invoke("execute", () -> GemFireQueryValues.convert(query.execute()));
	}

	@Override public Object execute(Object... parameters) {
		return invoke("execute", () -> {
			if (parameters == null) throw new IllegalArgumentException("parameters must not be null");
			for (Object parameter : parameters) {
				if (parameter == null) throw new IllegalArgumentException("query parameters must not be null");
			}
			return GemFireQueryValues.convert(query.execute(parameters));
		});
	}

	@Override public DriverQueryStatistics statistics() {
		return invoke("statistics", () -> {
			var statistics = query.getStatistics();
			return new DriverQueryStatistics(statistics.getNumExecutions(), statistics.getTotalExecutionTime());
		});
	}

	private <T> T invoke(String operation, Callable<T> action) {
		try {
			return action.call();
		}
		catch (Exception ex) {
			throw exceptions.translate("query." + operation, ex);
		}
	}
}
