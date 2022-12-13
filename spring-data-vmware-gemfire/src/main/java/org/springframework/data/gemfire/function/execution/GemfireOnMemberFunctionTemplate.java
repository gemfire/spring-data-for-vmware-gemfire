/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.execution;

import org.apache.geode.distributed.DistributedMember;

/**
 *
 * @author David Turanski
 * @author John Blum
 * @see DistributedMember
 * @see AbstractFunctionTemplate
 */
public class GemfireOnMemberFunctionTemplate extends AbstractFunctionTemplate {

	private final DistributedMember distributedMember;

    private final String[] groups;

	public GemfireOnMemberFunctionTemplate() {
		this.distributedMember = null;
		this.groups = null;
	}

	public GemfireOnMemberFunctionTemplate(DistributedMember distributedMember) {
		this.distributedMember = distributedMember;
		this.groups = null;
	}

	public GemfireOnMemberFunctionTemplate(String[] groups) {
		this.distributedMember = null;
		this.groups = groups;
	}

	protected AbstractFunctionExecution getFunctionExecution() {

		if (this.distributedMember == null && this.groups == null) {
			return new OnDefaultMemberFunctionExecution();
		}
		else if (this.distributedMember == null) {
			return new OnMemberInGroupsFunctionExecution(this.groups);
		}

		return new OnDistributedMemberFunctionExecution(this.distributedMember);
	}
}
