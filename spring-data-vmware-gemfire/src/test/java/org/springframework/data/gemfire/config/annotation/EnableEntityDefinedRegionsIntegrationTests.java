/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.Function;

import org.junit.Test;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.data.gemfire.GemfireUtils;
import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.mock.env.MockPropertySource;

/**
 * Integration Tests for {@link EnableEntityDefinedRegions} and {@link EntityDefinedRegionsConfiguration}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 * @see org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions
 * @see org.springframework.data.gemfire.config.annotation.EntityDefinedRegionsConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects
 * @since 2.0.2
 */
public class EnableEntityDefinedRegionsIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private ConfigurableApplicationContext newApplicationContext(PropertySource<?> testPropertySource,
		Class<?>... annotatedClasses) {

		Function<ConfigurableApplicationContext, ConfigurableApplicationContext> applicationContextInitializer = applicationContext -> {

			MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();

			propertySources.addFirst(testPropertySource);

			return applicationContext;
		};

		return newApplicationContext(applicationContextInitializer, annotatedClasses);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void entityBasePackagesAreIdentifiedByProperty() {

		MockPropertySource testPropertySource = new MockPropertySource()
			.withProperty("spring.data.gemfire.entities.base-packages", Person.class.getPackage().getName());

		newApplicationContext(testPropertySource, TestConfiguration.class);

		Region<Long, Person> people = getBean("People", Region.class);

		assertThat(people).isNotNull();
		assertThat(people.getName()).isEqualTo("People");
		assertThat(people.getFullPath()).isEqualTo(GemfireUtils.toRegionPath("People"));
		assertThat(people.getAttributes()).isNotNull();
		assertThat(people.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	@EnableEntityDefinedRegions
	static class TestConfiguration { }

}
