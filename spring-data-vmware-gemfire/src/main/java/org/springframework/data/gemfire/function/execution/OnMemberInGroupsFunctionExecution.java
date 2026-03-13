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
import org.springframework.util.Assert;

/**
 * Creates an {@literal OnMember} {@link GudFunction} {@link GudExecution} initialized with an array of {@link String groups}
 * using {@link GudFunctionService#onMember(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionService
 * @see AbstractFunctionExecution
 */
class OnMemberInGroupsFunctionExecution extends AbstractFunctionExecution {

	private final String[] groups;

	/**
	 * Constructs a new instance of the {@link OnMemberInGroupsFunctionExecution} initialized to execute a data independent
	 * {@link GudFunction} on a single member from each of the specified groups.
	 *
	 * @param groups array of {@link String groups} from which to pick a member from each group
	 * on which to execute the data independent {@link GudFunction}.
	 * @throws IllegalArgumentException if {@link String groups} is {@literal null} or empty.
	 */
	public OnMemberInGroupsFunctionExecution(String... groups) {

		Assert.notEmpty(groups, "Groups must not be null or empty");

		this.groups = groups;
	}

	protected String[] getGroups() {
		return this.groups;
	}

	/**
	 * Executes the data independent Function on a single member from each of the specified groups.
	 *
	 * @return an Execution to execute the Function.
	 * @see GudFunctionService#onMember(String...)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onMember(getGroups());
	}
}
