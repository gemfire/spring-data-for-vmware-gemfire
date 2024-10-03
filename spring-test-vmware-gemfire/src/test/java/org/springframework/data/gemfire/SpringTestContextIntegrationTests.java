/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Spring's {@link TestContext}.
 *
 * @author John Blum
 * @see ApplicationContext
 * @see DirtiesContext
 * @see ContextConfiguration
 * @see TestContext
 * @see SpringRunner
 * @since 0.0.23
 */
@RunWith(SpringRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@ContextConfiguration
@SuppressWarnings("unused")
public class SpringTestContextIntegrationTests {

	private static final Set<Integer> applicationContextsIdentityHashCodes =
		Collections.synchronizedSet(new HashSet<>());

	@Autowired
	private ApplicationContext applicationContext;

	@AfterClass
	public static void runAfterTestClass() {
		assertThat(applicationContextsIdentityHashCodes).hasSize(1);
	}

	@Before
	public void setup() {
		applicationContextsIdentityHashCodes.add(System.identityHashCode(this.applicationContext));
	}

	@Test
	//@DirtiesContext
	public void testCaseOne() {
		assertThat(this.applicationContext).isNotNull();
	}

	@Test
	public void testCaseTwo() {
		assertThat(this.applicationContext).isNotNull();
	}

	@Test
	public void testCaseThree() {
		assertThat(this.applicationContext).isNotNull();
	}

	@Configuration
	static class TestConfiguration { }

}
