/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.query.CqEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.test.model.Gender;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.test.repo.PersonRepository;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing the combination of {@link EnableContinuousQueries} with {@link EnableClusterConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.query.CqEvent
 * @see org.springframework.data.gemfire.config.annotation.EnableClusterConfiguration
 * @see org.springframework.data.gemfire.config.annotation.EnableContinuousQueries
 * @see org.springframework.data.gemfire.listener.annotation.ContinuousQuery
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see <a href="https://jira.spring.io/browse/DATAGEODE-73">Fix race condition between ContinuousQuery registration and EnableClusterConfiguration Region creation.</a>
 * @since 2.0.3
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EnableContinuousQueriesWithClusterConfigurationIntegrationTests.TestConfiguration.class)
@SuppressWarnings("unused")
public class EnableContinuousQueriesWithClusterConfigurationIntegrationTests
		extends ForkingClientServerIntegrationTestsSupport {

	private static final BlockingQueue<Person> events = new ArrayBlockingQueue<>(2);

	@BeforeClass
	public static void startGemFireServer() throws Exception {
		startGemFireServer(GemFireServerConfiguration.class);
	}

	@Autowired
	private PersonRepository personRepository;

	@Test
	public void personEventsFired() throws Exception {

		Person jonDoe = Person.newPerson(1L, "Jon", "Doe", null, Gender.MALE);

		jonDoe = this.personRepository.save(jonDoe);

		assertThat(this.personRepository.findById(jonDoe.getId()).orElse(null)).isEqualTo(jonDoe);
		assertThat(events.poll(5L, TimeUnit.SECONDS)).isEqualTo(jonDoe);

		Person janeDoe = Person.newPerson(2L, "Jane", "Doe", null, Gender.FEMALE);

		janeDoe = this.personRepository.save(janeDoe);

		assertThat(this.personRepository.findById(janeDoe.getId()).orElse(null)).isEqualTo(janeDoe);
		assertThat(events.poll(5L, TimeUnit.SECONDS)).isEqualTo(janeDoe);
	}

	@Configuration
	@EnableContinuousQueries
	@Import(GemFireClientConfiguration.class)
	static class TestConfiguration {

		@ContinuousQuery(name = "PersonEvents", query = "SELECT * FROM /People")
		public void peopleEventHandler(CqEvent event) {

			Optional.ofNullable(event)
				.map(CqEvent::getNewValue)
				.filter(newValue -> newValue instanceof Person)
				.map(newValue -> (Person) newValue)
				.ifPresent(events::offer);
		}
	}

	@ClientCacheApplication(logLevel = "error", subscriptionEnabled = true)
	@EnableClusterConfiguration
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableGemfireRepositories(basePackageClasses = PersonRepository.class)
	static class GemFireClientConfiguration {

		@Bean
		ClientCacheConfigurer clientCachePoolPortConfigurer(
				@Value("${" + GEMFIRE_CACHE_SERVER_PORT_PROPERTY + ":40404}") int port) {

			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setServers(
				Collections.singletonList(new ConnectionEndpoint("localhost", port)));
		}
	}

	@CacheServerApplication(name = "EnableContinuousQueriesWithClusterConfigurationIntegrationTests", logLevel = "error")
	static class GemFireServerConfiguration {

		public static void main(String[] args) {
			runSpringApplication(GemFireServerConfiguration.class, args);
		}
	}
}
