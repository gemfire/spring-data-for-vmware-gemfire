/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.vmware.gemfire.testcontainers.GemFireCluster;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.query.CqEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.listener.annotation.ContinuousQuery;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.test.model.Gender;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.test.repo.PersonRepository;
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
public class EnableContinuousQueriesWithClusterConfigurationIntegrationTests {

	private static final BlockingQueue<Person> events = new ArrayBlockingQueue<>(2);

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		int port = 0;

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1);

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
		System.setProperty("spring.data.gemfire.management.http.port", String.valueOf(gemFireCluster.getHttpPorts().get(0)));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
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

	@EnablePdx(includeDomainTypes = Person.class)
	@EnableContinuousQueries
	@EnableClusterConfiguration(useHttp = true, requireHttps = false)
	@EnableEntityDefinedRegions(basePackageClasses = Person.class)
	@EnableGemfireRepositories(basePackageClasses = PersonRepository.class)
	@ClientCacheApplication(logLevel = "error", subscriptionEnabled = true)
	static class TestConfiguration {

		@ContinuousQuery(name = "PersonEvents", query = "SELECT * FROM /People")
		public void peopleEventHandler(CqEvent event) {

			Optional.ofNullable(event)
					.map(CqEvent::getNewValue)
					.filter(newValue -> newValue instanceof Person)
					.map(newValue -> (Person) newValue)
					.ifPresent(events::offer);
		}

		@Bean
		ClientCacheConfigurer clientCachePoolPortConfigurer() {

			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
					Collections.singletonList(new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}
	}
}
