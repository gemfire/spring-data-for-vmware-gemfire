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

import org.springframework.data.gemfire.gud.api.GudDistributedMember;
import org.springframework.data.gemfire.gud.api.GudExecution;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionService;
import org.springframework.util.Assert;

/**
 * Creates an {@literal OnMember} {@link GudFunction} {@link GudExecution} initialized with a {@link GudDistributedMember}
 * using {@link GudFunctionService#onMember(GudDistributedMember)}.
 *
 * @author David Turanski
 * @author John Blum
 * @see GudExecution
 * @see GudFunction
 * @see GudFunctionService
 * @see GudDistributedMember
 */
class OnDistributedMemberFunctionExecution extends AbstractFunctionExecution {

	private final GudDistributedMember distributedMember;

	public OnDistributedMemberFunctionExecution(GudDistributedMember distributedMember) {

		Assert.notNull(distributedMember, "DistributedMember must not be null");

		this.distributedMember = distributedMember;
	}

	protected GudDistributedMember getDistributedMember() {
		return this.distributedMember;
	}

	@Override
	@SuppressWarnings("rawtypes")
	protected GudExecution getExecution() {
		return GudFunctionService.onMember(getDistributedMember());
	}
}
