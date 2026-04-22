/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import java.util.Properties;

import org.junit.Test;

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
			.put("securityManagerClass", TestSecurityManager.class)
			.put("securityManagerClassName", "  ")
			.put("peerAuthenticationInitializer", "example.TestSecurityPeerAuthenticationInitializer")
			.put("securityPostProcessorClass", TestSecurityPostProcessor.class)
			.put("securityPostProcessorClassName", "")
			.build();

		GeodeIntegratedSecurityConfiguration configuration = new GeodeIntegratedSecurityConfiguration();

		Properties gemfireProperties = configuration.toGemFireProperties(annotationAttributes);

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties).isNotEmpty();
		assertThat(gemfireProperties.getProperty(GeodeIntegratedSecurityConfiguration.SECURITY_CLIENT_AUTH_INIT))
			.isEqualTo("example.TestSecurityClientAuthenticationInitialization");
		assertThat(gemfireProperties.getProperty(GeodeIntegratedSecurityConfiguration.SECURITY_MANAGER))
			.isEqualTo(TestSecurityManager.class.getName());
		assertThat(gemfireProperties).doesNotContainKey(GeodeIntegratedSecurityConfiguration.SECURITY_SHIRO_INIT);
		assertThat(gemfireProperties.getProperty(GeodeIntegratedSecurityConfiguration.SECURITY_PEER_AUTH_INIT))
			.isEqualTo("example.TestSecurityPeerAuthenticationInitializer");
		assertThat(gemfireProperties.getProperty(GeodeIntegratedSecurityConfiguration.SECURITY_POST_PROCESSOR))
			.isEqualTo(TestSecurityPostProcessor.class.getName());

	}

	private static class TestSecurityPostProcessor implements org.springframework.data.gemfire.gud.api.GudPostProcessor {

		@Override
		public Object processRegionValue(Object principal, String regionName, Object key, Object value) {
			return value;
		}
	}
}
