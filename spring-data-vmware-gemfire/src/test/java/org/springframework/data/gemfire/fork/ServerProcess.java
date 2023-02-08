/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fork;

import java.io.File;
import java.io.IOException;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.gemfire.tests.process.ProcessUtils;
import org.springframework.data.gemfire.tests.util.FileSystemUtils;
import org.springframework.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ServerProcess} class is a main Java class using Spring Data for Apache Geode to configure and bootstrap
 * an Apache Geode Server process.
 *
 * @author John Blum
 * @see ConfigurableApplicationContext
 * @see ClassPathXmlApplicationContext
 * @since 1.5.2
 */
public class ServerProcess {

	private static final Logger logger = LoggerFactory.getLogger(ServerProcess.class);

	public static void main(String[] args) throws Throwable {

		ConfigurableApplicationContext applicationContext = null;

		try {
			applicationContext = newApplicationContext(args);
			waitForShutdown();
		}
		catch (Throwable cause) {
			logger.debug("", cause);
			throw cause;
		}
		finally {
			close(applicationContext);
		}
	}

	private static ConfigurableApplicationContext newApplicationContext(String[] configLocations) {

		Assert.notEmpty(configLocations, String.format("Usage: >java -cp ... %1$s %2$s",
			ServerProcess.class.getName(), "classpath:/to/applicationContext.xml"));

		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocations);

		applicationContext.registerShutdownHook();

		return applicationContext;
	}

	private static boolean close(ConfigurableApplicationContext applicationContext) {

		if (applicationContext != null) {

			applicationContext.close();

			return !(applicationContext.isRunning() || applicationContext.isActive());
		}

		return true;
	}

	private static void waitForShutdown() throws IOException {

		File serverProcessControlFile = new File(FileSystemUtils.WORKING_DIRECTORY, getServerProcessControlFilename());

		ProcessUtils.writePid(serverProcessControlFile, ProcessUtils.currentPid());
		ProcessUtils.waitForStopSignal();
	}

	public static String getServerProcessControlFilename() {
		return ServerProcess.class.getSimpleName().toLowerCase().concat(".pid");
	}
}
