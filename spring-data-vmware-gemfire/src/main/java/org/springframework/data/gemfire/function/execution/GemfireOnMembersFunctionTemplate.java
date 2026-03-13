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

import java.util.Set;

import org.springframework.data.gemfire.gud.api.GudDistributedMember;

/**
 *
 * @author David Turanski
 * @author John Blum
 * @see GudDistributedMember
 * @see AbstractFunctionTemplate
 */
public class GemfireOnMembersFunctionTemplate extends AbstractFunctionTemplate {

	private final Set<GudDistributedMember> distributedMembers;

    private final String[] groups;

	public GemfireOnMembersFunctionTemplate() {
		this.distributedMembers = null;
		this.groups = null;
	}

	public GemfireOnMembersFunctionTemplate(Set<GudDistributedMember> distributedMembers) {
		this.distributedMembers = distributedMembers;
		this.groups = null;
	}

	public GemfireOnMembersFunctionTemplate(String[] groups) {
		this.distributedMembers = null;
		this.groups = groups;
	}

	protected AbstractFunctionExecution getFunctionExecution() {

		if (this.distributedMembers == null && this.groups == null) {
			return new OnAllMembersFunctionExecution();
		}
		else if (this.distributedMembers == null) {
			return new OnMembersInGroupsFunctionExecution(this.groups);
		}

		return new OnDistributedMembersFunctionExecution(this.distributedMembers);
	}
}
