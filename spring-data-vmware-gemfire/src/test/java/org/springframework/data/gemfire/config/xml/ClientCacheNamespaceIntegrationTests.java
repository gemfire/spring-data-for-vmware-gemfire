/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.pdx.PdxSerializer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link ClientCacheParser}.
 *
 * @author John Blum
 * @see Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see ClientCacheFactoryBean
 * @see ClientCacheParser
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.6.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class ClientCacheNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	@Qualifier("&client-cache-with-no-name")
	private ClientCacheFactoryBean clientCacheFactoryBean;

	@Autowired
	private PdxSerializer reflectionBaseAutoSerializer;

	@Autowired
	private Properties gemfireProperties;

	@Test
	public void clientCacheFactoryBeanConfiguration() throws Exception {

		assertThat(clientCacheFactoryBean.getCacheXml().toString()).contains("empty-client-cache.xml");
		assertThat(clientCacheFactoryBean.getProperties()).isEqualTo(gemfireProperties);
		assertThat(clientCacheFactoryBean.getCopyOnRead()).isTrue();
		assertThat(clientCacheFactoryBean.getCriticalHeapPercentage()).isEqualTo(0.85f);
		assertThat(clientCacheFactoryBean.getDurableClientId()).isEqualTo("TestDurableClientId");
		assertThat(clientCacheFactoryBean.getDurableClientTimeout()).isEqualTo(600);
		assertThat(clientCacheFactoryBean.getEvictionHeapPercentage()).isEqualTo(0.65f);
		assertThat(clientCacheFactoryBean.isKeepAlive()).isTrue();
		assertThat(clientCacheFactoryBean.getPdxIgnoreUnreadFields()).isTrue();
		assertThat(clientCacheFactoryBean.getPdxPersistent()).isFalse();
		assertThat(clientCacheFactoryBean.getPdxReadSerialized()).isTrue();
		assertThat(clientCacheFactoryBean.getPdxSerializer()).isEqualTo(reflectionBaseAutoSerializer);
		assertThat(TestUtils.<String>readField("poolName", clientCacheFactoryBean)).isEqualTo("serverPool");
		assertThat(clientCacheFactoryBean.getReadyForEvents()).isFalse();
	}

	@Test
	public void namedClientCacheWithNoPropertiesAndNoCacheXml() {

		assertThat(applicationContext.containsBean("client-cache-with-name")).isTrue();

		ClientCacheFactoryBean clientCacheFactoryBean =
			applicationContext.getBean("&client-cache-with-name", ClientCacheFactoryBean.class);

		assertThat(clientCacheFactoryBean.getCacheXml()).isNull();
		assertThat(clientCacheFactoryBean.getProperties()).isNull();
	}

	@Test
	public void clientCacheWithXmlNoProperties() {

		assertThat(applicationContext.containsBean("client-cache-with-xml")).isTrue();

		ClientCacheFactoryBean clientCacheFactoryBean =
			applicationContext.getBean("&client-cache-with-xml", ClientCacheFactoryBean.class);

		Resource cacheXmlResource = clientCacheFactoryBean.getCacheXml();

		assertThat(cacheXmlResource.getFilename()).isEqualTo("gemfire-client-cache.xml");

		assertThat(clientCacheFactoryBean.getProperties()).isNull();
	}
}
