/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.io.IOException;

import org.apache.geode.internal.security.shiro.GeodePermissionResolver;
import org.apache.shiro.realm.text.PropertiesRealm;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration tests for Apache Geode Integrated Security using an Apache Shiro Realm security configuration
 * as a Spring managed bean in a Spring {@link org.springframework.context.ApplicationContext}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.shiro.realm.text.PropertiesRealm
 * @see org.springframework.data.gemfire.config.annotation.AbstractGeodeSecurityIntegrationTests
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 2.0.0
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("apache-geode-client")
@ContextConfiguration(classes = AbstractGeodeSecurityIntegrationTests.GeodeClientConfiguration.class)
@DirtiesContext
public class ApacheShiroRealmSecurityIntegrationTests extends AbstractGeodeSecurityIntegrationTests {

	protected static final String SHIRO_REALM_CONFIGURATION_PROFILE = "shiro-realm-configuration";

	@BeforeClass
	public static void startGeodeServer() throws IOException {
		runGeodeServer(SHIRO_REALM_CONFIGURATION_PROFILE);
	}

	@Configuration
	@EnableSecurity
	@Profile(SHIRO_REALM_CONFIGURATION_PROFILE)
	@SuppressWarnings("unused")
	public static class ApacheShiroRealmConfiguration {

		@Bean
		public PropertiesRealm shiroRealm() {

			PropertiesRealm propertiesRealm = new PropertiesRealm();

			propertiesRealm.setResourcePath("classpath:shiro.properties");
			propertiesRealm.setPermissionResolver(new GeodePermissionResolver());

			return propertiesRealm;
		}
	}
}
