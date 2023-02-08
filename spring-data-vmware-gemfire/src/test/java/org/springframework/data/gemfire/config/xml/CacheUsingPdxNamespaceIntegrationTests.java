/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.xml;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.pdx.PdxSerializer;

import org.springframework.data.gemfire.CacheFactoryBean;
import org.springframework.data.gemfire.config.support.PdxDiskStoreAwareBeanFactoryPostProcessor;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing SDG XML namespace configuration metadata when PDX is configured in Apache Geode.
 *
 * @author John Blum
 * @see Test
 * @see PdxSerializer
 * @see CacheFactoryBean
 * @see PdxDiskStoreAwareBeanFactoryPostProcessor
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 * @since 1.3.3
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class CacheUsingPdxNamespaceIntegrationTests extends IntegrationTestsSupport {

	@Test
	public void testApplicationContextHasPdxDiskStoreAwareBeanFactoryPostProcessor() {

		PdxDiskStoreAwareBeanFactoryPostProcessor postProcessor =
			requireApplicationContext().getBean(PdxDiskStoreAwareBeanFactoryPostProcessor.class);

		// NOTE the postProcessor reference will not be null as the ApplicationContext.getBean(:Class) method (getting
		// a bean by Class type) will throw a NoSuchBeanDefinitionException if no bean of type
		// PdxDiskStoreAwareBeanFactoryPostProcessor could be found, or throw a NoUniqueBeanDefinitionException if
		// our PdxDiskStoreAwareBeanFactoryPostProcessor bean is not unique!
		assertThat(postProcessor).isNotNull();
		assertThat(postProcessor.getPdxDiskStoreName()).isEqualTo("pdxStore");
	}

	@Test
	public void testCachePdxConfiguration() {

		CacheFactoryBean cacheFactoryBean =
			requireApplicationContext().getBean("&gemfireCache", CacheFactoryBean.class);

		assertThat(cacheFactoryBean).isNotNull();
		assertThat(cacheFactoryBean.getPdxDiskStoreName()).isEqualTo("pdxStore");
		assertThat(Boolean.TRUE.equals(cacheFactoryBean.getPdxPersistent())).isTrue();
		assertThat(Boolean.TRUE.equals(cacheFactoryBean.getPdxReadSerialized())).isTrue();

		PdxSerializer autoSerializer =
			requireApplicationContext().getBean("autoSerializer", PdxSerializer.class);

		assertThat(autoSerializer).isNotNull();
		assertThat(cacheFactoryBean.getPdxSerializer()).isSameAs(autoSerializer);
	}

}
