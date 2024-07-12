/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.pdx.PdxSerializer;
import org.junit.After;
import org.junit.Test;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.DiskStoreFactoryBean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.mapping.MappingPdxSerializer;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;

/**
 * Integration Tests for {@link EnablePdx} and {@link PdxConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.pdx.PdxSerializer
 * @see org.springframework.data.gemfire.config.annotation.EnablePdx
 * @see org.springframework.data.gemfire.config.annotation.PdxConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 1.9.0
 */
public class EnablePdxConfigurationIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	@Test
	public void regionBeanDefinitionDependsOnPdxDiskStoreBean() {

		newApplicationContext(TestEnablePdxWithDiskStoreConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("MockPdxSerializer")).isTrue();
		assertThat(containsBean("TestDiskStore")).isTrue();
		assertThat(containsBean("TestRegion")).isTrue();

		ClientCacheFactoryBean gemfireCache = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getPdxSerializer()).isEqualTo(getBean("MockPdxSerializer", PdxSerializer.class));

		BeanDefinition testDiskStoreBeanDefinition =
			requireApplicationContext().getBeanFactory().getBeanDefinition("TestDiskStore");

		assertThat(testDiskStoreBeanDefinition).isNotNull();
		assertThat(testDiskStoreBeanDefinition.getDependsOn()).isNullOrEmpty();

		BeanDefinition testRegionBeanDefinition =
			requireApplicationContext().getBeanFactory().getBeanDefinition("TestRegion");

		assertThat(testRegionBeanDefinition).isNotNull();
		assertThat(testRegionBeanDefinition.getDependsOn()).containsExactly("TestDiskStore");
	}

	@Test
	public void regionBeanDefinitionHasNoDependencies() {

		newApplicationContext(TestEnablePdxConfigurationWithNoDiskStoreConfiguration.class);

		assertThat(containsBean("gemfireCache")).isTrue();
		assertThat(containsBean("TestDiskStore")).isTrue();
		assertThat(containsBean("TestRegion")).isTrue();

		ClientCacheFactoryBean gemfireCache = getBean("&gemfireCache", ClientCacheFactoryBean.class);

		assertThat(gemfireCache).isNotNull();
		assertThat(gemfireCache.getPdxSerializer()).isInstanceOf(MappingPdxSerializer.class);

		BeanDefinition testDiskStoreBeanDefinition =
			requireApplicationContext().getBeanFactory().getBeanDefinition("TestDiskStore");

		assertThat(testDiskStoreBeanDefinition).isNotNull();
		assertThat(testDiskStoreBeanDefinition.getDependsOn()).isNullOrEmpty();

		BeanDefinition testRegionBeanDefinition =
			requireApplicationContext().getBeanFactory().getBeanDefinition("TestRegion");

		assertThat(testRegionBeanDefinition).isNotNull();
		assertThat(testRegionBeanDefinition.getDependsOn()).isNullOrEmpty();

	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnablePdx(diskStoreName = "TestDiskStore", serializerBeanName = "MockPdxSerializer")
	@SuppressWarnings("unused")
	static class TestEnablePdxWithDiskStoreConfiguration {

		@Bean("TestDiskStore")
		DiskStoreFactoryBean testPdxDiskStore(ClientCache gemfireCache) {

			DiskStoreFactoryBean testDiskStore = new DiskStoreFactoryBean();

			testDiskStore.setCache(gemfireCache);

			return testDiskStore;
		}

		@Bean("TestRegion")
		ClientRegionFactoryBean<Object, Object> testRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> testRegion = new ClientRegionFactoryBean<>();

			testRegion.setCache(gemfireCache);
			testRegion.setClose(false);
			testRegion.setPersistent(false);

			return testRegion;
		}

		@Bean("MockPdxSerializer")
		PdxSerializer mockPdxSerializer() {
			return mock(PdxSerializer.class);
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnablePdx
	@SuppressWarnings("unused")
	static class TestEnablePdxConfigurationWithNoDiskStoreConfiguration {

		@Bean("TestDiskStore")
		DiskStoreFactoryBean testPdxDiskStore(ClientCache gemfireCache) {

			DiskStoreFactoryBean testDiskStore = new DiskStoreFactoryBean();

			testDiskStore.setCache(gemfireCache);

			return testDiskStore;
		}

		@Bean("TestRegion")
		public ClientRegionFactoryBean<Object, Object> testRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> testRegion = new ClientRegionFactoryBean<>();

			testRegion.setCache(gemfireCache);
			testRegion.setClose(false);
			testRegion.setShortcut(ClientRegionShortcut.LOCAL);

			return testRegion;
		}
	}
}
