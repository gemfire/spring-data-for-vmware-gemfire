/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.cdi;

import static org.assertj.core.api.Assertions.assertThat;

import javax.enterprise.inject.se.SeContainer;
import javax.enterprise.inject.se.SeContainerInitializer;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.apache.geode.cache.CacheClosedException;
import org.apache.geode.cache.CacheFactory;

import org.springframework.data.gemfire.repository.sample.Person;

/**
 * Integration Tests for CDI.
 *
 * @author John Blum
 * @author Mark Paluch
 * @see Test
 * @see GemfireRepositoryBean
 * @see GemfireRepositoryExtension
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

		assertThat(actual.getId()).isEqualTo(expected.getId());
		assertThat(actual.getFirstname()).isEqualTo(expected.getFirstname());
		assertThat(actual.getLastname()).isEqualTo(expected.getLastname());
	}

	@Test // DATAGEODE-42
	public void bootstrapsRepositoryCorrectly() {

		RepositoryClient repositoryClient = container.select(RepositoryClient.class).get();

		assertThat(repositoryClient.getPersonRepository()).isNotNull();

		Person expectedJonDoe = repositoryClient.newPerson("Jon", "Doe");

		assertThat(expectedJonDoe).isNotNull();
		assertThat(expectedJonDoe.getId()).isGreaterThan(0L);
		assertThat(expectedJonDoe.getName()).isEqualTo("Jon Doe");

		Person savedJonDoe = repositoryClient.save(expectedJonDoe);

		assertIsExpectedPerson(savedJonDoe, expectedJonDoe);

		Person foundJonDoe = repositoryClient.find(expectedJonDoe.getId());

		assertIsExpectedPerson(foundJonDoe, expectedJonDoe);

		assertThat(repositoryClient.delete(foundJonDoe)).isTrue();
		assertThat(repositoryClient.find(foundJonDoe.getId())).isNull();
	}

	@Test // DATAGEODE-42
	public void returnOneFromCustomImplementation() {

		RepositoryClient repositoryClient = container.select(RepositoryClient.class).get();

		assertThat(repositoryClient.getPersonRepository().returnOne()).isEqualTo(1);
	}
}
