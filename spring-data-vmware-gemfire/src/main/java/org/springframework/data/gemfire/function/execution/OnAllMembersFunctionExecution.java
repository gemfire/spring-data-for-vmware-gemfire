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

import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionService;

/**
 * Creates an {@literal OnMembers} {@link GudFunction} {@link GudExecution} for all members
 * using {@link GudFunctionService#onMembers(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionService
 * @see AbstractFunctionExecution
 * @since 1.3.0
 */
class OnAllMembersFunctionExecution extends AbstractFunctionExecution {

  	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onMembers();
	}
}
