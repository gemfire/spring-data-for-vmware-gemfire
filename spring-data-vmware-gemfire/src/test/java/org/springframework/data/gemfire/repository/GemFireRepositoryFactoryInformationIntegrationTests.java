/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.repository.support.GemfireRepositoryFactoryBean;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.support.RepositoryFactoryInformation;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.model.User;
import example.app.repo.UserRepository;

/**
 * Integration Tests testing and asserting that Apache Geode-based {@link Repository} factories,
 * implementing the {@link RepositoryFactoryInformation} interface, can in fact be looked up
 * in the Spring {@link ApplicationContext}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see ApplicationContext
 * @see ClientCacheApplication
 * @see EnableGemfireRepositories
 * @see GemfireRepositoryFactoryBean
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see Repository
 * @see RepositoryFactoryInformation
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.9.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class GemFireRepositoryFactoryInformationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	public void applicationContextContainsUserRepositoryBean() {
		assertThat(this.applicationContext.getBeanNamesForType(UserRepository.class)).contains("userRepository");
	}

	@Test
	@SuppressWarnings("rawtypes")
	public void canGetGemfireRepositoryBeansByRepositoryFactoryInformationType() {

		Map<String, RepositoryFactoryInformation> repositoryFactories =
			this.applicationContext.getBeansOfType(RepositoryFactoryInformation.class);

		assertThat(repositoryFactories).isNotNull();
		assertThat(repositoryFactories).isNotEmpty();
		assertThat(repositoryFactories).containsKeys("&userRepository");
		assertThat(repositoryFactories.get("&userRepository")).isInstanceOf(GemfireRepositoryFactoryBean.class);
	}

	@ClientCacheApplication(name = "GemFireRepositoryFactoryInformationIntegrationTests")
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = User.class)
	@EnableGemfireRepositories(basePackageClasses = UserRepository.class)
	static class TestGeodeConfiguration { }

}
