/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.client;

import java.time.Month;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
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

		Assertions.assertThat(this.people).isNotNull();
		Assertions.assertThat(this.people.getName()).isEqualTo("People");
		Assertions.assertThat(this.people.getFullPath()).isEqualTo(GemfireUtils.toRegionPath("People"));
		Assertions.assertThat(this.people.getAttributes()).isNotNull();
		Assertions.assertThat(this.people.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
		Assertions.assertThat(this.people.getAttributes().getKeyConstraint()).isEqualTo(Long.class);
		Assertions.assertThat(this.people.getAttributes().getValueConstraint()).isEqualTo(Person.class);
		Assertions.assertThat(this.people).hasSize(0);
	}

	@Test
	public void putAndGetPersonIsSuccessful() {

		Person jonDoe = Person.newPerson("Jon", "Doe",
			Person.newBirthDate(1974, Month.MAY, 5), Gender.MALE);

		Assertions.assertThat(this.people).hasSize(0);
		Assertions.assertThat(this.people.put(jonDoe.getId(), jonDoe));
		Assertions.assertThat(this.people).hasSize(1);
		Assertions.assertThat(this.people.get(jonDoe.getId())).isEqualTo(jonDoe);
		Assertions.assertThat(this.people).hasSize(1);
	}

	@ClientCacheApplication
	@EnableEntityDefinedRegions(basePackageClasses = Person.class,
		clientRegionShortcut = ClientRegionShortcut.LOCAL, strict = true)
	static class TestConfiguration { }

}
