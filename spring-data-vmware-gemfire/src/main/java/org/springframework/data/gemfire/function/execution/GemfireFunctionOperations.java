/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Function;

/**
 *
 * An interface for invoking Gemfire functions
 *
 * @author David Turanski
 */
public interface GemfireFunctionOperations {

	/**
	 * Execute an unregistered GemFire Function with the given arguments.
	 *
	 * @param <T> type parameter specifying the result type of the Function execution.
	 * @param function the GemFire Function object to execute.
	 * @param args an array of Object arguments to the Function call.
	 * @return the contents of the ResultsCollector.
	 */
	<T> Iterable<T> execute(Function function, Object... args);

	/**
	 * Execute a GemFire Function registered with the given ID.
	 *
	 * @param <T> type parameter specifying the result type of the Function execution.
	 * @param functionId the ID under which the GemFire function is registered.
	 * @param args an array of Object arguments to the Function call.
	 * @return the results
	 */
	<T> Iterable<T> execute(String functionId, Object... args);

    /**
     * Execute an unregistered GemFire Function with the expected singleton result.

	 * @param <T> type parameter specifying the result type of the Function execution.
	 * @param function the GemFire Function object.
	 * @param args an array of Object arguments to the Function call.
	 * @return the first item in the ResultsCollector.
	 * @see Function
     */
	<T> T executeAndExtract(Function function, Object... args);

	/**
	 * Execute a GemFire Function registered with an ID and with an expected singleton result

	 * @param <T> type parameter specifying the result type of the Function execution.
	 * @param functionId the ID under which the GemFire function is registered.
	 * @param args an array of Object arguments to the Function call.
	 * @return the first item in the results collector
	 */
	<T> T executeAndExtract(String functionId, Object... args);

	/**
	 * Execute a GemFire Function registered with the given ID having no return value.

	 * @param functionId the ID under which the GemFire function is registered.
	 * @param args an array of Object arguments to the Function call.
	 */
	void executeWithNoResult(String functionId, Object... args);

    /**
     * Execute a GemFire Function using a native GemFire {@link org.apache.geode.cache.execute.Execution} instance.
	 *
	 * @param <T> type parameter specifying the result type of the Function execution.
     * @param callback a callback providing the execution instance.
     * @return the Function execution result.
	 * @see GemfireFunctionCallback
     */
	<T> T execute(GemfireFunctionCallback<T> callback);

}
