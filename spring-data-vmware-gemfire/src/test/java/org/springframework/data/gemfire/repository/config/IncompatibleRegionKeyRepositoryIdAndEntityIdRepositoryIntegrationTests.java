/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Supplier;

import org.junit.Test;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.repository.sample.Animal;
import org.springframework.data.gemfire.repository.sample.Plant;
import org.springframework.data.gemfire.repository.sample.PlantRepository;
import org.springframework.data.gemfire.repository.sample.RabbitRepository;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.Repository;

/**
 * Integration Tests testing and asserting the compatibility of the {@link Region}
 * {@link RegionAttributes#getKeyConstraint() key type}, {@link Repository} {@link Class ID type}
 * and {@link PersistentEntity entity} {@link Class ID type}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @see org.springframework.data.repository.Repository
 * @see <a href="https://github.com/spring-projects/spring-data-gemfire/pull/55">PR-55</a>
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class IncompatibleRegionKeyRepositoryIdAndEntityIdRepositoryIntegrationTests
		extends SpringApplicationContextIntegrationTestsSupport {

	@SuppressWarnings("rawtypes")
	private void usingTestConfigurationExpectIllegalArgumentExceptionWithMessage(Class<?> testConfiguration,
			Class<? extends Repository> repositoryType, Supplier<String> exceptionMessage) {

		try {
			newApplicationContext(testConfiguration);
			assertThat(getBean(repositoryType)).isNotNull();
		}
		catch (BeanCreationException expected) {

			assertThat(expected).hasCauseInstanceOf(IllegalArgumentException.class);
			assertThat(expected.getCause()).hasMessage(exceptionMessage.get());

			throw (IllegalArgumentException) expected.getCause();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void withRegionUsingStringKeyAndRepositoryUsingLongId() {

		usingTestConfigurationExpectIllegalArgumentExceptionWithMessage(
			TestIncompatibleRegionKeyRepositoryIdTypeConfiguration.class,
			RabbitRepository.class,
			() -> String.format("Region [/Rabbits] requires keys of type [%1$s], but Repository [%2$s] declared an id of type [%3$s]",
				String.class.getName(), RabbitRepository.class.getName(), Long.class.getName()));
	}

	@Test(expected = IllegalArgumentException.class)
	public void withRepositoryUsingStringIdAndEntityUsingLongId() {

		usingTestConfigurationExpectIllegalArgumentExceptionWithMessage(
			TestIncompatibleRepositoryIdEntityIdTypeConfiguration.class,
			PlantRepository.class,
			() -> String.format("Repository [%1$s] declared an id of type [%2$s], but entity [%3$s] has an id of type [%4$s]",
				PlantRepository.class.getName(), String.class.getName(), Plant.class.getName(), Long.class.getName()));
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableGemfireRepositories(basePackageClasses = RabbitRepository.class,
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = RabbitRepository.class)
	)
	static class TestIncompatibleRegionKeyRepositoryIdTypeConfiguration {

		@Bean("Rabbits")
		public ClientRegionFactoryBean<String, Animal> clientRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<String, Animal> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(gemfireCache);
			clientRegion.setKeyConstraint(String.class);
			clientRegion.setShortcut(ClientRegionShortcut.LOCAL);
			clientRegion.setValueConstraint(Animal.class);

			return clientRegion;
		}
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableGemfireRepositories(basePackageClasses = PlantRepository.class,
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = PlantRepository.class)
	)
	static class TestIncompatibleRepositoryIdEntityIdTypeConfiguration {

		@Bean("Plants")
		public ClientRegionFactoryBean<Object, Object> clientRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<Object, Object> clientRegion = new ClientRegionFactoryBean<>();

			clientRegion.setCache(gemfireCache);
			clientRegion.setShortcut(ClientRegionShortcut.LOCAL);

			return clientRegion;
		}
	}
}
