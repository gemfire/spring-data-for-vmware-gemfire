/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-01: Replaced Apache Geode ClientCache and DistributedSystem with GudClientCache and GudDistributedSystem
 * 2026-04-02: Replaced spy(new PoolFactoryBean()) with mock(PoolFactoryBean.class) — PoolFactoryBean is now abstract
 */

package org.springframework.data.gemfire.tests.mock.beans.factory.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Properties;

import org.junit.Test;

import org.springframework.data.gemfire.gud.api.GudClientCache;
import org.springframework.data.gemfire.gud.api.GudDistributedSystem;

import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.util.PropertiesBuilder;

/**
 * Unit Tests for {@link GemFireMockObjectsBeanPostProcessor}.
 *
 * @author John Blum
 * @see Properties
 * @see Test
 * @see org.mockito.Mockito
 * @see GudClientCache
 * @see GudDistributedSystem
 * @see ClientCacheFactoryBean
 * @see PoolFactoryBean
 * @see GemFireMockObjectsBeanPostProcessor
 * @see PropertiesBuilder
 * @since 0.0.16
 */
public class GemFireMockObjectsBeanPostProcessorUnitTests {

	@Test
	public void postProcessBeforeInitializationWithGemFireProperties() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = GemFireMockObjectsBeanPostProcessor.newInstance();

		assertThat(beanPostProcessor).isNotNull();
		assertThat(beanPostProcessor.isUsingSingletonCache())
			.isEqualTo(GemFireMockObjectsBeanPostProcessor.DEFAULT_USE_SINGLETON_CACHE);

		Properties gemfireProperties = new PropertiesBuilder()
			.setProperty("name", "postProcessBeforeInitializationWithGemFirePropertiesTest")
			.setProperty("log-level", "error")
			.build();

		assertThat(beanPostProcessor.getGemFireProperties()).isNotSameAs(gemfireProperties);

		assertThat(beanPostProcessor.postProcessBeforeInitialization(gemfireProperties,
			GemFireMockObjectsBeanPostProcessor.GEMFIRE_PROPERTIES_BEAN_NAME)).isEqualTo(gemfireProperties);

		assertThat(beanPostProcessor.getGemFireProperties()).isSameAs(gemfireProperties);
	}

	@Test
	public void postProcessBeforeInitializationWithNonGemFireProperties() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = GemFireMockObjectsBeanPostProcessor.newInstance();

		assertThat(beanPostProcessor).isNotNull();
		assertThat(beanPostProcessor.isUsingSingletonCache())
			.isEqualTo(GemFireMockObjectsBeanPostProcessor.DEFAULT_USE_SINGLETON_CACHE);

		Properties applicationProperties = new PropertiesBuilder()
			.setProperty("spring.application.name", "postProcessBeforeInitializationWithNonGemFirePropertiesTest")
			.setProperty("log-level", "error")
			.build();

		assertThat(beanPostProcessor.getGemFireProperties()).isNotSameAs(applicationProperties);

		assertThat(beanPostProcessor.postProcessBeforeInitialization(applicationProperties,
			"applicationProperties")).isEqualTo(applicationProperties);

		assertThat(beanPostProcessor.getGemFireProperties()).isNotSameAs(applicationProperties);
	}

	@Test
	public void postProcessBeforeInitializationWithClientCacheFactoryBean() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = GemFireMockObjectsBeanPostProcessor.newInstance(true);

		assertThat(beanPostProcessor).isNotNull();
		assertThat(beanPostProcessor.isUsingSingletonCache()).isTrue();

		ClientCacheFactoryBean clientCacheFactoryBean = spy(new ClientCacheFactoryBean());

		assertThat(beanPostProcessor.postProcessBeforeInitialization(clientCacheFactoryBean, "clientCache"))
			.isEqualTo(clientCacheFactoryBean);

		verify(clientCacheFactoryBean, times(1))
			.setCacheFactoryInitializer(isA(GemFireMockObjectsBeanPostProcessor.SpyingGudClientCacheFactoryInitializer.class));
	}

	@Test
	public void postProcessBeforeInitializationWithPoolFactoryBean() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = GemFireMockObjectsBeanPostProcessor.newInstance();

		assertThat(beanPostProcessor).isNotNull();
		assertThat(beanPostProcessor.isUsingSingletonCache())
			.isEqualTo(GemFireMockObjectsBeanPostProcessor.DEFAULT_USE_SINGLETON_CACHE);

		PoolFactoryBean poolFactoryBean = mock(PoolFactoryBean.class);

		assertThat(beanPostProcessor.postProcessBeforeInitialization(poolFactoryBean, "gemfirePool"))
			.isEqualTo(poolFactoryBean);

		verify(poolFactoryBean, times(1))
			.setPoolFactoryInitializer(isA(GemFireMockObjectsBeanPostProcessor.MockingPoolFactoryInitializer.class));
	}

	@Test
	public void postProcessBeforeInitializationWithRegularBean() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = spy(new GemFireMockObjectsBeanPostProcessor());

		Object bean = new Object();

		assertThat(beanPostProcessor.postProcessBeforeInitialization(bean, "regularBean")).isEqualTo(bean);

		verify(beanPostProcessor, times(1))
			.postProcessBeforeInitialization(eq(bean), eq("regularBean"));
		verifyNoMoreInteractions(beanPostProcessor);
	}

	@Test
	public void postProcessAfterInitializationWithGemFireCache() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = spy(new GemFireMockObjectsBeanPostProcessor());

		Properties gemfireProperties = new PropertiesBuilder()
			.setProperty("name", "postProcessAfterInitializationWithGemFireCacheTest")
			.setProperty("log-level", "error")
			.build();

		doReturn(gemfireProperties).when(beanPostProcessor).getGemFireProperties();

		GudClientCache mockCache = mock(GudClientCache.class);

		GudDistributedSystem mockDistributedSystem = mock(GudDistributedSystem.class);

		doReturn(mockDistributedSystem).when(mockCache).getDistributedSystem();

		assertThat(beanPostProcessor.postProcessAfterInitialization(mockCache, "gemfireCache"))
			.isEqualTo(mockCache);

		assertThat(mockCache.getDistributedSystem().getProperties()).isSameAs(gemfireProperties);

		verify(beanPostProcessor, times(1)).getGemFireProperties();
	}

	@Test
	public void postProcessAfterInitializationWithRegularBean() {

		GemFireMockObjectsBeanPostProcessor beanPostProcessor = spy(new GemFireMockObjectsBeanPostProcessor());

		Object bean = new Object();

		assertThat(beanPostProcessor.postProcessAfterInitialization(bean, "regularBean"))
			.isEqualTo(bean);

		verify(beanPostProcessor, times(1))
			.postProcessAfterInitialization(eq(bean), eq("regularBean"));
		verifyNoMoreInteractions(beanPostProcessor);
	}
}
