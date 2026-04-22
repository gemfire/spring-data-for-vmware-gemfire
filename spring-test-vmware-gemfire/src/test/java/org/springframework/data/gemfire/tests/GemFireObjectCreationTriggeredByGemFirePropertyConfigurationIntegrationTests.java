/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-01: Replaced Apache Geode ClientCache with GudClientCache
 */

package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.data.gemfire.gud.api.GudClientCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.tests.objects.geode.security.TestSecurityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for Apache Geode & VMware GemFire {@link Object} creation when the {@link Object} configuration
 * and {@link Class} type is expressed in {@link Properties}.
 *
 * @author John Blum
 * @see Test
 * @see EnableSecurity
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class GemFireObjectCreationTriggeredByGemFirePropertyConfigurationIntegrationTests
		extends IntegrationTestsSupport {

	@Autowired
	private GudClientCache gemfireCache;

	@Test
	public void securityManagerIsPresent() {

		assertThat(this.gemfireCache).isNotNull();
		assertThat(TestSecurityManager.getInstance()).isInstanceOf(TestSecurityManager.class);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableSecurity(securityManagerClassName = "org.springframework.data.gemfire.tests.objects.geode.security.TestSecurityManager")
	static class TestConfiguration { }

}
