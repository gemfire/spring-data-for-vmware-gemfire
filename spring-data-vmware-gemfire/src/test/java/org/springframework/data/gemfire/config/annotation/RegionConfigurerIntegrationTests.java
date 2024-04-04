/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import org.apache.geode.cache.client.ClientCache;
import org.junit.After;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.test.entities.NonEntity;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.util.ReflectionUtils;

/**
 * Integration Tests for {@link RegionConfigurer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.client.ClientRegionFactoryBean
 * @see org.springframework.data.gemfire.config.annotation.RegionConfigurer
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.1.0
 */
public class RegionConfigurerIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	@After
	public void cleanupAfterTests() {
		destroyAllGemFireMockObjects();
	}

	@SuppressWarnings("unchecked")
	private Iterable<String> resolveBeanNames(Object target) {

		return Optional.ofNullable(target)
			.map(Object::getClass)
			.map(type -> ReflectionUtils.findField(type, "beanNames"))
			.map(beanNamesField -> {
				ReflectionUtils.makeAccessible(beanNamesField);
				return beanNamesField;
			})
			.map(beanNamesField -> (Set<String>) ReflectionUtils.getField(beanNamesField, target))
			.orElseGet(Collections::emptySet);
	}

	private void assertRegionConfigurerInvocations(Iterable<String> actualRegionBeanNames,
			String... expectedRegionBeanNames) {

		assertThat(actualRegionBeanNames).isNotNull();
		assertThat(actualRegionBeanNames).hasSize(expectedRegionBeanNames.length);
		assertThat(actualRegionBeanNames).contains(expectedRegionBeanNames);
	}

	@Test
	public void clientRegionConfigurersCalledSuccessfully() {

		newApplicationContext(ClientTestConfiguration.class);

		assertThat(containsBean("Test")).isTrue();
		assertThat(containsBean("Sessions")).isTrue();
		assertThat(containsBean("GenericRegionEntity")).isTrue();
		assertThat(containsBean("testRegionConfigurerOne")).isTrue();
		assertThat(containsBean("testRegionConfigurerTwo")).isTrue();
		assertThat(containsBean("testRegionConfigurerThree")).isTrue();

		assertRegionConfigurerInvocations(getBean("testRegionConfigurerOne", TestRegionConfigurer.class),
			"GenericRegionEntity", "Sessions");

		assertRegionConfigurerInvocations(getBean("testRegionConfigurerTwo", TestRegionConfigurer.class),
			"GenericRegionEntity", "Sessions");

		assertRegionConfigurerInvocations(resolveBeanNames(getBean("testRegionConfigurerThree",
				RegionConfigurer.class)), "GenericRegionEntity", "Sessions");
	}

	@SuppressWarnings("unused")
	static class AbstractTestConfiguration {

		@Bean
		TestRegionConfigurer testRegionConfigurerOne() {
			return new TestRegionConfigurer();
		}

		@Bean
		TestRegionConfigurer testRegionConfigurerTwo() {
			return new TestRegionConfigurer();
		}

		@Bean
		RegionConfigurer testRegionConfigurerThree() {

			return new RegionConfigurer() {

				private final Set<String> beanNames = new HashSet<>();

				@Override
				public void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) {
					this.beanNames.add(beanName);
				}
			};
		}

		@Bean
		String nonRelevantBean() {
			return "test";
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = NonEntity.class)
	@SuppressWarnings("unused")
	static class ClientTestConfiguration extends AbstractTestConfiguration {

		@Bean(name = "Test")
		ClientRegionFactoryBean<Object, Object> testRegion(ClientCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> testRegionFactory = new ClientRegionFactoryBean<>();

			testRegionFactory.setCache(gemfireCache);

			return testRegionFactory;
		}
	}

	private static class TestRegionConfigurer implements Iterable<String>, RegionConfigurer {

		private final Set<String> beanNames = new HashSet<>();

		@Override
		public void configure(String beanName, ClientRegionFactoryBean<?, ?> bean) {
			this.beanNames.add(beanName);
		}

		@Override
		public Iterator<String> iterator() {
			return Collections.unmodifiableSet(this.beanNames).iterator();
		}
	}
}
