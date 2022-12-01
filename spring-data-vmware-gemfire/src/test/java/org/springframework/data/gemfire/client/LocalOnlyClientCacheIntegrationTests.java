/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Month;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.test.model.Gender;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the configuration of a {@link ClientRegionShortcut#LOCAL}
 * {@link ClientCache} {@link Region}.
 *
 * @author John Blum
 * @see Test
 * @see Region
 * @see ClientCache
 * @see IntegrationTestsSupport
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 1.6.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class LocalOnlyClientCacheIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("People")
	private Region<Long, Person> people;

	@Before
	public void assertPeopleRegionConfiguration() {

		assertThat(this.people).isNotNull();
		assertThat(this.people.getName()).isEqualTo("People");
		assertThat(this.people.getFullPath()).isEqualTo(GemfireUtils.toRegionPath("People"));
		assertThat(this.people.getAttributes()).isNotNull();
		assertThat(this.people.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		assertThat(this.people.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		assertThat(this.people.getAttributes().getValueConstraint()).isEqualTo(Person.class);
		assertThat(this.people).hasSize(0);
	}

	@Test
	public void putAndGetPersonIsSuccessful() {

		Person jonDoe = Person.newPerson("Jon", "Doe",
			Person.newBirthDate(1974, Month.MAY, 5), Gender.MALE);

		assertThat(this.people).hasSize(0);
		assertThat(this.people.put(jonDoe.getId(), jonDoe));
		assertThat(this.people).hasSize(1);
		assertThat(this.people.get(jonDoe.getId())).isEqualTo(jonDoe);
		assertThat(this.people).hasSize(1);
	}

	@ClientCacheApplication
	@EnableEntityDefinedRegions(basePackageClasses = Person.class,
		clientRegionShortcut = ClientRegionShortcut.LOCAL, strict = true)
	static class TestConfiguration { }

}
