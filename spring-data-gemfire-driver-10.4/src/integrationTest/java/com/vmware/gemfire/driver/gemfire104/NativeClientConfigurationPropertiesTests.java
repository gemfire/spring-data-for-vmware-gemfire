/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.gemfire104;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.geode.distributed.ConfigurationProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.vmware.gemfire.driver.spi.CacheConfiguration;
import com.vmware.gemfire.driver.spi.ClientConfigurationProperties;
import com.vmware.gemfire.driver.spi.DriverBindException;

class NativeClientConfigurationPropertiesTests {

	@Test
	void monitoringPropertiesHaveEffectiveNativeReadback(@TempDir Path directory) {
		var monitoring = Map.ofEntries(
			Map.entry(ClientConfigurationProperties.ARCHIVE_DISK_SPACE_LIMIT, "20"),
			Map.entry(ClientConfigurationProperties.ARCHIVE_FILE_SIZE_LIMIT, "10"),
			Map.entry(ClientConfigurationProperties.ENABLE_TIME_STATISTICS, "true"),
			Map.entry(ClientConfigurationProperties.LOG_DISK_SPACE_LIMIT, "20"),
			Map.entry(ClientConfigurationProperties.LOG_FILE, directory.resolve("client.log").toString()),
			Map.entry(ClientConfigurationProperties.LOG_FILE_SIZE_LIMIT, "10"),
			Map.entry(ClientConfigurationProperties.LOG_LEVEL, "error"),
			Map.entry(ClientConfigurationProperties.MEMBER_TIMEOUT, "6000"),
			Map.entry(ClientConfigurationProperties.STATISTIC_ARCHIVE_FILE, directory.resolve("stats.gfs").toString()),
			Map.entry(ClientConfigurationProperties.STATISTIC_SAMPLE_RATE, "500"),
			Map.entry(ClientConfigurationProperties.STATISTIC_SAMPLING_ENABLED, "false"),
			Map.entry(ClientConfigurationProperties.THREAD_MONITOR_ENABLED, "false"),
			Map.entry(ClientConfigurationProperties.THREAD_MONITOR_INTERVAL, "70000"),
			Map.entry(ClientConfigurationProperties.THREAD_MONITOR_TIME_LIMIT, "140000"));
		var settings = new HashMap<>(monitoring);
		settings.put("mcast-port", "0");
		settings.put(ClientConfigurationProperties.LOCATORS, "");
		var configuration = new CacheConfiguration(settings);
		try (var connection = new GemFire104Driver().connect(configuration)) {
			var expected = new HashMap<>(monitoring);
			// The native setter accepts 500 ms but normalizes it to the 1,000 ms sampling floor.
			expected.put(ClientConfigurationProperties.STATISTIC_SAMPLE_RATE, "1000");
			var actual = connection.getRuntime().getProperties().properties();
			expected.forEach((name, value) -> assertThat(actual.getProperty(name)).as(name).isEqualTo(value));
			assertThat(configuration.properties().getProperty(ClientConfigurationProperties.STATISTIC_SAMPLE_RATE))
				.isEqualTo("500");
		}
	}

	@Test
	void invalidMonitoringValuesFailDuringNativeConnectionCreation() {
		var invalid = Map.of(
			ClientConfigurationProperties.ARCHIVE_DISK_SPACE_LIMIT, "-1",
			ClientConfigurationProperties.ARCHIVE_FILE_SIZE_LIMIT, "-1",
			ClientConfigurationProperties.LOG_DISK_SPACE_LIMIT, "-1",
			ClientConfigurationProperties.LOG_FILE_SIZE_LIMIT, "-1",
			ClientConfigurationProperties.LOG_LEVEL, "private-token",
			ClientConfigurationProperties.MEMBER_TIMEOUT, "9",
			ClientConfigurationProperties.STATISTIC_SAMPLE_RATE, "60001",
			ClientConfigurationProperties.THREAD_MONITOR_INTERVAL, "-1",
			ClientConfigurationProperties.THREAD_MONITOR_TIME_LIMIT, "-1");
		invalid.forEach((name, value) -> {
			var settings = new HashMap<>(Map.of("mcast-port", "0", ClientConfigurationProperties.LOCATORS, "",
				ClientConfigurationProperties.LOG_LEVEL, "error"));
			settings.put(name, value);
			assertThatThrownBy(() -> {
				try (var ignored = new GemFire104Driver().connect(new CacheConfiguration(settings))) {
					// Close an unexpected successful connection before the assertion reports failure.
				}
			}).as(name).isInstanceOf(DriverBindException.class)
				.hasMessage("Unable to create the GemFire cache.")
				.hasCauseInstanceOf(IllegalArgumentException.class);
		});
	}

	@Test
	void approvedClientNamesMatchThePinnedNativeDriver() throws ReflectiveOperationException {
		// Fixed client scope from task 72; adding a native constant requires a scope decision.
		String[] expectedNames = """
			ARCHIVE_DISK_SPACE_LIMIT
			ARCHIVE_FILE_SIZE_LIMIT
			AVAILABILITY_ZONE
			CACHE_XML_FILE
			CLUSTER_SSL_CIPHERS
			CLUSTER_SSL_ENABLED
			CLUSTER_SSL_KEYSTORE
			CLUSTER_SSL_KEYSTORE_PASSWORD
			CLUSTER_SSL_KEYSTORE_TYPE
			CLUSTER_SSL_PREFIX
			CLUSTER_SSL_PROTOCOLS
			CLUSTER_SSL_REQUIRE_AUTHENTICATION
			CLUSTER_SSL_TRUSTSTORE
			CLUSTER_SSL_TRUSTSTORE_PASSWORD
			CONFLATE_EVENTS
			DELTA_PROPAGATION
			DISTRIBUTED_SYSTEM_ID
			DURABLE_CLIENT_ID
			DURABLE_CLIENT_TIMEOUT
			ENABLE_TIME_STATISTICS
			GROUPS
			LOCATORS
			LOCK_MEMORY
			LOG_DISK_SPACE_LIMIT
			LOG_FILE
			LOG_FILE_SIZE_LIMIT
			LOG_LEVEL
			MEMBER_TIMEOUT
			NAME
			REDUNDANCY_ZONE
			ROLES
			SECURITY_CLIENT_AUTH_INIT
			SECURITY_CLIENT_DHALGO
			SECURITY_LOG_FILE
			SECURITY_LOG_LEVEL
			SECURITY_PREFIX
			SERIALIZABLE_OBJECT_FILTER
			SERVER_SSL_CIPHERS
			SERVER_SSL_ENABLED
			SERVER_SSL_KEYSTORE
			SERVER_SSL_KEYSTORE_PASSWORD
			SERVER_SSL_KEYSTORE_TYPE
			SERVER_SSL_PROTOCOLS
			SERVER_SSL_REQUIRE_AUTHENTICATION
			SERVER_SSL_TRUSTSTORE
			SERVER_SSL_TRUSTSTORE_PASSWORD
			SSL_CIPHERS
			SSL_CLIENT_PROTOCOLS
			SSL_DEFAULT_ALIAS
			SSL_ENABLED_COMPONENTS
			SSL_ENDPOINT_IDENTIFICATION_ENABLED
			SSL_KEYSTORE
			SSL_KEYSTORE_PASSWORD
			SSL_KEYSTORE_TYPE
			SSL_LOCATOR_ALIAS
			SSL_PARAMETER_EXTENSION
			SSL_PROTOCOLS
			SSL_REQUIRE_AUTHENTICATION
			SSL_SERVER_ALIAS
			SSL_TRUSTSTORE
			SSL_TRUSTSTORE_PASSWORD
			SSL_TRUSTSTORE_TYPE
			SSL_USE_DEFAULT_CONTEXT
			STATISTIC_ARCHIVE_FILE
			STATISTIC_SAMPLE_RATE
			STATISTIC_SAMPLING_ENABLED
			THREAD_MONITOR_ENABLED
			THREAD_MONITOR_INTERVAL
			THREAD_MONITOR_TIME_LIMIT
			VALIDATE_SERIALIZABLE_OBJECTS
			""".strip().split("\\s+");
		var fields = ClientConfigurationProperties.class.getDeclaredFields();
		assertThat(Arrays.stream(fields).map(field -> field.getName()).toList())
			.containsExactlyInAnyOrder(expectedNames);
		for (var field : fields) {
			assertThat(field.getType()).as(field.getName()).isEqualTo(String.class);
			assertThat(field.getModifiers()).as(field.getName())
				.isEqualTo(Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL);
			assertThat(field.get(null)).as(field.getName())
				.isEqualTo(ConfigurationProperties.class.getField(field.getName()).get(null));
		}
	}
}
