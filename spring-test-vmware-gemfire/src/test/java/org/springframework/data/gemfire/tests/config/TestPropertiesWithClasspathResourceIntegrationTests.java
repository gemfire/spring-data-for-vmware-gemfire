/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.NonNull;

/**
 * Integration Tests with {@link TestProperties} having a {@link ClassPathResource}.
 *
 * @author John Blum
 * @see java.util.Properties
 * @see Test
 * @see Resource
 * @see ClassPathResource
 * @see TestProperties
 * @since 1.0.0
 */
public class TestPropertiesWithClasspathResourceIntegrationTests {

	private static final TestProperties testProperties = new TestProperties() {

		/**
		 * @inheritDoc
		 */
		@Override
		protected @NonNull Resource getTestPropertiesResource() {
			return new ClassPathResource("mock-test.properties");
		}
	};

	@BeforeClass
	public static void testPropertiesArePresent() {
		assertThat(testProperties).isNotNull();
	}

	@Test
	public void getMockTestProperties() {

		assertThat(testProperties)
			.containsExactlyInAnyOrder("app.testProperty", "gem.mockProperty", "system.gem.testProperty");

		assertThat(testProperties.get("app.testProperty")).isEqualTo("X");
		assertThat(testProperties.get("gem.mockProperty")).isEqualTo("Y");
		assertThat(testProperties.get("system.gem.testProperty")).isEqualTo("NO");
	}

	@Test
	public void setGetAndClearProperties() {

		try {
			testProperties.set("myProperty", "TEST") // new
				.set("app.testProperty", "A") // override
				.set("app.mockProperty", "B") // new
				.set("system.gem.testProperty", "YES"); // override

			assertThat(testProperties).containsExactlyInAnyOrder("myProperty",
				"app.testProperty", "app.mockProperty", "gem.mockProperty", "system.gem.testProperty");

			assertThat(testProperties.get("myProperty")).isEqualTo("TEST");
			assertThat(testProperties.get("app.testProperty")).isEqualTo("A");
			assertThat(testProperties.get("app.mockProperty")).isEqualTo("B");
			assertThat(testProperties.get("gem.mockProperty")).isEqualTo("Y");
			assertThat(testProperties.get("system.gem.testProperty")).isEqualTo("YES");

			testProperties.clear();

			assertThat(testProperties).containsExactlyInAnyOrder("app.testProperty",
				"gem.mockProperty", "system.gem.testProperty");

			assertThat(testProperties).doesNotContain("myProperty", "app.mockProperty");
			assertThat(testProperties.get("app.testProperty")).isEqualTo("X");
			assertThat(testProperties.get("gem.mockProperty")).isEqualTo("Y");
			assertThat(testProperties.get("system.gem.testProperty")).isEqualTo("NO");
		}
		finally {
			testProperties.getTestProperties().clear();
		}
	}

	@Test
	public void configureAndClearSystemProperties() {

		try {
			assertThat(System.getProperties())
				.doesNotContainKeys("app.testProperty", "gem.mockProperty", "gem.testProperty");

			testProperties.configureSystemProperties();

			assertThat(System.getProperties()).containsKeys("gem.testProperty");
			assertThat(System.getProperties()).doesNotContainKeys("app.testProperty", "gem.mockProperty");
			assertThat(System.getProperty("gem.testProperty")).isEqualTo("NO");

			testProperties.clearSystemProperties();

			assertThat(System.getProperties())
				.doesNotContainKeys("app.testProperty", "gem.mockProperty", "gem.testProperty");
		}
		finally {
			testProperties.forEach(System::clearProperty);
			assertThat(System.getProperties())
				.doesNotContainKeys("app.testProperty", "gem.mockProperty", "gem.testProperty");
		}
	}
}
