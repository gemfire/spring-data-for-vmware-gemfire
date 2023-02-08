/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.After;
import org.junit.Test;

import org.apache.geode.cache.Cache;
import org.apache.geode.distributed.ServerLauncher;
import org.apache.geode.distributed.ServerLauncherCacheProvider;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * Unit Tests testing the contract and functionality of the {@link SpringServerLauncherCacheProvider} class.
 *
 * This test class focuses on testing isolated units of functionality in the {@link ServerLauncherCacheProvider} class
 * directly, mocking any dependencies as appropriate, in order for the class to uphold it's contract.
 *
 * @author Dan Smith
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see Cache
 * @see ServerLauncher
 * @see ServerLauncherCacheProvider
 * @see org.springframework.context.ApplicationContext
 * @see ConfigurableApplicationContext
 * @see SpringServerLauncherCacheProvider
 */
public class SpringServerLauncherCacheProviderUnitTests extends IntegrationTestsSupport {

	private Properties singletonProperties(String propertyName, String propertyValue) {
		return PropertiesBuilder.create().setProperty(propertyName, propertyValue).build();
	}

	@After
	public void tearDown() {
		SpringContextBootstrappingInitializer.destroy();
	}

	@Test
	public void createsCacheWhenSpringXmlLocationIsSpecified() {

		Cache mockCache = mock(Cache.class);
		ConfigurableApplicationContext mockApplicationContext = mock(ConfigurableApplicationContext.class);
		ServerLauncher mockServerLauncher = mock(ServerLauncher.class);

		SpringContextBootstrappingInitializer.applicationContext = mockApplicationContext;

		when(mockServerLauncher.isSpringXmlLocationSpecified()).thenReturn(true);
		when(mockServerLauncher.getSpringXmlLocation()).thenReturn("test-context.xml");
		when(mockServerLauncher.getMemberName()).thenReturn("TEST");
		when(mockApplicationContext.getBean(eq(Cache.class))).thenReturn(mockCache);

		final SpringContextBootstrappingInitializer initializer = mock(SpringContextBootstrappingInitializer.class);

		SpringServerLauncherCacheProvider provider = spy(new SpringServerLauncherCacheProvider());

		doReturn(initializer).when(provider).newSpringContextBootstrappingInitializer();

		Properties expectedParameters =
			singletonProperties(SpringContextBootstrappingInitializer.CONTEXT_CONFIG_LOCATIONS_PARAMETER,
				"test-context.xml");

		assertThat(provider.createCache(null, mockServerLauncher)).isEqualTo(mockCache);

		verify(mockServerLauncher, times(1)).isSpringXmlLocationSpecified();
		verify(mockServerLauncher, times(1)).getSpringXmlLocation();
		verify(mockServerLauncher, times(1)).getMemberName();
		verify(mockApplicationContext, times(1)).getBean(eq(Cache.class));
		verify(initializer).init(eq(expectedParameters));
	}

	@Test
	public void doesNothingWhenSpringXmlLocationNotSpecified() {

		ServerLauncher launcher = mock(ServerLauncher.class);

		when(launcher.isSpringXmlLocationSpecified()).thenReturn(false);

		assertThat(new SpringServerLauncherCacheProvider().createCache(null, launcher)).isNull();

		verify(launcher, times(1)).isSpringXmlLocationSpecified();
	}
}
