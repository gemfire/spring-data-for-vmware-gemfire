/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class CacheConfigurationTests {

	@Test
	void copiesInputAndReturnsIndependentPropertiesWithoutDisclosingSecrets() {
		Map<String, String> input = new HashMap<>(Map.of("security-password", "credential-secret",
			ClientConfigurationProperties.SSL_KEYSTORE_PASSWORD, "tls-secret"));
		CacheConfiguration configuration = new CacheConfiguration(input);
		input.clear();
		configuration.properties().clear();
		assertThat(configuration.properties().getProperty("security-password")).isEqualTo("credential-secret");
		assertThat(configuration.properties().getProperty(ClientConfigurationProperties.SSL_KEYSTORE_PASSWORD))
			.isEqualTo("tls-secret");
		assertThat(configuration.toString()).doesNotContain("credential-secret", "security-password",
			"tls-secret", ClientConfigurationProperties.SSL_KEYSTORE_PASSWORD);
	}
}
