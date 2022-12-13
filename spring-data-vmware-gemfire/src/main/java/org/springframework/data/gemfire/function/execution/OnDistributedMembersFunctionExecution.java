/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.util.Collections;
import java.util.Set;

import org.apache.geode.cache.execute.Execution;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionService;
import org.apache.geode.distributed.DistributedMember;

/**
 * Creates an {@literal OnMembers} {@link Function} {@link Execution} initialized with a {@link Set}
 * of {@link DistributedMember DistributedMembers} using {@link FunctionService#onMembers(Set)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see Execution
 * @see Function
 * @see FunctionService
 * @see DistributedMember
 */
class OnDistributedMembersFunctionExecution extends AbstractFunctionExecution {

	private final Set<DistributedMember> distributedMembers;

	public OnDistributedMembersFunctionExecution(Set<DistributedMember> distributedMembers ) {
		this.distributedMembers = distributedMembers;
	}

	protected Set<DistributedMember> getDistributedMembers() {
		return this.distributedMembers != null
			? Collections.unmodifiableSet(this.distributedMembers)
			: Collections.emptySet();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected Execution getExecution() {
		return FunctionService.onMembers(getDistributedMembers());
	}
}
