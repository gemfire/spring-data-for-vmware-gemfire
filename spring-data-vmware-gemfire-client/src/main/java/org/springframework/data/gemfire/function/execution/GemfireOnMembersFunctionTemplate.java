/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import java.util.Set;

import org.apache.geode.distributed.DistributedMember;

/**
 *
 * @author David Turanski
 * @author John Blum
 * @see DistributedMember
 * @see AbstractFunctionTemplate
 */
public class GemfireOnMembersFunctionTemplate extends AbstractFunctionTemplate {

	private final Set<DistributedMember> distributedMembers;

    private final String[] groups;

	public GemfireOnMembersFunctionTemplate() {
		this.distributedMembers = null;
		this.groups = null;
	}

	public GemfireOnMembersFunctionTemplate(Set<DistributedMember> distributedMembers) {
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
