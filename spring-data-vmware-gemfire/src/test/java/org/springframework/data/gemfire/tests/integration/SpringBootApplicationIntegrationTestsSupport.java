/*
 * Copyright 2023-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.lang.NonNull;

/**
 * The {@link SpringBootApplicationIntegrationTestsSupport} class is an extension of Spring Test
 * for Apache Geode/VMware GemFire's {@link SpringApplicationContextIntegrationTestsSupport} class
 * used to construct a new Spring {@link ConfigurableApplicationContext} using Spring Boot's
 * {@link SpringApplicationBuilder} class.
 *
 * @author John Blum
 * @see SpringApplication
 * @see SpringApplicationBuilder
 * @see ConfigurableApplicationContext
 * @see SpringApplicationContextIntegrationTestsSupport
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public abstract class SpringBootApplicationIntegrationTestsSupport
		extends SpringApplicationContextIntegrationTestsSupport {

	protected static final String[] EMPTY_STRING_ARRAY = {};

	protected String[] getArguments() {
		return EMPTY_STRING_ARRAY;
	}

	protected WebApplicationType getWebApplicationType() {
		return WebApplicationType.NONE;
	}

	@Override
	protected @NonNull ConfigurableApplicationContext newApplicationContext(Class<?>... annotatedClasses) {

		return setApplicationContext(processBeforeRun(processBeforeBuild(
			newSpringApplicationBuilder(annotatedClasses)
				.initializers(this::processBeforeRefresh)
				.web(getWebApplicationType()))
				.build())
				.run(getArguments()));
	}

	protected @NonNull SpringApplicationBuilder newSpringApplicationBuilder(Class<?>... annotatedClasses) {
		return new SpringApplicationBuilder(ArrayUtils.nullSafeArray(annotatedClasses, Class.class));
	}

	protected @NonNull SpringApplicationBuilder processBeforeBuild(@NonNull SpringApplicationBuilder springApplicationBuilder) {
		return springApplicationBuilder;
	}

	protected @NonNull SpringApplication processBeforeRun(@NonNull SpringApplication springApplication) {
		return springApplication;
	}
}
