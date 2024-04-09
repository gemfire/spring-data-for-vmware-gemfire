/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fork;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.geode.distributed.Locator;
import org.apache.geode.distributed.LocatorLauncher;
import org.apache.geode.distributed.internal.InternalLocator;

import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.tests.process.ProcessUtils;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.data.gemfire.tests.util.ThreadUtils;
import org.springframework.data.gemfire.util.ArrayUtils;

/**
 * The {@link LocatorProcess} class is a main Java class that is used fork and launch an Apache Geode {@link Locator}
 * process using the {@link LocatorLauncher} class.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.Locator
 * @see org.apache.geode.distributed.LocatorLauncher
 * @since 1.5.0
 */
public class LocatorProcess {

	private static final int DEFAULT_LOCATOR_PORT = 20668;

	private static final String GEMFIRE_NAME = "SpringDataGemFireLocator";
	private static final String GEMFIRE_LOG_LEVEL = "config";
	private static final String HOSTNAME_FOR_CLIENTS = "localhost";

	public static void main(String... args) throws IOException {

		//runLocator();
		runInternalLocator();

		registerShutdownHook();

		waitForLocatorStart(TimeUnit.SECONDS.toMillis(30));

		ProcessUtils.writePid(new File(FileSystemUtils.WORKING_DIRECTORY, getLocatorProcessControlFilename()),
			ProcessUtils.currentPid());

		ProcessUtils.waitForStopSignal();
	}

	public static String getLocatorProcessControlFilename() {
		return LocatorProcess.class.getSimpleName().toLowerCase().concat(".pid");
	}

	@SuppressWarnings("unused")
	private static InternalLocator runInternalLocator() throws IOException {

		String hostnameForClients =
			System.getProperty("spring.data.gemfire.locator.hostname-for-clients", HOSTNAME_FOR_CLIENTS);

		int locatorPort = Integer.getInteger("spring.data.gemfire.locator.port", DEFAULT_LOCATOR_PORT);

		boolean loadClusterConfigurationFromDirectory =
			Boolean.getBoolean("spring.data.gemfire.load-cluster-configuration");

		Properties distributedSystemProperties = new Properties();

		distributedSystemProperties.setProperty(GemFireProperties.ENABLE_CLUSTER_CONFIGURATION.getName(),
			String.valueOf(Boolean.getBoolean("spring.data.gemfire.enable-cluster-configuration")));
		distributedSystemProperties.setProperty(GemFireProperties.JMX_MANAGER.getName(),
			System.getProperty("spring.data.gemfire.jmx-manager", Boolean.TRUE.toString()));
		distributedSystemProperties.setProperty(GemFireProperties.JMX_MANAGER_START.getName(),
			System.getProperty("spring.data.gemfire.jmx-manager-start", Boolean.FALSE.toString()));
		distributedSystemProperties.setProperty(GemFireProperties.LOAD_CLUSTER_CONFIGURATION_FROM_DIR.getName(),
			String.valueOf(loadClusterConfigurationFromDirectory));
		distributedSystemProperties.setProperty(GemFireProperties.LOG_LEVEL.getName(),
			System.getProperty("spring.data.gemfire.log-level", GEMFIRE_LOG_LEVEL));

		return InternalLocator.startLocator(locatorPort, null, null, null,
			null, true, distributedSystemProperties, hostnameForClients);
	}

	@SuppressWarnings("unused")
	private static LocatorLauncher runLocator() {

		LocatorLauncher locatorLauncher = buildLocatorLauncher();

		// Start a Pivotal GemFire Locator process...
		locatorLauncher.start();

		return locatorLauncher;
	}

	private static LocatorLauncher buildLocatorLauncher() {

		return new LocatorLauncher.Builder()
			.setMemberName(GEMFIRE_NAME)
			.setHostnameForClients(getProperty("spring.data.gemfire.hostname-for-clients", HOSTNAME_FOR_CLIENTS))
			.setPort(getInteger("spring.data.gemfire.locator.port", DEFAULT_LOCATOR_PORT))
			.setRedirectOutput(false)
			.set(GemFireProperties.ENABLE_CLUSTER_CONFIGURATION.getName(),
				String.valueOf(getBoolean("spring.data.gemfire.enable-cluster-configuration")))
			.set(GemFireProperties.JMX_MANAGER.getName(), Boolean.TRUE.toString())
			.set(GemFireProperties.JMX_MANAGER_START.getName(), Boolean.FALSE.toString())
			.set(GemFireProperties.LOAD_CLUSTER_CONFIGURATION_FROM_DIR.getName(),
				String.valueOf(getBoolean("spring.data.gemfire.load-cluster-configuration")))
			.set(GemFireProperties.LOG_LEVEL.getName(),
				getProperty("spring.data.gemfire.log-level", GEMFIRE_LOG_LEVEL))
			.build();
	}

	private static boolean getBoolean(String name) {
		return Boolean.getBoolean(name);
	}

	private static int getInteger(String name, int defaultValue) {
		return Integer.getInteger(name, defaultValue);
	}

	private static String getProperty(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}

	private static void registerShutdownHook() {

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {

			Locator locator = GemfireUtils.getLocator();

			if (locator != null) {
				locator.stop();
			}

			Arrays.stream(ArrayUtils.nullSafeArray(FileSystemUtils.WORKING_DIRECTORY.listFiles((dir, name) -> name.startsWith("vf.gf") && name.endsWith(".pid")), File.class))
				.forEach(File::delete);
		}));
	}

	private static void waitForLocatorStart(long milliseconds) {

		InternalLocator locator = InternalLocator.getLocator();

		if (isClusterConfigurationEnabled(locator)) {
			ThreadUtils.timedWait(milliseconds, 500L, () -> !locator.isSharedConfigurationRunning());
		}
		else {
			LocatorLauncher.getInstance().waitOnStatusResponse(
				milliseconds, Math.min(500L, milliseconds), TimeUnit.MILLISECONDS);
		}
	}

	private static boolean isClusterConfigurationEnabled(Locator locator) {

		return locator != null && Boolean.parseBoolean(locator.getDistributedSystem().getProperties()
			.getProperty("enable-cluster-configuration"));
	}
}
