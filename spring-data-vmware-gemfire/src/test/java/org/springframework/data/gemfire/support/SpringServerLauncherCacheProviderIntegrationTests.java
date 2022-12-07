/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.Cache;
import org.apache.geode.distributed.AbstractLauncher.Status;
import org.apache.geode.distributed.ServerLauncher;
import org.apache.geode.distributed.ServerLauncher.ServerState;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.SpringExtensions;

/**
 * Integration Tests {@link SpringServerLauncherCacheProvider} class.
 *
 * This test class focuses on testing isolated units of functionality in the
 * {@link org.apache.geode.distributed.ServerLauncherCacheProvider} class directly, mocking any dependencies
 * as appropriate, in order for the class to uphold it's contract.
 *
 * @author Dan Smith
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.distributed.ServerLauncher
 * @see org.springframework.context.ApplicationContext
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.data.gemfire.support.SpringServerLauncherCacheProvider
 */
public class SpringServerLauncherCacheProviderIntegrationTests extends IntegrationTestsSupport {

	@After
	public void tearDown() {

		SpringExtensions.safeDoOperation(() ->
			closeApplicationContext(SpringContextBootstrappingInitializer.getApplicationContext()));

		SpringContextBootstrappingInitializer.destroy();
	}

	@Test
	public void createCacheWithSpring() {

		String springXmlLocation = getClass().getSimpleName() + "-context.xml";

		ServerLauncher.Builder builder = new ServerLauncher.Builder();

		builder.setSpringXmlLocation(springXmlLocation);
		builder.setMemberName("TestMemberName");
		builder.setDisableDefaultServer(true);

		ServerLauncher launcher = builder.build();

		ServerState state = launcher.start();

		assertThat(state.getStatus()).isEqualTo(Status.ONLINE);

		ConfigurableApplicationContext applicationContext =
			SpringContextBootstrappingInitializer.getApplicationContext();

		Cache cache = applicationContext.getBean(Cache.class);

		assertThat(cache).isNotNull();
		assertThat(cache.getName()).isEqualTo("TestMemberName");
		assertThat(cache.getResourceManager().getCriticalHeapPercentage()).isEqualTo(55.0f);

		state = launcher.stop();

		assertThat(state.getStatus()).isEqualTo(Status.STOPPED);
	}
}
