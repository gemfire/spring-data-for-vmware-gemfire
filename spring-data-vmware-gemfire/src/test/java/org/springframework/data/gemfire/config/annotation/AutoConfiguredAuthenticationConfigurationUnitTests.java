/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.util.Properties;

import org.junit.Test;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.Authentication;
import org.springframework.data.gemfire.config.annotation.support.AutoConfiguredAuthenticationInitializer;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoConfiguredAuthenticationConfigurationUnitTests {

	@Test
	public void requestedAuthenticationConfiguresClientInitializerWithoutPeerAuthentication() {

		Authentication<String, String> authentication = new Authentication<>() {
			@Override
			public String getPrincipal() {
				return "client-user";
			}

			@Override
			public String getCredentials() {
				return "client-password";
			}
		};

		Properties properties = configure(authentication);

		assertThat(properties)
			.containsEntry("security-client-auth-init", AutoConfiguredAuthenticationInitializer.class.getName()
				+ ".newAuthenticationInitializer")
			.containsEntry("name", "client")
			.doesNotContainKeys("security-peer-auth-init", "security-username", "security-password");
	}

	@Test
	public void missingCredentialsPreserveConfiguredClientInitializer() {
		assertUnchanged(configure(() -> "client-user"));
	}

	@Test
	public void absentAuthenticationPreservesConfiguredClientInitializer() {
		assertUnchanged(configure(null));
	}

	private Properties configure(Authentication<String, String> authentication) {

		Properties properties = new Properties();
		properties.setProperty("name", "client");
		properties.setProperty("security-client-auth-init", "example.ClientAuthInitialize.create");

		ClientCacheFactoryBean client = new ClientCacheFactoryBean();
		client.setProperties(properties);

		new AutoConfiguredAuthenticationConfiguration()
			.authenticationInitializingClientCacheConfigurer(authentication)
			.configure("client", client);

		return client.getProperties();
	}

	private void assertUnchanged(Properties properties) {

		assertThat(properties)
			.hasSize(2)
			.containsEntry("name", "client")
			.containsEntry("security-client-auth-init", "example.ClientAuthInitialize.create")
			.doesNotContainKey("security-peer-auth-init");
	}
}
