/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import static org.assertj.core.api.Assertions.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.GemFireCheckedException;
import org.apache.geode.cache.query.FunctionDomainException;
import org.apache.geode.cache.query.QueryException;
import org.apache.geode.cache.query.QueryInvocationTargetException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.gemfire.GemfireQueryException;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.mock.annotation.EnableGemFireMockObjects;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Tests Spring Data for Apache Geode checked persistence {@link Exception Exceptions} translation.
 *
 * @author David Turanski
 * @author John Blum
 * @see org.junit.Test
 * @see PersistenceExceptionTranslationPostProcessor
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class GemfirePersistenceExceptionTranslationIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private TestGemFireRepository gemfireRepository;

	@SuppressWarnings("all")
	private void handleExceptionThrowingCall(SpringExtensions.VoidReturningThrowableOperation operation) {

		try {
			operation.run();
			fail("Should have thrown a QueryException");
		}
		catch (GemfireQueryException ignore) { }
		catch (Throwable cause) {
			fail("Should have thrown a QueryException", cause);
		}
	}

	@Test
	public void exceptionTranslationIsSuccessful() {

		handleExceptionThrowingCall(() -> this.gemfireRepository.doIt(new QueryException()));
		handleExceptionThrowingCall(() -> this.gemfireRepository.doIt(new FunctionDomainException("test")));
		handleExceptionThrowingCall(() -> this.gemfireRepository.doIt(new QueryInvocationTargetException("test")));
	}

	@ClientCacheApplication
	@EnableGemFireMockObjects
	static class TestConfiguration {

		@Bean
		PersistenceExceptionTranslationPostProcessor persistenceExceptionTranslationProcessor() {
			return new PersistenceExceptionTranslationPostProcessor();
		}

		@Bean
		TestGemFireRepository gemFireRepository() {
			return new TestGemFireRepository();
		}
	}

	/**
	 * Wraps {@link GemFireCheckedException} in {@link RuntimeException}.
	 */
	@Repository
	public static class TestGemFireRepository {
		public void doIt(Exception cause)  {
			throw new RuntimeException(cause);
		}
	}
}
