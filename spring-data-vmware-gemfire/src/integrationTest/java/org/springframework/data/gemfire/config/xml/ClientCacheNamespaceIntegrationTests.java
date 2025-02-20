/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.Properties;

import org.apache.geode.pdx.PdxSerializer;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.config.xml.ClientCacheParser
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
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

		Assertions.assertThat(clientCacheFactoryBean.getCacheXml().toString()).contains("empty-client-cache.xml");
		Assertions.assertThat(clientCacheFactoryBean.getProperties()).isEqualTo(gemfireProperties);
		Assertions.assertThat(clientCacheFactoryBean.getCopyOnRead()).isTrue();
		Assertions.assertThat(clientCacheFactoryBean.getCriticalHeapPercentage()).isEqualTo(0.85f);
		Assertions.assertThat(clientCacheFactoryBean.getDurableClientId()).isEqualTo("TestDurableClientId");
		Assertions.assertThat(clientCacheFactoryBean.getDurableClientTimeout()).isEqualTo(600);
		Assertions.assertThat(clientCacheFactoryBean.getEvictionHeapPercentage()).isEqualTo(0.65f);
		Assertions.assertThat(clientCacheFactoryBean.isKeepAlive()).isTrue();
		Assertions.assertThat(clientCacheFactoryBean.getPdxIgnoreUnreadFields()).isTrue();
		Assertions.assertThat(clientCacheFactoryBean.getPdxPersistent()).isFalse();
		Assertions.assertThat(clientCacheFactoryBean.getPdxReadSerialized()).isTrue();
		Assertions.assertThat(clientCacheFactoryBean.getPdxSerializer()).isEqualTo(reflectionBaseAutoSerializer);
		Assertions.assertThat(TestUtils.<String>readField("poolName", clientCacheFactoryBean)).isEqualTo("serverPool");
		Assertions.assertThat(clientCacheFactoryBean.getReadyForEvents()).isFalse();
	}

	@Test
	public void namedClientCacheWithNoPropertiesAndNoCacheXml() {

		Assertions.assertThat(applicationContext.containsBean("client-cache-with-name")).isTrue();

		ClientCacheFactoryBean clientCacheFactoryBean =
			applicationContext.getBean("&client-cache-with-name", ClientCacheFactoryBean.class);

		Assertions.assertThat(clientCacheFactoryBean.getCacheXml()).isNull();
		Assertions.assertThat(clientCacheFactoryBean.getProperties()).isNullOrEmpty();
	}

	@Test
	public void clientCacheWithXmlNoProperties() {

		Assertions.assertThat(applicationContext.containsBean("client-cache-with-xml")).isTrue();

		ClientCacheFactoryBean clientCacheFactoryBean =
			applicationContext.getBean("&client-cache-with-xml", ClientCacheFactoryBean.class);

		Resource cacheXmlResource = clientCacheFactoryBean.getCacheXml();

		Assertions.assertThat(cacheXmlResource.getFilename()).isEqualTo("gemfire-client-cache.xml");

		Assertions.assertThat(clientCacheFactoryBean.getProperties()).isNullOrEmpty();
	}
}
