/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import java.io.File;

import org.junit.After;
import org.junit.Before;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;

/**
 * Abstract base test class that creates the Spring {@link ConfigurableApplicationContext} after each method (test case).
 * Used to properly destroy the beans defined inside Spring.
 *
 * @author Costin Leau
 * @author John Blum
 * @see ConfigurableApplicationContext
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 */
public abstract class RecreatingSpringApplicationContextTest extends SpringApplicationContextIntegrationTestsSupport {

	@Before
	public void createContext() {

		GenericXmlApplicationContext applicationContext = configureContext(new GenericXmlApplicationContext());

		applicationContext.load(location());
		applicationContext.registerShutdownHook();
		applicationContext.refresh();

		setApplicationContext(applicationContext);
	}

	protected abstract String location();

	protected <T extends ConfigurableApplicationContext> T configureContext(T context){
		return context;
	}

	@After
	public void cleanupAfterTests() {

		destroyAllGemFireMockObjects();

		for (String name : new File(".").list((file, filename) -> filename.startsWith("BACKUP"))) {
			new File(name).delete();
		}
	}
}
