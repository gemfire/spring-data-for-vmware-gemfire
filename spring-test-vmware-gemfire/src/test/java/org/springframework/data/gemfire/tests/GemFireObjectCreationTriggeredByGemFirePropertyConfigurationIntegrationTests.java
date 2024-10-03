/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.GemFireCache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.EnableSecurity;
import org.springframework.data.gemfire.config.annotation.PeerCacheApplication;
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
 * @see PeerCacheApplication
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
	private GemFireCache gemfireCache;

	@Test
	public void securityManagerIsPresent() {

		assertThat(this.gemfireCache).isNotNull();
		assertThat(TestSecurityManager.getInstance()).isInstanceOf(TestSecurityManager.class);
	}

	@PeerCacheApplication
	@EnableGemFireMockObjects
	@EnableSecurity(securityManagerClassName = "org.springframework.data.gemfire.tests.objects.geode.security.TestSecurityManager")
	static class TestConfiguration { }

}
