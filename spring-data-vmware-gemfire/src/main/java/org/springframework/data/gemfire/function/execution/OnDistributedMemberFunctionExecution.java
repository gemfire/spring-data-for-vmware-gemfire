/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.distributed.DistributedMember;

import org.springframework.util.Assert;

/**
 * Creates an {@literal OnMember} {@link Function} {@link Execution} initialized with a {@link DistributedMember}
 * using {@link FunctionService#onMember(DistributedMember)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Execution
 * @see Function
 * @see FunctionService
 * @see DistributedMember
 */
class OnDistributedMemberFunctionExecution extends AbstractFunctionExecution {

	private final DistributedMember distributedMember;

	public OnDistributedMemberFunctionExecution(DistributedMember distributedMember) {

		Assert.notNull(distributedMember, "DistributedMember must not be null");

		this.distributedMember = distributedMember;
	}

	protected DistributedMember getDistributedMember() {
		return this.distributedMember;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMember(getDistributedMember());
	}
}
