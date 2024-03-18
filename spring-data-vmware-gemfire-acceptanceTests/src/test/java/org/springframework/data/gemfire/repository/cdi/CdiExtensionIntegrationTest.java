/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.cdi;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.data.gemfire.repository.sample.Person;

/**
 * Integration Tests for CDI.
 *
 * @author John Blum
 * @author Mark Paluch
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.repository.cdi.GemfireRepositoryBean
 * @see org.springframework.data.gemfire.repository.cdi.GemfireRepositoryExtension
 * @since 1.8.0
 */
public class CdiExtensionIntegrationTest {

	static SeContainer container;

	@BeforeClass
	public static void setUp() {

		container = SeContainerInitializer.newInstance() //
				.disableDiscovery() //
				.addPackages(RepositoryClient.class) //
				.initialize();
	}

	@AfterClass
	public static void tearDown() {
		container.close();
		closeGemfireCache();
	}

	private static void closeGemfireCache() {
		try {
			CacheFactory.getAnyInstance().close();
		}
		catch (CacheClosedException ignore) {}
	}

	protected void assertIsExpectedPerson(Person actual, Person expected) {

		Assertions.assertThat(actual.getId()).isEqualTo(expected.getId());
		Assertions.assertThat(actual.getFirstname()).isEqualTo(expected.getFirstname());
		Assertions.assertThat(actual.getLastname()).isEqualTo(expected.getLastname());
	}

	@Test // DATAGEODE-42
	public void bootstrapsRepositoryCorrectly() {

		RepositoryClient repositoryClient = container.select(RepositoryClient.class).get();

		Assertions.assertThat(repositoryClient.getPersonRepository()).isNotNull();

		Person expectedJonDoe = repositoryClient.newPerson("Jon", "Doe");

		Assertions.assertThat(expectedJonDoe).isNotNull();
		Assertions.assertThat(expectedJonDoe.getId()).isGreaterThan(0L);
		Assertions.assertThat(expectedJonDoe.getName()).isEqualTo("Jon Doe");

		Person savedJonDoe = repositoryClient.save(expectedJonDoe);

		assertIsExpectedPerson(savedJonDoe, expectedJonDoe);

		Person foundJonDoe = repositoryClient.find(expectedJonDoe.getId());

		assertIsExpectedPerson(foundJonDoe, expectedJonDoe);

		Assertions.assertThat(repositoryClient.delete(foundJonDoe)).isTrue();
		Assertions.assertThat(repositoryClient.find(foundJonDoe.getId())).isNull();
	}

	@Test // DATAGEODE-42
	public void returnOneFromCustomImplementation() {

		RepositoryClient repositoryClient = container.select(RepositoryClient.class).get();

		Assertions.assertThat(repositoryClient.getPersonRepository().returnOne()).isEqualTo(1);
	}
}
