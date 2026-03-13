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

package org.springframework.data.gemfire.config.annotation.support;

import java.util.Optional;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.gud.api.GudAuthInitialize;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

/**
 * The {@link AutoConfiguredAuthenticationInitializer} class is a {@link GudAuthInitialize} implementation,
 * which auto-configures security, and specifically authentication, for Apache Geode/Pivotal GemFire.
 *
 * @author John Blum
 * @see Properties
 * @see GudAuthInitialize
 * @see Authentication
 * @since 2.0.0
 */
@SuppressWarnings("unused")
public class AutoConfiguredAuthenticationInitializer extends AbstractAuthInitialize {

	public static final String SECURITY_USERNAME_PROPERTY = SECURITY_USERNAME;
	public static final String SECURITY_PASSWORD_PROPERTY = SECURITY_PASSWORD;

	protected static final Properties NO_PARAMETERS = new Properties();

	private Authentication<String, String> authentication;

	/**
	 * Factory method used to construct a new instance of {@link AutoConfiguredAuthenticationInitializer}
	 * that will be auto-wired with a SDG {@link Authentication} providing Apache Geode Security and Auth
	 * were configured/requested.
	 *
	 * @return a new instance of {@link AutoConfiguredAuthenticationInitializer}.
	 * @see AutoConfiguredAuthenticationInitializer
	 */
	public static AutoConfiguredAuthenticationInitializer newAuthenticationInitializer() {

		AutoConfiguredAuthenticationInitializer authenticationInitializer =
			new AutoConfiguredAuthenticationInitializer();

		authenticationInitializer.initialize(null, NO_PARAMETERS);

		return authenticationInitializer;
	}

	@Autowired(required = false)
	public void setAuthentication(@Nullable Authentication<String, String> authentication) {
		this.authentication = authentication;
	}

	protected Optional<Authentication<String, String>> getAuthentication() {
		return Optional.ofNullable(this.authentication);
	}

	@Override
	protected @NonNull Properties doGetCredentials(@NonNull Properties properties) {

		getAuthentication()
			.filter(Authentication::isRequested)
			.ifPresent(authentication -> {
				properties.setProperty(SECURITY_USERNAME_PROPERTY, authentication.getPrincipal());
				properties.setProperty(SECURITY_PASSWORD_PROPERTY, authentication.getCredentials());
			});

		return properties;
	}
}
