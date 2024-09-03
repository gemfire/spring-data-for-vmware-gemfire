/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.geode.cache.RegionAttributes;
import org.apache.geode.cache.client.ClientCache;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Sort;
import org.springframework.data.gemfire.RegionAttributesFactoryBean;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests for {@link PersonRepository}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.repository.sample.Person
 * @see org.springframework.data.gemfire.repository.sample.PersonRepository
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @since 1.4.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = PersonRepositoryIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class PersonRepositoryIntegrationTests extends IntegrationTestsSupport {

	private static final String DEFAULT_GEMFIRE_LOG_LEVEL = "error";
	private static final String GEMFIRE_LOG_LEVEL = System.getProperty("gemfire.log-level", DEFAULT_GEMFIRE_LOG_LEVEL);

	protected final AtomicLong ID_SEQUENCE = new AtomicLong(0L);

	private Person cookieDoe = newPerson("Cookie", "Doe");
	private Person janeDoe = newPerson("Jane", "Doe");
	private Person jonDoe = newPerson("Jon", "Doe");
	private Person pieDoe = newPerson("Pie", "Doe");
	private Person sourDoe = newPerson("Sour", "Doe");
	private Person jackHandy = newPerson("Jack", "Handy");
	private Person sandyHandy = newPerson("Sandy", "Handy");
	private Person imaPigg = newPerson("Ima", "Pigg");

	@Autowired
	private PersonRepository personRepository;

	@Before
	public void setup() {

		if (this.personRepository.count() == 0) {
			sourDoe = personRepository.save(sourDoe);
			sandyHandy = personRepository.save(sandyHandy);
			jonDoe = personRepository.save(jonDoe);
			jackHandy = personRepository.save(jackHandy);
			janeDoe = personRepository.save(janeDoe);
			pieDoe = personRepository.save(pieDoe);
			imaPigg = personRepository.save(imaPigg);
			cookieDoe = personRepository.save(cookieDoe);
		}

		assertThat(this.personRepository.count()).isEqualTo(8L);
	}

	protected <T> List<T> asList(Iterable<T> iterable) {

		List<T> list = new ArrayList<>();

		for (T element : iterable) {
			list.add(element);
		}

		return list;
	}

	protected Person newPerson(String firstName, String lastName) {
		return new Person(ID_SEQUENCE.incrementAndGet(), firstName, lastName);
	}

	protected Sort.Order newSortOrder(String property) {
		return newSortOrder(property, Sort.Direction.ASC);
	}

	protected Sort.Order newSortOrder(String property, Sort.Direction direction) {
		return new Sort.Order(direction, property);
	}

	protected Sort newSort(Sort.Order... orders) {
		return Sort.by(orders);
	}

	@Test
	public void findAllPeopleSorted() {

		Iterable<Person> people = this.personRepository.findAll(newSort(newSortOrder("firstname")));

		assertThat(people).isNotNull();

		List<Person> peopleList = asList(people);

		assertThat(peopleList.size()).isEqualTo(8);
		assertThat(peopleList).isEqualTo(
			Arrays.asList(cookieDoe, imaPigg, jackHandy, janeDoe, jonDoe, pieDoe, sandyHandy, sourDoe));
	}

	@Test
	public void findDistinctPeopleOrderedByLastnameDescendingFirstnameAscending() {

		List<Person> actualPeople = this.personRepository.findDistinctPeopleByOrderByLastnameDesc(
			newSort(newSortOrder("firstname")));

		assertThat(actualPeople).isNotNull();
		assertThat(actualPeople.size()).isEqualTo(8);
		assertThat(actualPeople).isEqualTo(Arrays.asList(
			imaPigg, jackHandy, sandyHandy, cookieDoe, janeDoe, jonDoe, pieDoe, sourDoe));
	}

	@Test
	public void findDistinctPeopleByLastnameUnordered() {

		List<Person> actualPeople = this.personRepository.findDistinctByLastname("Handy", null);

		assertThat(actualPeople).isNotNull();
		assertThat(actualPeople.size()).isEqualTo(2);
		assertThat(actualPeople).containsAll(Arrays.asList(jackHandy, sandyHandy));
	}

	@Test
	public void findDistinctPeopleByFirstOrLastNameWithSort() {

		Collection<Person> people = this.personRepository.findDistinctByFirstnameOrLastname("Cookie", "Pigg",
			newSort(newSortOrder("lastname", Sort.Direction.DESC), newSortOrder("firstname", Sort.Direction.ASC)));

		assertThat(people).isNotNull();
		assertThat(people.size()).isEqualTo(2);

		Iterator<Person> peopleIterator = people.iterator();

		assertThat(peopleIterator.hasNext()).isTrue();
		assertThat(peopleIterator.next()).isEqualTo(imaPigg);
		assertThat(peopleIterator.hasNext()).isTrue();
		assertThat(peopleIterator.next()).isEqualTo(cookieDoe);
		assertThat(peopleIterator.hasNext()).isFalse();
	}

	@Test
	public void findPersonByFirstAndLastNameIgnoringCase() {

		Collection<Person> people = this.personRepository.findByFirstnameIgnoreCaseAndLastnameIgnoreCase("jON", "doE");

		assertThat(people).isNotNull();
		assertThat(people.size()).isEqualTo(1);
		assertThat(people.iterator().next()).isEqualTo(jonDoe);
	}

	@Test
	public void findByFirstAndLastNameAllIgnoringCase() {

		Collection<Person> people = this.personRepository.findByFirstnameAndLastnameAllIgnoringCase("IMa", "PIGg");

		assertThat(people).isNotNull();
		assertThat(people.size()).isEqualTo(1);
		assertThat(people.iterator().next()).isEqualTo(imaPigg);
	}

	@Configuration
	@EnableGemfireRepositories(basePackages = "org.springframework.data.gemfire.repository.sample",
		includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
			value = org.springframework.data.gemfire.repository.sample.PersonRepository.class))
	public static class TestConfiguration {

		Properties gemfireProperties() {

			Properties gemfireProperties = new Properties();

			gemfireProperties.setProperty("name", applicationName());
			gemfireProperties.setProperty("log-level", logLevel());

			return gemfireProperties;
		}

		String applicationName() {
			return PersonRepositoryIntegrationTests.class.getSimpleName();
		}

		String logLevel() {
			return GEMFIRE_LOG_LEVEL;
		}

		@Bean
		ClientCacheFactoryBean gemfireCache() {

			ClientCacheFactoryBean gemfireCache = new ClientCacheFactoryBean();

			gemfireCache.setClose(true);
			gemfireCache.setProperties(gemfireProperties());

			return gemfireCache;
		}

		@Bean(name = "simple")
		ClientRegionFactoryBean<Long, Person> simpleRegion(ClientCache gemfireCache, RegionAttributes<Long, Person> simpleRegionAttributes) {

			ClientRegionFactoryBean<Long, Person> simpleRegion = new ClientRegionFactoryBean<>();

			simpleRegion.setAttributes(simpleRegionAttributes);
			simpleRegion.setCache(gemfireCache);
			simpleRegion.setPersistent(false);

			return simpleRegion;
		}

		@Bean
		RegionAttributesFactoryBean<Long, Person> simpleRegionAttributes() {

			RegionAttributesFactoryBean<Long, Person> simpleRegionAttributes = new RegionAttributesFactoryBean<>();

			simpleRegionAttributes.setKeyConstraint(Long.class);
			simpleRegionAttributes.setValueConstraint(Person.class);

			return simpleRegionAttributes;
		}
	}
}
