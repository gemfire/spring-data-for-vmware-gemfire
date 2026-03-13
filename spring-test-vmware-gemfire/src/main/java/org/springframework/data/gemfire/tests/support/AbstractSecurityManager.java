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
package org.springframework.data.gemfire.tests.support;

import org.springframework.data.gemfire.gud.api.GudAuthenticationFailedException;
import org.springframework.data.gemfire.gud.api.GudResourcePermission;
import org.springframework.data.gemfire.gud.api.GudSecurityManager;

import java.util.Properties;

/**
 * The {@link AbstractSecurityManager} class is an abstract base class supporting implementations of
 * {@link GudSecurityManager}.
 *
 * @author John Blum
 * @see GudSecurityManager
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractSecurityManager implements GudSecurityManager {

	@Override
	public void init(Properties securityProps) {}

	@Override
	public Object authenticate(Properties credentials) throws GudAuthenticationFailedException {
		throw new GudAuthenticationFailedException("Access Denied");
	}

	@Override
	public boolean authorize(Object principal, GudResourcePermission permission) {
		throw new GudAuthenticationFailedException("Not Authorized");
	}

	@Override
	public void close() {}

}
