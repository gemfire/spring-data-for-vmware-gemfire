/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

import java.util.concurrent.atomic.AtomicLong;

import javax.inject.Inject;

import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.util.Assert;

/**
 * The RepositoryClient class is a user/consumer of the {@link SamplePersonRepository} bean in a CDI context.
 *
 * @author John Blum
 * @see javax.inject.Inject
 * @see org.springframework.data.gemfire.repository.cdi.SamplePersonRepository
 * @since 1.8.0
 */
public class RepositoryClient {

	private static final AtomicLong ID_SEQUENCE = new AtomicLong(0L);

	@Inject
	private SamplePersonRepository personRepository;

	protected SamplePersonRepository getPersonRepository() {
		Assert.state(personRepository != null, "PersonRepository was not properly initialized");
		return personRepository;
	}

	public Person newPerson(String firstName, String lastName) {
		return new Person(ID_SEQUENCE.incrementAndGet(), firstName, lastName);
	}

	public Person find(Long id) {
		return getPersonRepository().findById(id).orElse(null);
	}

	public Person save(Person person) {
		return getPersonRepository().save(person);
	}

	public boolean delete(Person person) {
		getPersonRepository().delete(person);
		return (find(person.getId()) == null);
	}
}
