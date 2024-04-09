/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fork;

import java.io.File;

import org.apache.geode.distributed.ServerLauncher;

import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.tests.process.ProcessUtils;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;

/**
 * The {@link GemFireBasedServerProcess} class is a main Java class used to launch a GemFire Server
 * using GemFire's {@link ServerLauncher} API.
 *
 * @author John Blum
 * @see org.apache.geode.distributed.ServerLauncher
 * @since 1.7.0
 */
public class GemFireBasedServerProcess {

	private static final String GEMFIRE_LOG_LEVEL = "error";
	private static final String GEMIRE_NAME = "SpringDataGemFireServer";
	private static final String GEMFIRE_USE_CLUSTER_CONFIGURATION = "false";

	public static void main(String[] args) throws Throwable {

		runServer(args);

		registerShutdownHook();

		ProcessUtils.writePid(new File(FileSystemUtils.WORKING_DIRECTORY, getServerProcessControlFilename()),
			ProcessUtils.currentPid());

		ProcessUtils.waitForStopSignal();
	}

	private static ServerLauncher runServer(String[] args) {

		ServerLauncher serverLauncher = buildServerLauncher(args);

		// start the GemFire Server process...
		serverLauncher.start();

		return serverLauncher;
	}

	private static ServerLauncher buildServerLauncher(String[] args) {

		return new ServerLauncher.Builder(args)
			.setMemberName(getProperty("gemfire.name", GEMIRE_NAME))
			.setCommand(ServerLauncher.Command.START)
			.setDisableDefaultServer(true)
			.setRedirectOutput(false)
			.set(GemFireProperties.LOG_LEVEL.getName(), getProperty("spring.data.gemfire.log-level", GEMFIRE_LOG_LEVEL))
			.set(GemFireProperties.USE_CLUSTER_CONFIGURATION.getName(), getProperty("spring.data.gemfire.use-cluster-configuration", GEMFIRE_USE_CLUSTER_CONFIGURATION))
			.build();
	}

	private static String getProperty(String name, String defaultValue) {
		return System.getProperty(name, defaultValue);
	}

	private static void registerShutdownHook() {

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {

			ServerLauncher serverLauncher = ServerLauncher.getInstance();

			if (serverLauncher != null) {
				serverLauncher.stop();
			}
		}));
	}

	public static String getServerProcessControlFilename() {
		return GemFireBasedServerProcess.class.getSimpleName().toLowerCase().concat(".pid");
	}
}
