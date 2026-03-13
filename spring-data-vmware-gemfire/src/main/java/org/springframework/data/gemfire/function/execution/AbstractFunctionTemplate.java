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

package org.springframework.data.gemfire.function.execution;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudResultCollector;

/**
 * Abstract base class for all {@link GudFunction} templates, containing operations common to invoking Apache Geode
 * or Pivotal GemFire {@link GudFunction Functions}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.springframework.data.gemfire.gud.api.GudExecution
 * @see GudFunction
 * @see GudResultCollector
 * @see InitializingBean
 * @see GemfireFunctionOperations
 * @see AbstractFunctionExecution
 */
abstract class AbstractFunctionTemplate implements GemfireFunctionOperations, InitializingBean {

	private volatile long timeout;

	private volatile GudResultCollector<?, ?> resultCollector;

	@Override
	public void afterPropertiesSet() throws Exception { }

	@Override
	@SuppressWarnings("rawtypes")
	public <T> Iterable<T> execute(GudFunction function, Object... args) {

		AbstractFunctionExecution functionExecution = getFunctionExecution()
			.setArguments(args)
			.setFunction(function);

		return execute(functionExecution);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public <T> T executeAndExtract(GudFunction function, Object... args) {

		AbstractFunctionExecution functionExecution = getFunctionExecution()
			.setArguments(args)
			.setFunction(function);

		return executeAndExtract(functionExecution);
	}

	@Override
	public <T> Iterable<T> execute(String functionId, Object... args) {

		AbstractFunctionExecution functionExecution = getFunctionExecution()
			.setArguments(args)
			.setFunctionId(functionId);

		return execute(functionExecution);
	}

	@Override
	public <T> T executeAndExtract(String functionId, Object... args) {

		AbstractFunctionExecution functionExecution = getFunctionExecution()
			.setArguments(args)
			.setFunctionId(functionId);

		return executeAndExtract(functionExecution);
	}

	@Override
	public void executeWithNoResult(String functionId, Object... args) {

		AbstractFunctionExecution functionExecution = getFunctionExecution()
			.setArguments(args)
			.setFunctionId(functionId);

		execute(functionExecution, false);
	}

	@Override
	public <T> T execute(GemfireFunctionCallback<T> callback) {
		return callback.doInGemfire(getFunctionExecution().getExecution());
	}

	protected <T> Iterable<T> execute(AbstractFunctionExecution functionExecution) {
		 return prepare(functionExecution).execute();
	}

	protected <T> Iterable<T> execute(AbstractFunctionExecution functionExecution, boolean returnResult) {
		 return prepare(functionExecution).execute(returnResult);
	}

	protected <T> T executeAndExtract(AbstractFunctionExecution functionExecution) {
		 return prepare(functionExecution).executeAndExtract();
	}

	AbstractFunctionExecution prepare(AbstractFunctionExecution functionExecution) {

		return functionExecution
			.setResultCollector(getResultCollector())
			.setTimeout(getTimeout());
	}

	protected abstract AbstractFunctionExecution getFunctionExecution();

	public void setResultCollector(GudResultCollector<?,?> resultCollector) {
		this.resultCollector = resultCollector;
	}

	public GudResultCollector<?,?> getResultCollector() {
		return this.resultCollector;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public long getTimeout() {
		return this.timeout;
	}
}
