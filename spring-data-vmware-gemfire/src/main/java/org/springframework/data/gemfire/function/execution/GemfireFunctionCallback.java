/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-12: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudExecution;

/**
 * A callback for Gemfire Function Templates
 * @author David Turanski
 *
 */
public interface GemfireFunctionCallback<T> {
	public T doInGemfire(GudExecution execution);
}
