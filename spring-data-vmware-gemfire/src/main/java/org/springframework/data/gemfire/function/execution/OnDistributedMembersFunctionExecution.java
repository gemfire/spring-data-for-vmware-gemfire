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

import java.util.Collections;
import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionService;

/**
 * Creates an {@literal OnMembers} {@link GudFunction} {@link GudExecution} initialized with a {@link Set}
 * of {@link GudDistributedMember DistributedMembers} using {@link GudFunctionService#onMembers(Set)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionService
 * @see GudDistributedMember
 */
class OnDistributedMembersFunctionExecution extends AbstractFunctionExecution {

	private final Set<GudDistributedMember> distributedMembers;

	public OnDistributedMembersFunctionExecution(Set<GudDistributedMember> distributedMembers ) {
		this.distributedMembers = distributedMembers;
	}

	protected Set<GudDistributedMember> getDistributedMembers() {
		return this.distributedMembers != null
			? Collections.unmodifiableSet(this.distributedMembers)
			: Collections.emptySet();
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onMembers(getDistributedMembers());
	}
}
