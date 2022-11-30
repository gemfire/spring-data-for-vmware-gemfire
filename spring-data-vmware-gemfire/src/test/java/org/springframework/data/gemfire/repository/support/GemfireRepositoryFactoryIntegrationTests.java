// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.mapping.Regions;
import org.springframework.data.gemfire.repository.sample.Person;
import org.springframework.data.gemfire.repository.sample.PersonRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.support.Repositories;
import org.springframework.test.context.ContextConfiguration;

/**
 * Integration Tests for {@link GemfireRepositoryFactory}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.junit.Test
 * @see org.springframework.data.gemfire.repository.support.AbstractGemfireRepositoryFactoryIntegrationTests
 * @see org.springframework.data.gemfire.repository.support.GemfireRepositoryFactory
 */
@ContextConfiguration("../config/repo-context.xml")
@SuppressWarnings("unused")
public class GemfireRepositoryFactoryIntegrationTests extends AbstractGemfireRepositoryFactoryIntegrationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private GemfireMappingContext mappingContext;

	@Override
	protected PersonRepository getRepository(Regions regions) {
		return new GemfireRepositoryFactory(regions, mappingContext).getRepository(PersonRepository.class);
	}

	@Test(expected = IllegalStateException.class)
	public void throwsExceptionIfReferencedRegionIsNotConfigured() {
		new GemfireRepositoryFactory(Collections.emptySet(), mappingContext).getRepository(PersonRepository.class);
	}

	@Test
	public void exposesPersistentProperty() {

		Repositories repositories = new Repositories(applicationContext);

		PersistentEntity<?, ?> entity = repositories.getPersistentEntity(Person.class);

		assertThat(entity).isNotNull();
	}
}
