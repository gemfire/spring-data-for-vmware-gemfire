/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalStateException;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.ExternalResource;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySources;
import org.springframework.lang.NonNull;
import org.springframework.mock.env.MockPropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Integration Tests testing and asserting the cleanup of the Spring container {@link ConfigurableEnvironment}
 * and {@link PropertySources} linked to Unit & Integration testing.
 *
 * @author John Blum
 * @see ConfigurableApplicationContext
 * @see Configuration
 * @see ConfigurableEnvironment
 * @see PropertySources
 * @see MockPropertySource
 * @see ContextConfiguration
 * @see SpringRunner
 * @since 0.0.24
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class SpringEnvironmentPropertySourcesCleanupIntegrationTests extends IntegrationTestsSupport {

	private static final AtomicReference<ConfigurableApplicationContext> applicationContextReference =
		new AtomicReference<>(null);

	private static final String MOCK_PROPERTY_SOURCE_NAME = "MockPropertySource";

	@ClassRule
	public static final ExternalResource SPRING_ENVIRONMENT_RESOURCE_CLEANUP = new ExternalResource() {

		@Override
		protected void after() {

			super.after();

			Optional.ofNullable(applicationContextReference.get())
				.map(ConfigurableApplicationContext::getEnvironment)
				.map(ConfigurableEnvironment::getPropertySources)
				.filter(propertySources -> Boolean.FALSE.equals(propertySources.contains(MOCK_PROPERTY_SOURCE_NAME)))
				//.map(success -> { System.err.println("SUCCESS!"); return success; })
				.orElseThrow(() -> newIllegalStateException(String.format(
					"Spring Environment should not contain the [%s] PropertySource", MOCK_PROPERTY_SOURCE_NAME)));
		}
	};

	@Autowired
	private ConfigurableApplicationContext applicationContext;

	@Before
	public void setup() {

		assertThat(this.applicationContext).isNotNull();
		assertThat(this.applicationContext.isActive()).isTrue();

		applicationContextReference.set(this.applicationContext);
	}

	@Test
	public void applicationContextEnvironmentContainsMockPropertySource() {

		ConfigurableEnvironment environment = this.applicationContext.getEnvironment();

		assertThat(environment).isNotNull();

		PropertySources propertySources = environment.getPropertySources();

		assertThat(propertySources).isNotNull();
		assertThat(propertySources.contains(MOCK_PROPERTY_SOURCE_NAME)).isTrue();
	}

	@Configuration
	static class TestConfiguration {

		@EventListener(classes = ContextRefreshedEvent.class)
		public void addTestPropertySource(@NonNull ContextRefreshedEvent event) {

			Optional.ofNullable(event)
				.map(ContextRefreshedEvent::getApplicationContext)
				.filter(ConfigurableApplicationContext.class::isInstance)
				.map(ConfigurableApplicationContext.class::cast)
				.map(ConfigurableApplicationContext::getEnvironment)
				.map(ConfigurableEnvironment::getPropertySources)
				.ifPresent(propertySources -> {

					MockPropertySource mockPropertySource = new MockPropertySource("MockPropertySource");

					propertySources.addLast(mockPropertySource);
				});
		}
	}
}
