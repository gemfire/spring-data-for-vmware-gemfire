/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.vmware.gemfire.driver.spi.ClientConfigurationProperties;

class MockClientRuntimeTests {

	@Test
	void samplingArchiveAndThreadMonitorSettingsRetainSuppliedValues() {
		var settings = new java.util.Properties();
		settings.setProperty(ClientConfigurationProperties.STATISTIC_SAMPLE_RATE, "500");
		settings.setProperty(ClientConfigurationProperties.STATISTIC_SAMPLING_ENABLED, "false");
		settings.setProperty(ClientConfigurationProperties.ARCHIVE_DISK_SPACE_LIMIT, "-1");
		settings.setProperty(ClientConfigurationProperties.THREAD_MONITOR_INTERVAL, "-1");
		var expected = new java.util.Properties();
		expected.putAll(settings);
		try (var connection = new MockConnection(settings)) {
			settings.clear();
			var snapshot = connection.getRuntime().getProperties().properties();
			assertThat(snapshot).isEqualTo(expected);
			snapshot.clear();
			assertThat(connection.getRuntime().getProperties().properties()).isEqualTo(expected);
		}
	}

	@Test
	void configuredIdentityIsDetachedAndOtherConnectionsKeepTheirDefaults() {
		var settings = new java.util.Properties();
		settings.setProperty(ClientConfigurationProperties.NAME, "first");
		settings.setProperty(ClientConfigurationProperties.GROUPS, "one,two");
		settings.setProperty(ClientConfigurationProperties.DURABLE_CLIENT_ID, "durable_gem_pool");
		settings.setProperty(ClientConfigurationProperties.DURABLE_CLIENT_TIMEOUT, "-1");
		try (var first = new MockConnection(settings); var second = new MockConnection()) {
			settings.clear();
			assertThat(first.getRuntime().getName()).isEqualTo("first");
			assertThat(first.member().getGroups()).containsExactly("one", "two");
			assertThatThrownBy(() -> first.member().getGroups().clear()).isInstanceOf(UnsupportedOperationException.class);
			assertThat(first.member().getDurableClientAttributes().poolName()).isEqualTo("pool");
			assertThat(first.member().getDurableClientAttributes().timeout()).isEqualTo(-1);
			assertThat(second.getRuntime().getName()).isEmpty();
			assertThat(second.member().getGroups()).isEmpty();
			assertThat(second.member().getDurableClientAttributes().id()).isEmpty();
			assertThat(second.member().getDurableClientAttributes().timeout()).isEqualTo(300);
		}
	}

	@Test
	void malformedDurableTimeoutHasAClassifiedSafeFailure() {
		var settings = new java.util.Properties();
		settings.setProperty(ClientConfigurationProperties.DURABLE_CLIENT_TIMEOUT, "private-token");
		assertThatThrownBy(() -> new MockConnection(settings)).isInstanceOfSatisfying(
			com.vmware.gemfire.driver.spi.DriverOperationException.class, failure -> {
				assertThat(failure.kind()).isEqualTo(com.vmware.gemfire.driver.spi.DriverFailureKind.INVALID_ARGUMENT);
				assertThat(failure.getMessage()).doesNotContain("private-token");
			});
	}

	@Test
	void propertyDiscoveryReportsUnresolvedDefaultsWithoutConnecting() {
		var driver = new MockGemFireDriver();
		assertThat(driver.resolvePropertiesFile().fileName()).isEqualTo("gemfire.properties");
		assertThat(driver.resolvePropertiesFile().location()).isEmpty();
		assertThat(driver.resolveSecurityPropertiesFile().fileName()).isEqualTo("gfsecurity.properties");
		assertThat(driver.resolveSecurityPropertiesFile().location()).isEmpty();
	}

	@Test
	void suppliedConfigurationIsIsolatedAndDoesNotInventNativeDefaults() {
		var settings = new java.util.Properties();
		settings.setProperty(ClientConfigurationProperties.NAME, "first");
		settings.setProperty("security-password", "private-token");
		settings.setProperty(ClientConfigurationProperties.SSL_KEYSTORE_PASSWORD, "private-tls-token");
		try (var first = new MockConnection(settings); var second = new MockConnection()) {
			settings.clear();
			assertThat(first.getRuntime().getProperties().properties()).hasSize(3)
				.containsEntry(ClientConfigurationProperties.NAME, "first")
				.containsEntry(ClientConfigurationProperties.SSL_KEYSTORE_PASSWORD, "private-tls-token");
			assertThat(first.getRuntime().getSecurityProperties().properties()).hasSize(1)
				.containsEntry("security-password", "private-token");
			assertThat(second.getRuntime().getProperties().properties()).isEmpty();
			assertThat(second.getRuntime().getSecurityProperties().properties()).isEmpty();
		}
	}

	@Test
	void independentConnectionsHaveIndependentIdentityAndCancellation() {
		try (var first = new MockConnection(); var second = new MockConnection()) {
			var firstRuntime = first.getRuntime();
			var secondRuntime = second.getRuntime();
			var systemCancellation = firstRuntime.getCancellation();
			assertThat(systemCancellation).isNotSameAs(first.getCancellation());
			assertThat(systemCancellation).isNotSameAs(secondRuntime.getCancellation());
			assertThat(firstRuntime.getMember()).isSameAs(first.member());
			assertThat(firstRuntime.getMember().getUniqueId()).isNotEqualTo(secondRuntime.getMember().getUniqueId());
			first.close();
			assertThat(firstRuntime.isConnected()).isFalse();
			assertThat(first.getCancellation().isCancelInProgress()).isTrue();
			assertThat(systemCancellation.cancelInProgress()).isNull();
			assertThat(systemCancellation.isCancelInProgress()).isFalse();
			systemCancellation.checkCancelInProgress();
			Throwable cause = new IllegalStateException("caller-diagnostic");
			systemCancellation.checkCancelInProgress(cause);
			assertThat(systemCancellation.generateCancelledException(cause)).isNull();
			assertThat(secondRuntime.isConnected()).isTrue();
			assertThat(second.getCancellation().cancelInProgress()).isNull();
			second.getCancellation().checkCancelInProgress();
		}
	}
}
