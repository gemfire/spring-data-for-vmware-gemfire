/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.mapping.annotation.Region;

/**
 * The GuestUser class represents an authorized restricted user of a service or computer system, etc.
 *
 * @author John Blum
 * @see Region
 * @see User
 * @since 1.4.0
 */
@Region("/Local/Guest/Users")
@SuppressWarnings("unused")
public class GuestUser extends User {

	public GuestUser(final String username) {
		super(username);
	}

	@Override
	public String toString() {
		return String.format("Guest User '%1$s'", getUsername());
	}

}
