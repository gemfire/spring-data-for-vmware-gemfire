/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import java.util.Properties;

import org.apache.geode.cache.client.ClientCache;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.support.GemfireBeanFactoryLocator;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link ClientCacheFactoryBean}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.client.ClientCacheFactoryBean
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
public class CacheNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@SuppressWarnings("unused")
	private ApplicationContext applicationContext;

	@Before
	public void setup() {
		Assertions.assertThat(this.applicationContext.getBean("gemfireCache"))
			.isNotEqualTo(this.applicationContext.getBean("cache-with-name"));
	}

	@Test
	public void noNamedCacheConfigurationIsCorrect() {

		Assertions.assertThat(applicationContext.containsBean("gemfireCache")).isTrue();
		Assertions.assertThat(applicationContext.containsBean("gemfire-cache")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean = applicationContext.getBean("&gemfireCache", ClientCacheFactoryBean.class);

		Assertions.assertThat(cacheFactoryBean.getBeanName()).isEqualTo("gemfireCache");
		Assertions.assertThat(cacheFactoryBean.getCacheXml()).isNull();

		ClientCache gemfireCache = applicationContext.getBean("gemfireCache", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem().getProperties()).isNotNull();
	}

	@Test
	public void namedCacheConfigurationIsCorrect() {

		Assertions.assertThat(applicationContext.containsBean("cache-with-name")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&cache-with-name", ClientCacheFactoryBean.class);

		Assertions.assertThat(cacheFactoryBean.getBeanName()).isEqualTo("cache-with-name");
		Assertions.assertThat(cacheFactoryBean.getCacheXml()).isNull();

		ClientCache gemfireCache = applicationContext.getBean("cache-with-name", ClientCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties distributedSystemProperties = gemfireCache.getDistributedSystem().getProperties();

		Assertions.assertThat(distributedSystemProperties).isNotNull();
		Assertions.assertThat(Boolean.parseBoolean(distributedSystemProperties.getProperty("use-cluster-configuration"))).isFalse();
	}

	@Test(expected = IllegalStateException.class)
	public void cacheWithNoBeanFactoryLocatorIsCorrect() {

		Assertions.assertThat(applicationContext.containsBean("cache-with-no-bean-factory-locator")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&cache-with-no-bean-factory-locator", ClientCacheFactoryBean.class);

		Assertions.assertThat(cacheFactoryBean.getBeanFactoryLocator()).isNull();

		GemfireBeanFactoryLocator.newBeanFactoryLocator().useBeanFactory("cache-with-no-bean-factory-locator");
	}

	@Test
	public void cacheWithXmlAndPropertiesConfigurationIsCorrect() throws Exception {

		Assertions.assertThat(applicationContext.containsBean("cache-with-xml-and-props")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&cache-with-xml-and-props", ClientCacheFactoryBean.class);

		Resource cacheXmlResource = cacheFactoryBean.getCacheXml();

		Assertions.assertThat(cacheXmlResource.getFilename()).isEqualTo("gemfire-cache.xml");
		Assertions.assertThat(applicationContext.containsBean("gemfireProperties")).isTrue();
		Assertions.assertThat(TestUtils.<Properties>readField("properties", cacheFactoryBean))
			.isEqualTo(applicationContext.getBean("gemfireProperties"));
		Assertions.assertThat(TestUtils.<Boolean>readField("pdxReadSerialized", cacheFactoryBean)).isEqualTo(Boolean.TRUE);
		Assertions.assertThat(TestUtils.<Boolean>readField("pdxIgnoreUnreadFields", cacheFactoryBean)).isEqualTo(Boolean.FALSE);
		Assertions.assertThat(TestUtils.<Boolean>readField("pdxPersistent", cacheFactoryBean)).isEqualTo(Boolean.TRUE);
	}

	@Test
	public void heapTunedCacheIsCorrect() {

		Assertions.assertThat(applicationContext.containsBean("heap-tuned-cache")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&heap-tuned-cache", ClientCacheFactoryBean.class);

		Float criticalHeapPercentage = cacheFactoryBean.getCriticalHeapPercentage();
		Float evictionHeapPercentage = cacheFactoryBean.getEvictionHeapPercentage();

		Assertions.assertThat(criticalHeapPercentage).isCloseTo(70.0f, Offset.offset(0.0001f));
		Assertions.assertThat(evictionHeapPercentage).isCloseTo(60.0f, Offset.offset(0.0001f));
	}
}
