/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for Apache Geode Integrated Security using an Apache Shiro INI security configuration resource.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see AbstractGeodeSecurityIntegrationTests
 * @see ActiveProfiles
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("apache-geode-client")
@ContextConfiguration(classes = AbstractGeodeSecurityIntegrationTests.GeodeClientConfiguration.class)
@DirtiesContext
public class ApacheShiroIniSecurityIntegrationTests extends AbstractGeodeSecurityIntegrationTests {

	protected static final String SHIRO_INI_CONFIGURATION_PROFILE = "shiro-ini-configuration";

	@BeforeClass
	public static void startGeodeServer() throws IOException {
		runGeodeServer(SHIRO_INI_CONFIGURATION_PROFILE);
	}

	@Configuration
	@EnableSecurity(shiroIniResourcePath = "shiro.ini")
	@Profile(SHIRO_INI_CONFIGURATION_PROFILE)
	public static class ApacheShiroIniConfiguration { }

}
