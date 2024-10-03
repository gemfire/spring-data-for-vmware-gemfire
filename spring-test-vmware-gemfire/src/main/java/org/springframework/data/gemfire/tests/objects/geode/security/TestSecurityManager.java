/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.objects.geode.security;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.geode.security.AuthenticationFailedException;

/**
 * Test {@link org.apache.geode.security.SecurityManager} implementation used for testing purposes (only).
 *
 * @author John Blum
 * @see Properties
 * @see org.apache.geode.security.SecurityManager
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public class TestSecurityManager implements org.apache.geode.security.SecurityManager {

	private static final AtomicReference<TestSecurityManager> instance = new AtomicReference<>(null);

	public static TestSecurityManager getInstance() {

		return Optional.ofNullable(instance.get())
			.orElseThrow(() -> newIllegalStateException("No TestSecurityManager was initialized"));
	}

	public TestSecurityManager() {
		instance.compareAndSet(null, this);
	}

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException {
		throw new UnsupportedOperationException("Not Implemented");
	}
}
