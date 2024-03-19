/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.repository.query.parser.PartTree;

/**
 * Unit Tests for {@link GemfireQueryCreator}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.junit.Test
 * @see GemfireQueryCreator
 */
public class GemfireQueryCreatorUnitTests {

	GemfirePersistentEntity<Person> entity;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() {
		entity = (GemfirePersistentEntity<Person>) new GemfireMappingContext().getPersistentEntity(Person.class);
	}

	@Test
	public void createsQueryForSimplePropertyReferenceCorrectly() {

		PartTree partTree = new PartTree("findByLastname", Person.class);

		GemfireQueryCreator queryCreator = new GemfireQueryCreator(partTree, entity);

		QueryString query = queryCreator.createQuery();

		assertThat(query.toString()).isEqualTo("SELECT * FROM /simple x WHERE x.lastname = $1");
	}

	@Test
	public void createsQueryForNestedPropertyReferenceCorrectly() {

		PartTree partTree = new PartTree("findPersonByAddressCity", Person.class);

		GemfireQueryCreator queryCreator = new GemfireQueryCreator(partTree, entity);

		QueryString query = queryCreator.createQuery();

		assertThat(query.toString()).isEqualTo("SELECT * FROM /simple x WHERE x.address.city = $1");
	}
}
