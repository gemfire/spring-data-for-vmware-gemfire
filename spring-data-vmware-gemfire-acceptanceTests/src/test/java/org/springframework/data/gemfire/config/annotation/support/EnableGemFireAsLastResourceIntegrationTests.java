/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation.support;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.resource.ResourceException;

import org.apache.geode.cache.GemFireCache;
import org.apache.geode.ra.GFConnection;
import org.apache.geode.ra.GFConnectionFactory;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.config.annotation.EnableGemFireAsLastResource;
import org.springframework.data.gemfire.config.annotation.GemFireAsLastResourceConfiguration;
import org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for {@link EnableGemFireAsLastResource}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.mockito.Mock
 * @see org.mockito.Mockito
 * @see org.apache.geode.ra.GFConnection
 * @see org.apache.geode.ra.GFConnectionFactory
 * @see org.springframework.data.gemfire.config.annotation.EnableGemFireAsLastResource
 * @see org.springframework.data.gemfire.config.annotation.GemFireAsLastResourceConfiguration
 * @see org.springframework.data.gemfire.tests.integration.SpringApplicationContextIntegrationTestsSupport
 * @see org.springframework.transaction.PlatformTransactionManager
 * @see org.springframework.transaction.annotation.EnableTransactionManagement
 * @see org.springframework.transaction.annotation.Transactional
 * @since 2.0.0
 */
public class EnableGemFireAsLastResourceIntegrationTests extends SpringApplicationContextIntegrationTestsSupport {

	private static final List<SpringGemFireTransactionEvents> transactionEvents = new ArrayList<>();

	@Before
	public void setup() {
		transactionEvents.clear();
	}

	@Test
	public void configurationIsCorrect() {

		newApplicationContext(TestGemFireAsLastResourceConfiguration.class);

		GemFireCache gemfireCache = getBean("gemfireCache", GemFireCache.class);

		Assertions.assertThat(gemfireCache).isNotNull();
		Assertions.assertThat(gemfireCache.getCopyOnRead()).isTrue();

		GemFireAsLastResourceConnectionAcquiringAspect connectionAcquiringAspect =
			getBean(GemFireAsLastResourceConnectionAcquiringAspect.class);

		Assertions.assertThat(connectionAcquiringAspect).isNotNull();
		Assertions.assertThat(connectionAcquiringAspect.getOrder()).isEqualTo(3);

		GemFireAsLastResourceConnectionClosingAspect connectionClosingAspect =
			getBean(GemFireAsLastResourceConnectionClosingAspect.class);

		Assertions.assertThat(connectionClosingAspect).isNotNull();
		Assertions.assertThat(connectionClosingAspect.getOrder()).isEqualTo(1);
	}

	private void transactionEventsForTransactionalServiceAreCorrect(
			Class<? extends TestTransactionalService> transactionalServiceType) {

		ConfigurableApplicationContext applicationContext =
			newApplicationContext(TestSpringApplicationConfiguration.class);

		Assertions.assertThat(applicationContext).isNotNull();
		Assertions.assertThat(applicationContext.containsBean(transactionalServiceType.getSimpleName())).isTrue();
		Assertions.assertThat(transactionEvents).isEmpty();

		GFConnectionFactory gemfireConnectionFactory = applicationContext.getBean(GFConnectionFactory.class);

		Assertions.assertThat(gemfireConnectionFactory).isNotNull();

		GemFireAsLastResourceConnectionAcquiringAspect connectionAcquiringAspect =
			applicationContext.getBean(GemFireAsLastResourceConnectionAcquiringAspect.class);

		Assertions.assertThat(connectionAcquiringAspect).isNotNull();
		Assertions.assertThat(connectionAcquiringAspect.getGemFireConnectionFactory()).isSameAs(gemfireConnectionFactory);

		TestTransactionalService service =
			applicationContext.getBean(transactionalServiceType.getSimpleName(), transactionalServiceType);

		Assertions.assertThat(service).isNotNull();

		service.doInTransactionCommits();

		Assertions.assertThat(transactionEvents).containsExactly(SpringGemFireTransactionEvents.BEGIN,
			SpringGemFireTransactionEvents.GET_CONNECTION,
			SpringGemFireTransactionEvents.TRANSACTION,
			SpringGemFireTransactionEvents.COMMIT,
			SpringGemFireTransactionEvents.CLOSE_CONNECTION);

		transactionEvents.clear();

		Assertions.assertThat(transactionEvents).isEmpty();

		try {
			service.doInTransactionRollsback();
		}
		catch (RuntimeException expected) {
			Assertions.assertThat(expected).hasMessage("TEST");
			Assertions.assertThat(expected).hasNoCause();

			throw expected;
		}
		finally {
			Assertions.assertThat(transactionEvents).containsExactly(SpringGemFireTransactionEvents.BEGIN,
				SpringGemFireTransactionEvents.GET_CONNECTION,
				SpringGemFireTransactionEvents.TRANSACTION,
				SpringGemFireTransactionEvents.ROLLBACK,
				SpringGemFireTransactionEvents.CLOSE_CONNECTION);
		}
	}

	@Test(expected = RuntimeException.class)
	public void transactionEventsForTransactionalServiceClassAreCorrect() {
		transactionEventsForTransactionalServiceAreCorrect(TestTransactionalServiceClass.class);
	}

	@Test(expected = RuntimeException.class)
	public void transactionEventsForTransactionalServiceMethodsAreCorrect() {
		transactionEventsForTransactionalServiceAreCorrect(TestTransactionalServiceMethods.class);
	}

	@Test(expected = IllegalStateException.class)
	public void missingEnableTransactionManagerAnnotationThrowsIllegalStateException() {
		try {
			newApplicationContext(TestMissingEnableTransactionManagementAnnotationConfiguration.class);
		}
		catch (BeanCreationException expected) {

			Assertions.assertThat(expected).hasMessageStartingWith(String.format("Error creating bean with name '%s'",
				GemFireAsLastResourceConfiguration.class.getName()));

			Assertions.assertThat(expected).hasCauseInstanceOf(IllegalStateException.class);

			Assertions.assertThat(expected.getCause()).hasMessage("The @EnableGemFireAsLastResource annotation may only be used"
				+ " on a Spring application @Configuration class that is also annotated with"
				+ " @EnableTransactionManagement having an explicit [order] set");

			Assertions.assertThat(expected.getCause()).hasNoCause();

			throw (IllegalStateException) expected.getCause();
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void missingEnableTransactionManagementOrderAttributeConfigurationThrowsIllegalArgumentException() {
		try {
			newApplicationContext(TestMissingEnableTransactionManagementOrderAttributeConfiguration.class);
		}
		catch (BeanCreationException expected) {

			Assertions.assertThat(expected).hasMessageStartingWith(String.format("Error creating bean with name '%s'",
				GemFireAsLastResourceConfiguration.class.getName()));

			Assertions.assertThat(expected).hasCauseInstanceOf(IllegalArgumentException.class);

			Assertions.assertThat(expected.getCause()).hasMessage("The @EnableTransactionManagement(order) attribute value"
				+ " [2147483647] must be explicitly set to a value other than Integer.MAX_VALUE or Integer.MIN_VALUE");

			Assertions.assertThat(expected.getCause()).hasNoCause();

			throw (IllegalArgumentException) expected.getCause();
		}
	}

	@Configuration
	@EnableGemFireAsLastResource
	@EnableTransactionManagement(order = 2)
	@SuppressWarnings("unused")
	static class TestGemFireAsLastResourceConfiguration {

		@Bean("gemfireCache")
		GemFireCache mockGemFireCache() {

			AtomicBoolean copyOnRead = new AtomicBoolean(false);

			GemFireCache mockGemFireCache = Mockito.mock(GemFireCache.class);

			Mockito.doAnswer(invocation -> {
				copyOnRead.set(invocation.getArgument(0));
				return null;
			}).when(mockGemFireCache).setCopyOnRead(ArgumentMatchers.anyBoolean());

			Mockito.when(mockGemFireCache.getCopyOnRead()).thenAnswer(invocation -> copyOnRead.get());

			return mockGemFireCache;
		}

		@Bean
		GFConnectionFactory mockGemFireConnectionFactory() throws ResourceException {

			GFConnectionFactory mockGemFireConnectionFactory = Mockito.mock(GFConnectionFactory.class);

			GFConnection mockGemFireConnection = Mockito.mock(GFConnection.class);

			Mockito.when(mockGemFireConnectionFactory.getConnection()).thenAnswer(invocation -> {
				transactionEvents.add(SpringGemFireTransactionEvents.GET_CONNECTION);
				return mockGemFireConnection;
			});

			Mockito.doAnswer(invocation -> transactionEvents.add(SpringGemFireTransactionEvents.CLOSE_CONNECTION))
				.when(mockGemFireConnection).close();

			return mockGemFireConnectionFactory;
		}

		@Bean("transactionManager")
		PlatformTransactionManager mockTransactionManager() {

			PlatformTransactionManager mockTransactionManager = Mockito.mock(PlatformTransactionManager.class);

			Mockito.when(mockTransactionManager.getTransaction(ArgumentMatchers.any(TransactionDefinition.class))).thenAnswer(invocation -> {

				TransactionStatus mockTransactionStatus = Mockito.mock(TransactionStatus.class);

				transactionEvents.add(SpringGemFireTransactionEvents.BEGIN);

				return mockTransactionStatus;
			});

			Mockito.doAnswer(invocation -> transactionEvents.add(SpringGemFireTransactionEvents.COMMIT))
				.when(mockTransactionManager).commit(ArgumentMatchers.any(TransactionStatus.class));

			Mockito.doAnswer(invocation -> transactionEvents.add(SpringGemFireTransactionEvents.ROLLBACK))
				.when(mockTransactionManager).rollback(ArgumentMatchers.any(TransactionStatus.class));

			return mockTransactionManager;
		}
	}

	@Configuration
	@EnableGemFireAsLastResource
	static class TestMissingEnableTransactionManagementAnnotationConfiguration { }

	@Configuration
	@EnableGemFireAsLastResource
	@EnableTransactionManagement
	@SuppressWarnings("unused")
	static class TestMissingEnableTransactionManagementOrderAttributeConfiguration {

		@Bean("transactionManager")
		PlatformTransactionManager mockTransactionManager() {
			return Mockito.mock(PlatformTransactionManager.class);
		}
	}

	@Configuration
	@Import(TestGemFireAsLastResourceConfiguration.class)
	@SuppressWarnings("unused")
	static class TestSpringApplicationConfiguration {

		@Bean("TestTransactionalServiceClass")
		TestTransactionalServiceClass testTransactionalServiceClass() {
			return new TestTransactionalServiceClass();
		}

		@Bean("TestTransactionalServiceMethods")
		TestTransactionalServiceMethods testTransactionalServiceMethods() {
			return new TestTransactionalServiceMethods();
		}
	}

	enum SpringGemFireTransactionEvents {

		BEGIN,
		CLOSE_CONNECTION,
		COMMIT,
		GET_CONNECTION,
		ROLLBACK,
		TRANSACTION,

	}

	interface TestTransactionalService {

		void doInTransactionCommits();

		void doInTransactionRollsback();

	}

	@Transactional
	@Service("TestTransactionalServiceClass")
	static class TestTransactionalServiceClass implements TestTransactionalService {

		public void doInTransactionCommits() {
			transactionEvents.add(SpringGemFireTransactionEvents.TRANSACTION);
		}

		public void doInTransactionRollsback() {
			transactionEvents.add(SpringGemFireTransactionEvents.TRANSACTION);
			throw new RuntimeException("TEST");
		}
	}

	@Service("TestTransactionalServiceMethods")
	static class TestTransactionalServiceMethods implements TestTransactionalService {

		@Transactional
		public void doInTransactionCommits() {
			transactionEvents.add(SpringGemFireTransactionEvents.TRANSACTION);
		}

		@Transactional
		public void doInTransactionRollsback() {
			transactionEvents.add(SpringGemFireTransactionEvents.TRANSACTION);
			throw new RuntimeException("TEST");
		}
	}
}
