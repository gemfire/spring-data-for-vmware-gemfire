/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import com.vmware.gemfire.testcontainers.GemFireCluster;
import edu.umd.cs.mtc.MultithreadedTestCase;
import edu.umd.cs.mtc.TestFramework;
import example.app.model.User;
import example.app.repo.UserRepository;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.Getter;
import lombok.Setter;
import org.apache.geode.cache.client.ClientCache;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.mapping.GemfireMappingContext;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.repository.support.GemfireRepositoryFactoryBean;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.gemfire.transaction.config.EnableGemfireCacheTransactions;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Integration Tests asserting the concurrent interaction of multiple {@link ClientCache} (client/server) transactions
 * where 1 transaction commits and the other rolls back.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see edu.umd.cs.mtc.MultithreadedTestCase
 * @see edu.umd.cs.mtc.TestFramework
 * @see org.apache.geode.cache.client.ClientCache
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.transaction.config.EnableGemfireCacheTransactions
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see org.springframework.transaction.annotation.Transactional
 * @since 2.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConcurrentClientTransactionalDataAccessWithRollbackIntegrationTests.TestGeodeClientConfiguration.class)
@SuppressWarnings("unused")
public class ConcurrentClientTransactionalDataAccessWithRollbackIntegrationTests {

	private static GemFireCluster gemFireCluster;

	@BeforeClass
	public static void startGeodeServer() throws IOException {

		gemFireCluster = new GemFireCluster(System.getProperty("spring.test.gemfire.docker.image"), 1, 1)
				.withGfsh(false, "create region --name=Users --type=PARTITION");

		gemFireCluster.acceptLicense().start();

		System.setProperty("gemfire.locator.port", String.valueOf(gemFireCluster.getLocatorPort()));
	}

	@AfterClass
	public static void shutdown() {
		gemFireCluster.close();
	}

	@Autowired
	private UserService userService;

	@Test
	public void concurrentTransactionalDataAccessOperations() throws Throwable {
		TestFramework.runOnce(new TwoConcurrentThreadsTransactionalDataAccessOperationsMultithreadedTestCase(this.userService));
	}

	@ClientCacheApplication(name = "ConcurrentClientTransactionalDataAccessWithRollbackIntegrationTests")
	@EnableEntityDefinedRegions(basePackageClasses = User.class)
	@EnableGemfireRepositories(basePackageClasses = UserRepository.class)
	@EnableGemfireCacheTransactions
	@EnablePdx
	static class TestGeodeClientConfiguration {

		@Bean
		GemfireRepositoryFactoryBean<UserRepository, User, Integer> userRepository(ClientCache gemfireCache) {

			GemfireRepositoryFactoryBean<UserRepository, User, Integer> userRepositoryFactoryBean =
				new GemfireRepositoryFactoryBean<>(UserRepository.class);

			userRepositoryFactoryBean.setCache(gemfireCache);
			userRepositoryFactoryBean.setGemfireMappingContext(new GemfireMappingContext());

			return userRepositoryFactoryBean;
		}

		@Bean
		UserService userService(UserRepository userRepository) {
			return new UserService(userRepository);
		}

		@Bean
		ClientCacheConfigurer clientCacheConfigurer() {
			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
					Collections.singletonList(
							new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}
	}

	public static final class TwoConcurrentThreadsTransactionalDataAccessOperationsMultithreadedTestCase
			extends MultithreadedTestCase {

		@Getter
		private final UserService userService;

		public TwoConcurrentThreadsTransactionalDataAccessOperationsMultithreadedTestCase(
				@NonNull UserService userService) {

			Assert.notNull(userService, "UserService must not be null");

			this.userService = userService;
			this.userService.setTestCase(this);
		}

		private void assertUser(@NonNull User user, Integer expectedId, String expectedName) {

			assertThat(user).isNotNull();
			assertThat(user.getId()).isEqualTo(expectedId);
			assertThat(user.getName()).isEqualTo(expectedName);
		}

		@Override
		public void initialize() {

			super.initialize();

			User jonDoe = User.as("jonDoe").identifiedBy(1);

			getUserService().saveAndCommit(jonDoe);

			assertThat(getUserService().exists(jonDoe)).isTrue();
		}

		public void thread1() {

			Thread.currentThread().setName("Data Access Thread One Running Rollback");

			assertTick(0);

			User jonDoe = getUserService().findById(1);

			assertUser(jonDoe, 1, "jonDoe");

			jonDoe.withName("sourDoe");

			assertUser(jonDoe, 1, "sourDoe");

			AtomicBoolean optimisticLockingFailureExceptionIsPresent = new AtomicBoolean(false);

			try {
				getUserService().saveThrowRuntimeExceptionAndRollback(jonDoe);
			}
			catch (Throwable expected) {

				Throwable cause = expected;

				while (cause != null) {
					if (cause instanceof OptimisticLockingFailureException) {
						assertThat(cause).hasMessage("TEST");
						optimisticLockingFailureExceptionIsPresent.set(true);
					}
					else if (cause instanceof NoTransactionException) {
						fail(String.format("%s was incorrectly thrown", cause.getClass().getName()));
					}

					cause = cause.getCause();
				}
			}
			finally {
				assertThat(optimisticLockingFailureExceptionIsPresent).isTrue();
			}
		}

		public void thread2() {

			Thread.currentThread().setName("Data Access Thread Two Running Commit");

			waitForTick(1);
			assertTick(1);

			User jonDoe = getUserService().findById(1);

			assertUser(jonDoe, 1, "jonDoe");

			jonDoe.withName("pieDoe");

			assertUser(jonDoe, 1, "pieDoe");

			getUserService().saveAndCommit(jonDoe);
		}

		@Override
		public void finish() {

			super.finish();

			User pieDoe = getUserService().findById(1);

			assertUser(pieDoe, 1, "pieDoe");
		}
	}

	@Service
	static class UserService {

		@Setter
		private MultithreadedTestCase testCase;

		@Getter
		private final UserRepository userRepository;

		public UserService(@NonNull UserRepository userRepository) {
			Assert.notNull(userRepository, "UserRepository must not be null");
			this.userRepository = userRepository;
		}

		protected MultithreadedTestCase getTestCase() {

			Assert.state(this.testCase != null,
				"A reference to the TestCase was not configured correctly");

			return this.testCase;
		}

		@Transactional(readOnly = true)
		public boolean exists(@NonNull User user) {

			return user != null
				&& user.getId() != null
				&& getUserRepository().existsById(user.getId());
		}

		@Transactional(readOnly = true)
		public @NonNull User findById(@NonNull Integer id) {

			return Optional.ofNullable(id)
				.flatMap(getUserRepository()::findById)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		}

		@Transactional
		public @NonNull User saveAndCommit(@NonNull User user) {
			return save(user);
		}

		@Transactional
		public @NonNull User saveThrowRuntimeExceptionAndRollback(@NonNull User user) {
			save(user);
			getTestCase().waitForTick(2);
			throw new OptimisticLockingFailureException("TEST");
		}

		private @NonNull User save(@NonNull User user) {
			return getUserRepository().save(user);
		}
	}
}
