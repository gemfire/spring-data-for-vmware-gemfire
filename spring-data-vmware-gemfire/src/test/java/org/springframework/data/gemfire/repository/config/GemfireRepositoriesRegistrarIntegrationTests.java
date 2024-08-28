/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.model.User;
import example.app.repo.UserRepository;

/**
 * Integration Tests for {@link GemfireRepositoriesRegistrar}
 *
 * This Integration Tests class tests and assert annotation-based Apache Geode Repository configuration.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see Test
 * @see GemfireRepositoriesRegistrar
 * @see IntegrationTestsSupport
 * @see EnableGemFireMockObjects
 * @see ContextConfiguration
 * @see SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class GemfireRepositoriesRegistrarIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private UserRepository repository;

	@Test
	public void registersAndBootstrapsGemfireRepositoriesCorrectly() {
		assertThat(this.repository).isNotNull();
	}

	@ClientCacheApplication(name = "GemfireRepositoriesRegistrarIntegrationTests")
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions(basePackageClasses = User.class)
	@EnableGemfireRepositories(basePackageClasses = UserRepository.class)
	static class TestGeodeConfiguration { }

}
