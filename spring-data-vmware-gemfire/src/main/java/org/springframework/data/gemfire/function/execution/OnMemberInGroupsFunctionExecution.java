/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;

import org.springframework.util.Assert;

/**
 * Creates an {@literal OnMember} {@link Function} {@link Execution} initialized with an array of {@link String groups}
 * using {@link FunctionService#onMember(String...)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.apache.geode.cache.execute.Execution
 * @see org.apache.geode.cache.execute.Function
 * @see org.apache.geode.cache.execute.FunctionService
 * @see org.springframework.data.gemfire.function.execution.AbstractFunctionExecution
 */
class OnMemberInGroupsFunctionExecution extends AbstractFunctionExecution {

	private final String[] groups;

	/**
	 * Constructs a new instance of the {@link OnMemberInGroupsFunctionExecution} initialized to execute a data independent
	 * {@link Function} on a single member from each of the specified groups.
	 *
	 * @param groups array of {@link String groups} from which to pick a member from each group
	 * on which to execute the data independent {@link Function}.
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
	 * @see org.apache.geode.cache.execute.FunctionService#onMember(String...)
	 */
	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMember(getGroups());
	}
}
