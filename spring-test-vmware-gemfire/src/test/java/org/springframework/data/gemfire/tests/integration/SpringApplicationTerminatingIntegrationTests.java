/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Scanner;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.tests.process.ProcessWrapper;

/**
 * Integration Tests testing and asserting Spring Application termination configuration.
 *
 * @author John Blum
 * @see Duration
 * @see Instant
 * @see Test
 * @see AnnotationConfigApplicationContext
 * @see Bean
 * @see Configuration
 * @see Import
 * @see Profile
 * @see ProcessWrapper
 * @since 0.0.24
 */
@SuppressWarnings("unused")
public class SpringApplicationTerminatingIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	private static Instant startTime;

	private static ProcessWrapper springApplicationProcess;

	@BeforeClass
	public static void startSpringApplication() throws IOException {

		springApplicationProcess = run(TestSpringApplicationConfiguration.class,
			"-Dspring.profiles.active=SpringApplicationTerminator");

		startTime = Instant.now();
	}

	@AfterClass
	public static void assertSpringApplicationTerminated() {

		Condition springApplicationTerminatedCondition = () -> springApplicationProcess.isNotRunning();

		waitOn(springApplicationTerminatedCondition, Duration.ofSeconds(10L).toMillis());

		assertThat(Duration.between(startTime, Instant.now())).isGreaterThan(Duration.ofSeconds(5));
	}

	@Test
	public void springApplicationIsRunning() {

		assertThat(springApplicationProcess).isNotNull();
		assertThat(springApplicationProcess.isAlive()).isTrue();
		assertThat(springApplicationProcess.isRunning()).isTrue();
	}

	@Configuration
	@Profile("SpringApplicationTerminator")
	@Import(SpringApplicationTerminatorConfiguration.class)
	static class TestSpringApplicationConfiguration {

		public static void main(String[] args) {

			AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

			applicationContext.register(TestSpringApplicationConfiguration.class);
			applicationContext.registerShutdownHook();

			new Scanner(System.in).nextLine();
		}

		@Bean
		SpringApplicationTerminatorConfigurer terminatorConfigurer() {

			return new SpringApplicationTerminatorConfigurer() {

				@Override
				public Duration delay() {
					return Duration.ofSeconds(5);
				}
			};
		}
	}
}
