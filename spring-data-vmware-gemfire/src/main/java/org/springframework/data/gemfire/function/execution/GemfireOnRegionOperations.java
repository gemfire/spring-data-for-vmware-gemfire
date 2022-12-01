/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import java.util.Set;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;

/**
 * Interface define {@link Region} {@link Function} data access operations.
 *
 * @author David Turanski
 * @author John Blum
 * @see Region
 * @see Function
 * @see GemfireFunctionOperations
 */
@SuppressWarnings("unused")
public interface GemfireOnRegionOperations extends GemfireFunctionOperations {

	default <T> Iterable<T> execute(Function function, Set<?> keys, Object... args) {
		return execute(function.getId(), keys, args);
	}

	<T> Iterable<T> execute(String functionId, Set<?> keys, Object... args);

	default <T> T executeAndExtract(Function function, Set<?> keys, Object... args) {
		return executeAndExtract(function.getId(), keys, args);
	}

	<T> T executeAndExtract(String functionId, Set<?> keys, Object... args);

	default void executeWithNoResult(Function function, Set<?> keys, Object... args) {
		executeWithNoResult(function.getId(), keys, args);
	}

	void executeWithNoResult(String functionId, Set<?> keys, Object... args);

}
