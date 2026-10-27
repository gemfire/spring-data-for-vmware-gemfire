/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import org.springframework.data.gemfire.test.support.MapBuilder;

/**
 * Unit Tests for {@link GeodeIntegratedSecurityConfiguration}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.config.annotation.GeodeIntegratedSecurityConfiguration
 * @since 1.0.0
 */
public class GeodeIntegratedSecurityConfigurationUnitTests {

	@Test
	public void toGemFirePropertiesIsCorrect() {

		Map<String, Object> annotationAttributes = MapBuilder.<String, Object>newMapBuilder()
			.put("clientAuthenticationInitializer", "example.TestSecurityClientAuthenticationInitialization")
			.build();

		GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();

		Properties gemfireProperties = configuration.toGemFireProperties(annotationAttributes);

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties).isNotEmpty();
		assertThat(gemfireProperties.getProperty(GeodeIntegratedSecurityConfiguration.SECURITY_CLIENT_AUTH_INIT))
			.isEqualTo("example.TestSecurityClientAuthenticationInitialization");
		assertThat(gemfireProperties).doesNotContainKey("security-manager");
		assertThat(gemfireProperties).doesNotContainKey("security-shiro-init");
		assertThat(gemfireProperties).doesNotContainKey("security-peer-auth-init");
		assertThat(gemfireProperties).doesNotContainKey("security-post-processor");

	}

	@Test
	public void rejectsExplicitShiroEnablement() {
		GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();
		configuration.setEnvironment(new MockEnvironment()
			.withProperty("spring.data.gemfire.security.shiro.enabled", "true"));

		assertThatThrownBy(() -> configuration.toGemFireProperties(Map.of()))
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessageContaining("spring.data.gemfire.security.shiro.enabled")
			.hasMessageContaining("client authentication");
	}

	@Test
	public void explicitShiroDisablementPreservesClientAuthentication() {
		GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();
		configuration.setEnvironment(new MockEnvironment()
			.withProperty("spring.data.gemfire.security.shiro.enabled", "false"));

		Properties properties = configuration.toGemFireProperties(Map.of(
			"clientAuthenticationInitializer", "example.ClientAuthenticationInitializer"));

		assertThat(properties).hasSize(1);
		assertThat(properties.getProperty("security-client-auth-init"))
			.isEqualTo("example.ClientAuthenticationInitializer");
	}

	@Test
	public void rejectsRemovedShiroEnvironmentProperty() {
		GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();
		configuration.setEnvironment(new MockEnvironment()
			.withProperty("spring.data.gemfire.security.shiro.ini-resource-path", "shiro.ini"));

		assertThatThrownBy(() -> configuration.toGemFireProperties(Map.of()))
			.isInstanceOf(UnsupportedOperationException.class)
			.hasMessageContaining("security-shiro-init");
	}

	@Test
	public void rejectsServerSecurityCallbackEnvironmentProperties() {
		for (String suffix : new String[] { "manager.class-name", "postprocessor.class-name" }) {
			String property = "spring.data.gemfire.security." + suffix;
			GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();
			configuration.setEnvironment(new MockEnvironment().withProperty(property, "private.ServerCallback"));

			assertThatThrownBy(() -> configuration.toGemFireProperties(Map.of()))
				.isInstanceOf(UnsupportedOperationException.class)
				.hasMessageContaining(property)
				.hasMessageContaining("configure it on the server")
				.hasMessageNotContaining("private.ServerCallback");
		}
	}

	@Test
	public void blankServerCallbackPropertiesPreserveClientAuthenticationOverride() {
		GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();
		configuration.setEnvironment(new MockEnvironment()
			.withProperty("spring.data.gemfire.security.manager.class-name", " ")
			.withProperty("spring.data.gemfire.security.postprocessor.class-name", "")
			.withProperty("spring.data.gemfire.security.client.authentication-initializer", "example.EnvironmentInitializer"));

		Properties properties = configuration.toGemFireProperties(Map.of(
			"clientAuthenticationInitializer", "example.AnnotationInitializer"));

		assertThat(properties).hasSize(1);
		assertThat(properties.getProperty("security-client-auth-init")).isEqualTo("example.EnvironmentInitializer");
	}
}
