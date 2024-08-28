/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.SecurityManager;

import org.springframework.util.StringUtils;

/**
 * The {@link TestSecurityManager} class is an Apache Geode {@link SecurityManager}
 * implementation used for testing purposes.
 *
 * @author John Blum
 * @see Principal
 * @see Properties
 * @see javax.security.auth.Subject
 * @see SecurityManager
 * @since 2.0.0
 */
public final class TestSecurityManager implements SecurityManager {

	public static final String SECURITY_USERNAME = "testUser";
	public static final String SECURITY_PASSWORD = "&t35t9@55w0rd!";

	public static final String SECURITY_USERNAME_PROPERTY = SecurityManager.USER_NAME;
	public static final String SECURITY_PASSWORD_PROPERTY = SecurityManager.PASSWORD;

	private final ConcurrentMap<String, String> authorizedUsers;

	public TestSecurityManager() {
		this.authorizedUsers = new ConcurrentHashMap<>();
		this.authorizedUsers.putIfAbsent(SECURITY_USERNAME, SECURITY_PASSWORD);
	}

	private Map<String, String> getAuthorizedUsers() {
		return Collections.unmodifiableMap(this.authorizedUsers);
	}

	@Override
	public Object authenticate(Properties credentials) throws AuthenticationFailedException {

		String username = credentials.getProperty(SECURITY_USERNAME_PROPERTY);
		String password = credentials.getProperty(SECURITY_PASSWORD_PROPERTY);

		return Optional.ofNullable(identify(username, password)).orElseThrow(() ->
			new AuthenticationFailedException(String.format("User [%s] is not authorized", username)));
	}

	private Principal identify(String username, String password) {
		return isIdentified(username, password) ? TestPrincipal.newPrincipal(username) : null;
	}

	private boolean isIdentified(String username, String password) {

		return Optional.ofNullable(username)
			.filter(StringUtils::hasText)
			.map(user -> getAuthorizedUsers().get(user))
			.map(userPassword -> userPassword.equals(password))
			.orElse(false);
	}

	@SuppressWarnings("unused")
	public static final class TestPrincipal implements Principal, java.io.Serializable {

		private final String name;

		public static TestPrincipal newPrincipal(String username) {
			return new TestPrincipal(username);
		}

		public TestPrincipal(String name) {
			this.name = Optional.ofNullable(name)
				.filter(StringUtils::hasText)
				.orElseThrow(() -> newIllegalArgumentException("Name is required"));
		}

		@Override
		public String getName() {
			return this.name;
		}
	}
}
