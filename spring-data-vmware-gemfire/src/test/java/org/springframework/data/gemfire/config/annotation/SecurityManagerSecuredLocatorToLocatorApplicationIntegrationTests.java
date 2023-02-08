/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.distributed.DistributedSystem;
import org.apache.geode.distributed.Locator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Apache Geode Security Integration Tests testing Apache Geode Locator to Locator (application) authentication.
 *
 * @author John Blum
 * @see Test
 * @see DistributedSystem
 * @see Locator
 * @see Profile
 * @see org.springframework.data.gemfire.LocatorFactoryBean
 * @see EnableSecurity
 * @see LocatorApplication
 * @see ForkingClientServerIntegrationTestsSupport
 * @see ActiveProfiles
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.0.0
 */
@ActiveProfiles("locator-auth-client")
@ContextConfiguration(classes = SecurityManagerSecuredLocatorToLocatorApplicationIntegrationTests.LocatorAuthClient.class)
@RunWith(SpringRunner.class)
@SuppressWarnings("unused")
public class SecurityManagerSecuredLocatorToLocatorApplicationIntegrationTests
		extends ForkingClientServerIntegrationTestsSupport {

	private static ProcessWrapper locatorProcess;

	@BeforeClass
	public static void startGeodeLocator() throws IOException {

		int locatorPort = findAndReserveAvailablePort();

		locatorProcess= run(LocatorAuthServer.class,
			"-Dspring.profiles.active=locator-auth-server",
			String.format("-Dspring.data.gemfire.locator.port=%d", locatorPort));

		waitForServerToStart("localhost", locatorPort);

		System.setProperty("spring.data.gemfire.locators", String.format("localhost[%d]", locatorPort));
	}

	@AfterClass
	public static void stopGeodeLocator() {
		stop(locatorProcess);
		System.clearProperty("spring.data.gemfire.locators");
	}

	@Autowired
	private Locator locator;

	@Test
	public void locatorIsRunning() {

		assertThat(this.locator).isNotNull();

		DistributedSystem distributedSystem = this.locator.getDistributedSystem();

		assertThat(distributedSystem).isNotNull();
		assertThat(distributedSystem.isConnected()).isTrue();
		assertThat(distributedSystem.getName()).isEqualTo("LocatorAuthClient");
		assertThat(distributedSystem.getDistributedMember().getName()).isEqualTo("LocatorAuthClient");
		assertThat(distributedSystem.getAllOtherMembers()).hasSize(1);
	}

	@LocatorApplication(name = "LocatorAuthServer")
	@EnableSecurity(securityManagerClass = TestSecurityManager.class)
	@Profile("locator-auth-server")
	static class LocatorAuthServer {

		public static void main(String[] args) {
			runSpringApplication(LocatorAuthServer.class);
			block();
		}
	}

	@LocatorApplication(name = "LocatorAuthClient", port = 0)
	@EnableSecurity(securityUsername = TestSecurityManager.SECURITY_USERNAME, securityPassword = TestSecurityManager.SECURITY_PASSWORD)
	@Profile("locator-auth-client")
	static class LocatorAuthClient { }

}
