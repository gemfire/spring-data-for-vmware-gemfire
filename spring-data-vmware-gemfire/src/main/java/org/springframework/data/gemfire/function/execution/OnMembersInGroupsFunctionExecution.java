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
 * Creates an {@literal OnMembers} {@link GudFunction} {@link GudExecution} initialized with an array of {@link String groups}
 * using {@link GudFunctionService#onMembers(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionService
 * @see AbstractFunctionExecution
 */
class OnMembersInGroupsFunctionExecution extends AbstractFunctionExecution {

	private final String[] groups;

	/**
	 * Constructs a new instance of {@link OnMembersInGroupsFunctionExecution} initialized to execute a data independent
	 * {@link GudFunction} on all members from each of the specified {@link String groups}.
	 *
	 * @param groups array of {@link String groups} indicating the members on which to execute
	 * the data independent {@link GudFunction}.
	 * @throws IllegalArgumentException if {@link String groups} is {@literal null} or empty.
	 */
	public OnMembersInGroupsFunctionExecution(String... groups) {

		Assert.notEmpty(groups, "Groups must not be null or empty");

		this.groups = groups;
	}

	protected String[] getGroups() {
		return this.groups;
	}

	/**
	 * Executes the data independent Function on all members from each of the specified groups.
	 *
	 * @return an Execution to execute the Function.
	 * @see GudFunctionService#onMembers(String...)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onMembers(getGroups());
	}
}
