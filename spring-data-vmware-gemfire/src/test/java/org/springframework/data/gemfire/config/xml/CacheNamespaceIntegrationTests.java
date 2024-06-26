/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.springframework.data.gemfire.support.GemfireBeanFactoryLocator.newBeanFactoryLocator;
import java.util.Properties;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.gemfire.TestUtils;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link ClientCacheFactoryBean}.
 *
 * @author Costin Leau
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
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
		assertThat(this.applicationContext.getBean("gemfireCache"))
			.isNotEqualTo(this.applicationContext.getBean("cache-with-name"));
	}

	@Test
	public void noNamedCacheConfigurationIsCorrect() {

		assertThat(applicationContext.containsBean("gemfireCache")).isTrue();
		assertThat(applicationContext.containsBean("gemfire-cache")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean = applicationContext.getBean("&gemfireCache", ClientCacheFactoryBean.class);

		assertThat(cacheFactoryBean.getBeanName()).isEqualTo("gemfireCache");
		assertThat(cacheFactoryBean.getCacheXml()).isNull();

		ClientCache gemfireCache = applicationContext.getBean("gemfireCache", ClientCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();
		assertThat(gemfireCache.getDistributedSystem().getProperties()).isNotNull();
	}

	@Test
	public void namedCacheConfigurationIsCorrect() {

		assertThat(applicationContext.containsBean("cache-with-name")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&cache-with-name", ClientCacheFactoryBean.class);

		assertThat(cacheFactoryBean.getBeanName()).isEqualTo("cache-with-name");
		assertThat(cacheFactoryBean.getCacheXml()).isNull();

		ClientCache gemfireCache = applicationContext.getBean("cache-with-name", ClientCache.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getDistributedSystem()).isNotNull();

		Properties distributedSystemProperties = gemfireCache.getDistributedSystem().getProperties();

		assertThat(distributedSystemProperties).isNotNull();
		assertThat(Boolean.parseBoolean(distributedSystemProperties.getProperty("use-cluster-configuration"))).isFalse();
	}

	@Test(expected = IllegalStateException.class)
	public void cacheWithNoBeanFactoryLocatorIsCorrect() {

		assertThat(applicationContext.containsBean("cache-with-no-bean-factory-locator")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&cache-with-no-bean-factory-locator", ClientCacheFactoryBean.class);

		assertThat(cacheFactoryBean.getBeanFactoryLocator()).isNull();

		newBeanFactoryLocator().useBeanFactory("cache-with-no-bean-factory-locator");
	}

	@Test
	public void cacheWithXmlAndPropertiesConfigurationIsCorrect() throws Exception {

		assertThat(applicationContext.containsBean("cache-with-xml-and-props")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&cache-with-xml-and-props", ClientCacheFactoryBean.class);

		Resource cacheXmlResource = cacheFactoryBean.getCacheXml();

		assertThat(cacheXmlResource.getFilename()).isEqualTo("gemfire-cache.xml");
		assertThat(applicationContext.containsBean("gemfireProperties")).isTrue();
		assertThat(TestUtils.<Properties>readField("properties", cacheFactoryBean))
			.isEqualTo(applicationContext.getBean("gemfireProperties"));
		assertThat(TestUtils.<Boolean>readField("pdxReadSerialized", cacheFactoryBean)).isEqualTo(Boolean.TRUE);
		assertThat(TestUtils.<Boolean>readField("pdxIgnoreUnreadFields", cacheFactoryBean)).isEqualTo(Boolean.FALSE);
		assertThat(TestUtils.<Boolean>readField("pdxPersistent", cacheFactoryBean)).isEqualTo(Boolean.TRUE);
	}

	@Test
	public void heapTunedCacheIsCorrect() {

		assertThat(applicationContext.containsBean("heap-tuned-cache")).isTrue();

		ClientCacheFactoryBean cacheFactoryBean =
			applicationContext.getBean("&heap-tuned-cache", ClientCacheFactoryBean.class);

		Float criticalHeapPercentage = cacheFactoryBean.getCriticalHeapPercentage();
		Float evictionHeapPercentage = cacheFactoryBean.getEvictionHeapPercentage();

		assertThat(criticalHeapPercentage).isCloseTo(70.0f, offset(0.0001f));
		assertThat(evictionHeapPercentage).isCloseTo(60.0f, offset(0.0001f));
	}
}
