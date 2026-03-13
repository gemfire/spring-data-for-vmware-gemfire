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

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionException;

/**
 * An {@link GudFunctionException} indicating a {@link GudFunction} {@link GudExecution} {@link RuntimeException}
 * that has not be categorized, or identified by the framework.
 *
 * This {@link RuntimeException} was inspired by the {@link org.springframework.dao.UncategorizedDataAccessException}.
 *
 * @author John Blum
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionException
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public class UncategorizedFunctionException extends GudFunctionException {

	public UncategorizedFunctionException() {
		super(null, null);
	}

	public UncategorizedFunctionException(String message) {
		super(message, null);
	}

	public UncategorizedFunctionException(Throwable cause) {
		super(null, cause);
	}

	public UncategorizedFunctionException(String message, Throwable cause) {
		super(message, cause);
	}
}
