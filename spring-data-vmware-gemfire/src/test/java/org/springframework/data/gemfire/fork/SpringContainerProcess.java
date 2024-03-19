/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.fork;

import java.util.Scanner;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SpringContainerProcess} launches a Spring {@link ConfigurableApplicationContext} in this JVM process.
 *
 * @author David Turanski
 * @author John Blum
 * @see ConfigurableApplicationContext
 * @see ClassPathXmlApplicationContext
 */
public class SpringContainerProcess {

	private static final Logger logger = LoggerFactory.getLogger(SpringContainerProcess.class);

	public static void main(String[] args) {
		ConfigurableApplicationContext applicationContext = null;

		try {
			applicationContext = newApplicationContext(args);
			waitForShutdown(applicationContext);
		}
		catch (Exception e) {
			logger.debug("", e);
			System.exit(1);
		}
		finally {
			close(applicationContext);
		}
	}

	private static ConfigurableApplicationContext newApplicationContext(String[] configLocations) {
		ConfigurableApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocations);
		applicationContext.registerShutdownHook();
		return applicationContext;
	}

	private static void close(ConfigurableApplicationContext applicationContext) {
		if (applicationContext != null) {
			applicationContext.close();
		}
	}

	@SuppressWarnings({ "unused" })
	private static void waitForShutdown(ConfigurableApplicationContext applicationContext) {
		Scanner scanner = new Scanner(System.in);
		scanner.nextLine();
	}
}
