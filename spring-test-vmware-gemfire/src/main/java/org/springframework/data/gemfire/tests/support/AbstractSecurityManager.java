/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.support;

import java.util.Properties;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;

import org.apache.shiro.authz.AuthorizationException;

/**
 * The {@link AbstractSecurityManager} class is an abstract base class supporting implementations of
 * {@link org.apache.geode.security.SecurityManager}.
 *
 * @author John Blum
 * @see org.apache.geode.security.SecurityManager
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class AbstractSecurityManager implements org.apache.geode.security.SecurityManager {

	@Override
	public void init(Properties securityProps) { }

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException {
		throw new AuthenticationFailedException("Access Denied");
	}

	@Override
	public boolean authorize(Object principal, ResourcePermission permission) {
		throw new AuthorizationException("Not Authorized");
	}

	@Override
	public void close() { }

}
