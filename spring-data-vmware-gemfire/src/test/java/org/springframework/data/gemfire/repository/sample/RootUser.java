/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.mapping.annotation.Region;

/**
 * The RootUser class represents an authorized administrative user of a service or computer system, etc.
 *
 * @author John Blum
 * @see Region
 * @see org.springframework.data.gemfire.repository.sample.User
 * @since 1.4.0
 */
@Region("/Local/Admin/Users")
@SuppressWarnings("unused")
public class RootUser extends User {

	public RootUser(final String username) {
		super(username);
	}

	@Override
	public String toString() {
		return String.format("Root User '%1$s'", getUsername());
	}

}
