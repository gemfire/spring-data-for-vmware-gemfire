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

/**
 *
 * @author David Turanski
 * @author John Blum
 * @see GudDistributedMember
 * @see AbstractFunctionTemplate
 */
public class GemfireOnMemberFunctionTemplate extends AbstractFunctionTemplate {

	private final GudDistributedMember distributedMember;

    private final String[] groups;

	public GemfireOnMemberFunctionTemplate() {
		this.distributedMember = null;
		this.groups = null;
	}

	public GemfireOnMemberFunctionTemplate(GudDistributedMember distributedMember) {
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
