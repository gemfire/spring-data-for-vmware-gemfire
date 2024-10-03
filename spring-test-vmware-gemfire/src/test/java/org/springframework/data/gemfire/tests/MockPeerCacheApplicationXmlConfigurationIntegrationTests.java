/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import jakarta.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.distributed.DistributedSystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for an Apache Geode mock peer {@link Cache} application.
 *
 * @author John Blum
 * @see Properties
 * @see Test
 * @see Cache
 * @see GemFireCache
 * @see DistributedSystem
 * @see IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.context.GemFireMockObjectsApplicationContextInitializer
 * @since 0.0.26
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class MockPeerCacheApplicationXmlConfigurationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private GemFireCache cache;

	@Resource(name = "Example")
	private Region<Object, Object> example;

	@Test
	public void gemfireCacheConfigurationIsCorrect() {

		assertThat(this.cache).isNotNull();
		assertThat(this.cache.getName())
			.isEqualTo(MockPeerCacheApplicationXmlConfigurationIntegrationTests.class.getSimpleName());
		assertThat(this.example).isNotNull();
		assertThat(this.example.getName()).isEqualTo("Example");
		assertThat(this.cache.getRegion(this.example.getFullPath())).isEqualTo(this.example);

		DistributedSystem distributedSystem = this.cache.getDistributedSystem();

		assertThat(distributedSystem).isNotNull();
		assertThat(distributedSystem.getName()).isEqualTo(this.cache.getName());

		Properties distributedSystemProperties = distributedSystem.getProperties();

		assertThat(distributedSystemProperties).isNotNull();
		assertThat(distributedSystemProperties.containsKey("disable-auto-reconnect")).isTrue();
		assertThat(Boolean.parseBoolean(distributedSystemProperties.getProperty("disable-auto-reconnect"))).isTrue();
		assertThat(distributedSystemProperties.containsKey("use-cluster-configuration")).isTrue();
		assertThat(Boolean.parseBoolean(distributedSystemProperties.getProperty("use-cluster-configuration"))).isFalse();
	}
}
