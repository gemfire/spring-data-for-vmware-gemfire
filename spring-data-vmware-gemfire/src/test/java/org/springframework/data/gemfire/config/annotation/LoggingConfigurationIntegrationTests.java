/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.apache.geode.cache.GemFireCache;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.util.StringUtils;

/**
 * Integration Tests testing the configuration of Apache Geode Logging.
 *
 * @author John Blum
 * @see Properties
 * @see org.junit.Test
 * @see GemFireCache
 * @see ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see PropertiesPropertySource
 * @see ClientCacheApplication
 * @see EnableLogging
 * @see SpringApplicationContextIntegrationTestsSupport
 * @see PropertiesBuilder
 * @since 1.9.0
 */
public class LoggingConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private final AtomicReference<Properties> propertiesReference = new AtomicReference<>(null);

	@Before @After
	public void setupAndTearDown() {
		this.propertiesReference.set(null);
		deleteLogFiles();
	}

	private void assertGemFireCacheLogLevelAndLogFile(String logLevel, String logFile) {

		GemFireCache gemfireCache = getBean(GemFireCache.class);

		logFile = StringUtils.hasText(logFile) ? logFile : "";

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties distributedSystemProperties = gemfireCache.getDistributedSystem().getProperties();

		assertThat(distributedSystemProperties).isNotNull();
		assertThat(distributedSystemProperties.getProperty(GemFireProperties.LOG_LEVEL.getName())).isEqualTo(logLevel);
		assertThat(distributedSystemProperties.getProperty(GemFireProperties.LOG_FILE.getName())).isEqualTo(logFile);
	}

	private void deleteLogFiles() {

		FileFilter gemfireTestLogFileFilter = file ->
			file.getName().startsWith("gemfire-logging-test") & file.getName().endsWith(".log");

		File[] files = new File(System.getProperty("user.dir")).listFiles(gemfireTestLogFileFilter);

		Arrays.stream(ArrayUtils.nullSafeArray(files, File.class)).forEach(File::delete);
	}

	@Override
	protected ConfigurableApplicationContext processBeforeRefresh(ConfigurableApplicationContext applicationContext) {

		Optional.ofNullable(this.propertiesReference.get())
			.ifPresent(properties -> applicationContext.getEnvironment()
				.getPropertySources()
				.addFirst(new PropertiesPropertySource("Test Properties", properties)));

		return super.processBeforeRefresh(applicationContext);
	}

	private void with(Properties properties) {

		Optional.ofNullable(properties)
			.ifPresent(this.propertiesReference::set);
	}


	@Test
	public void clientCacheApplicationWithDefaultLoggingConfiguration() {

		newApplicationContext(ClientCacheApplicationWithDefaultLoggingTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("config", null);
	}

	@Test
	public void clientCacheApplicationWithLogLevelAttribute() {

		newApplicationContext(ClientCacheApplicationWithLogLevelTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("fine", null);
	}

	@Test
	public void clientCacheApplicationWithLogLevelProperty() {

		with(PropertiesBuilder.create().setProperty("spring.data.gemfire.cache.log-level", "info").build());

		newApplicationContext(ClientCacheApplicationWithLogLevelTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("info", null);

	}

	@Test
	public void withCustomLoggingEnabledClientCacheApplicationConfiguration() {

		newApplicationContext(ClientCacheApplicationWithDefaultLoggingTestConfiguration.class,
			CustomLoggingTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("warning", "gemfire-logging-test-zero.log");
	}

	@Test
	public void withCustomLoggingEnabledAndLogLevelPropertyConfiguredClientCacheApplicationConfiguration() {

		with(PropertiesBuilder.create()
			.setProperty("spring.data.gemfire.logging.level", "error")
			.setProperty("spring.data.gemfire.logging.log-file", "gemfire-logging-test-one.log")
			.build());

		newApplicationContext(ClientCacheApplicationWithLogLevelTestConfiguration.class,
			CustomLoggingTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("error", "gemfire-logging-test-one.log");
	}

	@Test
	public void withDefaultLoggingEnabledUsingClientCacheApplicationWithLogLevelConfiguration() {

		newApplicationContext(ClientCacheApplicationWithLogLevelTestConfiguration.class,
			DefaultLoggingTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("fine", null);
	}

	@Test
	public void withoutLoggingEnabledThenLoggingPropertiesHaveNoEffect() {

		with(PropertiesBuilder.create()
			.setProperty("spring.data.gemfire.logging.level", "error")
			.setProperty("spring.data.gemfire.logging.log-file", "gemfire-logging-test-two.log")
			.build());

		newApplicationContext(ClientCacheApplicationWithLogLevelTestConfiguration.class);

		assertGemFireCacheLogLevelAndLogFile("fine", null);
	}

	@ClientCacheApplication(name = "ClientCacheApplicationWithDefaultLogging")
	static class ClientCacheApplicationWithDefaultLoggingTestConfiguration { }

	@ClientCacheApplication(name = "ClientCacheApplicationWithLogLevel", logLevel = "fine")
	static class ClientCacheApplicationWithLogLevelTestConfiguration { }

	@EnableLogging
	static class DefaultLoggingTestConfiguration { }

	@EnableLogging(logLevel = "warning", logFile = "gemfire-logging-test-zero.log")
	static class CustomLoggingTestConfiguration { }

}
