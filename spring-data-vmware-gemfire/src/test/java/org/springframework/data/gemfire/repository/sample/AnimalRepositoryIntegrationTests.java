/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the functionality behind PR #55 involving persisting application domain object/entities
 * to multiple Regions in an Apache Geode cache.
 *
 * @author Stuart Williams
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @link https://github.com/spring-projects/spring-data-gemfire/pull/55
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class AnimalRepositoryIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private CatRepository catRepo;

	@Autowired
	private DogRepository dogRepo;

	protected static Animal newAnimal(long id, String name) {

		Animal animal = new Animal();

		animal.setId(id);
		animal.setName(name);

		return animal;
	}

	@Test
	public void entityStoredInMultipleRegionsIsSuccessful() {

		Animal felix = newAnimal(1, "Felix");
		Animal leo = newAnimal(2, "Leo");
		Animal cerberus = newAnimal(3, "Cerberus");
		Animal fido = newAnimal(1, "Fido");

		assertThat(catRepo.save(felix)).isNotNull();
		assertThat(catRepo.save(leo)).isNotNull();
		assertThat(catRepo.save(cerberus)).isNotNull();
		assertThat(dogRepo.save(fido)).isNotNull();
		assertThat(dogRepo.save(cerberus)).isNotNull();
		assertThat(catRepo.count()).isEqualTo(3L);
		assertThat(dogRepo.count()).isEqualTo(2L);

		Optional<Animal> foundFelix = catRepo.findById(1L);

		assertThat(foundFelix.isPresent()).isTrue();
		assertThat(foundFelix.get()).isEqualTo(felix);

		Animal foundLeo = catRepo.findBy("Leo");

		assertThat(foundLeo).isEqualTo(leo);

		Animal foundCerberusTheCat = catRepo.findByName("Cerberus");

		assertThat(foundCerberusTheCat).isEqualTo(cerberus);
		assertThat(catRepo.findBy("Cerberus")).isEqualTo(foundCerberusTheCat);
		assertThat(catRepo.findById(3L).orElse(null)).isEqualTo(foundCerberusTheCat);

		Animal foundFido = dogRepo.findBy("Fido");

		assertThat(foundFido).isEqualTo(fido);

		Animal foundCerberusTheDog = dogRepo.findByName("Cerberus");

		assertThat(foundCerberusTheDog).isEqualTo(cerberus);
		assertThat(dogRepo.findBy("Cerberus")).isEqualTo(foundCerberusTheDog);
		assertThat(dogRepo.findById(3L).orElse(null)).isEqualTo(foundCerberusTheDog);
	}
}
