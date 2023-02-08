/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.Locator;
import org.apache.geode.security.AuthenticationFailedException;
import org.apache.geode.security.ResourcePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.GemFireProperties;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of a "secure" Apache Geode Locator.
 *
 * @author John Blum
 * @see Properties
 * @see org.junit.Test
 * @see DistributedSystem
 * @see Locator
 * @see GemFireProperties
 * @see LocatorApplication
 * @see EnableSecurity
 * @see IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.3.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class SecureLocatorApplicationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private Locator locator;

	@BeforeClass
	public static void setup() {
		System.setProperty(ApacheShiroSecurityConfiguration.ApacheShiroPresentCondition.SPRING_DATA_GEMFIRE_SECURITY_SHIRO_ENABLED,
			Boolean.FALSE.toString());
	}

	@AfterClass
	public static void tearDown() {
		System.clearProperty(ApacheShiroSecurityConfiguration.ApacheShiroPresentCondition.SPRING_DATA_GEMFIRE_SECURITY_SHIRO_ENABLED);
	}

	@Test
	public void locatorIsSecure() {

		assertThat(this.locator).isNotNull();

		DistributedSystem distributedSystem = this.locator.getDistributedSystem();

		assertThat(distributedSystem).isNotNull();
		assertThat(distributedSystem.getProperties()).isNotNull();
		assertThat(distributedSystem.getProperties().getProperty(GemFireProperties.SECURITY_MANAGER.getName()))
			.isEqualTo(TestSecurityManager.class.getName());
	}

	@LocatorApplication(port = 0)
	@EnableSecurity(securityManagerClass = TestSecurityManager.class)
	//@EnableSecurity(securityManagerClassName = "org.springframework.data.gemfire.config.annotation.TestSecurityManager")
	static class TestConfiguration { }

	static final class MockSecurityManager implements org.apache.geode.security.SecurityManager {

		@Override
		public Object authenticate(Properties credentials) throws AuthenticationFailedException {
			throw new AuthenticationFailedException("Identity could not be verified");
		}

		@Override
		public boolean authorize(Object principal, ResourcePermission permission) {
			return false;
		}
	}
}
