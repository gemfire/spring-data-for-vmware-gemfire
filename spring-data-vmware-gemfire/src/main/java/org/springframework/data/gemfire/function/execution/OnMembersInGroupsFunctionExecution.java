// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.util.Assert;

/**
 * Creates an {@literal OnMembers} {@link Function} {@link Execution} initialized with an array of {@link String groups}
 * using {@link FunctionService#onMembers(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Execution
 * @see Function
 * @see FunctionService
 * @see AbstractFunctionExecution
 */
class OnMembersInGroupsFunctionExecution extends AbstractFunctionExecution {

	private final String[] groups;

	/**
	 * Constructs a new instance of {@link OnMembersInGroupsFunctionExecution} initialized to execute a data independent
	 * {@link Function} on all members from each of the specified {@link String groups}.
	 *
	 * @param groups array of {@link String groups} indicating the members on which to execute
	 * the data independent {@link Function}.
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
	 * @see FunctionService#onMembers(String...)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMembers(getGroups());
	}
}
