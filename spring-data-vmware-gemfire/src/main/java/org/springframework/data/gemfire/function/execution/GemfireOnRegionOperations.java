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

import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * Interface define {@link GudRegion} {@link GudFunction} data access operations.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudRegion
 * @see GudFunction
 * @see GemfireFunctionOperations
 */
@SuppressWarnings("unused")
public interface GemfireOnRegionOperations extends GemfireFunctionOperations {

	default <T> Iterable<T> execute(GudFunction function, Set<?> keys, Object... args) {
		return execute(function.getId(), keys, args);
	}

	<T> Iterable<T> execute(String functionId, Set<?> keys, Object... args);

	default <T> T executeAndExtract(GudFunction function, Set<?> keys, Object... args) {
		return executeAndExtract(function.getId(), keys, args);
	}

	<T> T executeAndExtract(String functionId, Set<?> keys, Object... args);

	default void executeWithNoResult(GudFunction function, Set<?> keys, Object... args) {
		executeWithNoResult(function.getId(), keys, args);
	}

	void executeWithNoResult(String functionId, Set<?> keys, Object... args);

}
