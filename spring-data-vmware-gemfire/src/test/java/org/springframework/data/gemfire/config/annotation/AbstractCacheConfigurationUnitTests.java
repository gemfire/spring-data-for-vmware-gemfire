/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.geode.cache.TransactionListener;
import org.apache.geode.cache.TransactionWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;

/**
 * Unit Tests for {@link AbstractCacheConfiguration}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.junit.runner.RunWith
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.mockito.Spy
 * @see org.mockito.junit.MockitoJUnitRunner
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.AbstractCacheConfiguration
 * @since 1.9.0
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractCacheConfigurationUnitTests {

	@Spy
	private AbstractCacheConfiguration cacheConfiguration;

	@Test
	public void gemfirePropertiesContainsEssentialProperties() {

		this.cacheConfiguration.setName("TestName");
		this.cacheConfiguration.setLogLevel("DEBUG");
		this.cacheConfiguration.setLocators("skullbox[11235]");

		Properties gemfireProperties = this.cacheConfiguration.gemfireProperties();

		assertThat(gemfireProperties).isNotNull();
		assertThat(gemfireProperties).hasSize(3);
		assertThat(gemfireProperties.getProperty("name")).isEqualTo("TestName");
		assertThat(gemfireProperties.getProperty("log-level")).isEqualTo("DEBUG");
		assertThat(gemfireProperties.getProperty("locators")).isEqualTo("skullbox[11235]");
	}

	@Test
	public void cacheFactoryBeanConfigurationIsCorrect() {

		BeanFactory mockBeanFactory = mock(BeanFactory.class);

		ClientCacheFactoryBean cacheFactoryBean = mock(ClientCacheFactoryBean.class);

		ClassLoader testBeanClassLoader = Thread.currentThread().getContextClassLoader();

		List<ClientCacheFactoryBean.JndiDataSource> jndiDataSources = Collections.emptyList();
		List<TransactionListener> transactionListeners = Collections.singletonList(mock(TransactionListener.class));

		Properties gemfireProperties = new Properties();

		Resource mockResource = mock(Resource.class);

		TransactionWriter mockTransactionWriter = mock(TransactionWriter.class);

		doReturn(gemfireProperties).when(this.cacheConfiguration).gemfireProperties();

		this.cacheConfiguration.setBeanClassLoader(testBeanClassLoader);
		this.cacheConfiguration.setBeanFactory(mockBeanFactory);
		this.cacheConfiguration.setCacheXml(mockResource);
		this.cacheConfiguration.setClose(false);
		this.cacheConfiguration.setCopyOnRead(true);
		this.cacheConfiguration.setCriticalHeapPercentage(0.90f);
		this.cacheConfiguration.setEvictionHeapPercentage(0.75f);
		this.cacheConfiguration.setJndiDataSources(jndiDataSources);
		this.cacheConfiguration.setTransactionListeners(transactionListeners);
		this.cacheConfiguration.setTransactionWriter(mockTransactionWriter);
		this.cacheConfiguration.setUseBeanFactoryLocator(true);

		assertThat(this.cacheConfiguration.configureCacheFactoryBean(cacheFactoryBean)).isEqualTo(cacheFactoryBean);

		verify(cacheFactoryBean, times(1)).setBeanClassLoader(eq(testBeanClassLoader));
		verify(cacheFactoryBean, times(1)).setBeanFactory(eq(mockBeanFactory));
		verify(cacheFactoryBean, times(1)).setClose(eq(false));
		verify(cacheFactoryBean, times(1)).setCopyOnRead(eq(true));
		verify(cacheFactoryBean, times(1)).setCriticalHeapPercentage(eq(0.90f));
		verify(cacheFactoryBean, times(1)).setEvictionHeapPercentage(eq(0.75f));
		verify(cacheFactoryBean, times(1)).setJndiDataSources(eq(jndiDataSources));
		verify(cacheFactoryBean, times(1)).setTransactionListeners(eq(transactionListeners));
		verify(cacheFactoryBean, times(1)).setTransactionWriter(eq(mockTransactionWriter));
		verify(cacheFactoryBean, times(1)).setUseBeanFactoryLocator(eq(true));
	}
}
