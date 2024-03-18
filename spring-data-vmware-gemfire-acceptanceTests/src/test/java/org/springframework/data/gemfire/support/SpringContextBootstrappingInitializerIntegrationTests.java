/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.support;

import java.time.Instant;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import org.apache.geode.cache.Cache;
import org.apache.geode.cache.CacheFactory;
import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.config.xml.GemfireConstants;
import org.springframework.data.gemfire.repository.sample.User;
import org.springframework.data.gemfire.support.sample.TestUserDao;
import org.springframework.data.gemfire.support.sample.TestUserService;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.support.DataSourceAdapter;
import org.springframework.data.gemfire.util.SpringExtensions;
import org.springframework.util.Assert;

/**
 * Integration Tests for the {@link SpringContextBootstrappingInitializer}.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Cache
 * @see org.apache.geode.cache.CacheFactory
 * @see org.apache.geode.cache.Region
 * @see org.springframework.context.ConfigurableApplicationContext
 * @see org.springframework.context.annotation.Bean
 * @see org.springframework.context.annotation.Configuration
 * @see org.springframework.data.gemfire.support.SpringContextBootstrappingInitializer
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class SpringContextBootstrappingInitializerIntegrationTests extends IntegrationTestsSupport {

	protected static final String GEMFIRE_LOG_LEVEL = "error";
	protected static final String GEMFIRE_JMX_MANAGER = "true";
	protected static final String GEMFIRE_JMX_MANAGER_PORT = "1199";
	protected static final String GEMFIRE_JMX_MANAGER_START = "true";
	protected static final String GEMFIRE_NAME = SpringContextBootstrappingInitializerIntegrationTests.class.getSimpleName();
	protected static final String GEMFIRE_START_LOCATORS = "localhost[11235]";

	@AfterClass
	public static void cleanupAfterTests() {
		SpringContextBootstrappingInitializer.destroy();
	}

	@Before @After
	public void testSetupAndTearDown() {

		SpringExtensions.safeDoOperation(() ->
			IntegrationTestsSupport.closeApplicationContext(SpringContextBootstrappingInitializer.getApplicationContext()));

		UserDataStoreCacheLoader.INSTANCE.set(null);

		IntegrationTestsSupport.closeAnyGemFireCache();
	}

	@SuppressWarnings("all")
	private void doSpringContextBootstrappingInitializationTest(String cacheXmlFile) {

		Cache gemfireCache = new CacheFactory()
			.set("name", GEMFIRE_NAME)
			.set("log-level", GEMFIRE_LOG_LEVEL)
			.set("cache-xml-file", cacheXmlFile)
			//.set("start-locator", GEMFIRE_START_LOCATORS)
			//.set("jmx-manager", GEMFIRE_JMX_MANAGER)
			//.set("jmx-manager-port", GEMFIRE_JMX_MANAGER_PORT)
			//.set("jmx-manager-start", GEMFIRE_JMX_MANAGER_START)
			.create();

		Assertions.assertThat(gemfireCache)
			.describedAs("GemFireCache was not properly created and initialized")
			.isNotNull();

		Assertions.assertThat(gemfireCache.isClosed())
			.describedAs("GemFireCache is closed")
			.isFalse();

		Set<Region<?, ?>> rootRegions = gemfireCache.rootRegions();

		Assertions.assertThat(rootRegions).isNotNull();
		Assertions.assertThat(rootRegions.isEmpty()).isFalse();
		Assertions.assertThat(rootRegions.size()).isEqualTo(2);
		Assertions.assertThat(gemfireCache.getRegion("/TestRegion")).isNotNull();
		Assertions.assertThat(gemfireCache.getRegion("/Users")).isNotNull();

		ConfigurableApplicationContext applicationContext =
			SpringContextBootstrappingInitializer.getApplicationContext();

		Assertions.assertThat(applicationContext).isNotNull();
		Assertions.assertThat(applicationContext.containsBean(GemfireConstants.DEFAULT_GEMFIRE_CACHE_NAME)).isTrue();
		Assertions.assertThat(applicationContext.containsBean("TestRegion")).isTrue();
		Assertions.assertThat(applicationContext.containsBean("Users")).isFalse(); // Region 'Users' is defined in Pivotal GemFire cache.xml
		Assertions.assertThat(applicationContext.containsBean("userDataSource")).isTrue();
		Assertions.assertThat(applicationContext.containsBean("userDao")).isTrue();
		Assertions.assertThat(applicationContext.containsBean("userService")).isTrue();

		DataSource userDataSource = applicationContext.getBean("userDataSource", DataSource.class);
		TestUserDao userDao = applicationContext.getBean("userDao", TestUserDao.class);
		TestUserService userService = applicationContext.getBean("userService", TestUserService.class);

		Assertions.assertThat(userDao.getDataSource()).isSameAs(userDataSource);
		Assertions.assertThat(userService.getUserDao()).isSameAs(userDao);

		// NOTE Pivotal GemFire declared component initialized by Spring!
		UserDataStoreCacheLoader usersCacheLoader = UserDataStoreCacheLoader.getInstance();

		Assertions.assertThat(usersCacheLoader.getDataSource()).isSameAs(userDataSource);

		Region<String, User> users = gemfireCache.getRegion("/Users");

		Assertions.assertThat(users).isNotNull();
		Assertions.assertThat(users.getName()).isEqualTo("Users");
		Assertions.assertThat(users.getFullPath()).isEqualTo("/Users");
		Assertions.assertThat(users.isEmpty()).isTrue();
		Assertions.assertThat(users.get("jblum")).isEqualTo(UserDataStoreCacheLoader.USER_DATA.get("jblum"));
		Assertions.assertThat(users.get("jdoe")).isEqualTo(UserDataStoreCacheLoader.USER_DATA.get("jdoe"));
		Assertions.assertThat(users.get("jhandy")).isEqualTo(UserDataStoreCacheLoader.USER_DATA.get("jhandy"));
		Assertions.assertThat(users.isEmpty()).isFalse();
		Assertions.assertThat(users.size()).isEqualTo(3);
	}

	@Test
	public void springContextBootstrappingInitializerUsingAnnotatedClassesIsCorrect() {

		SpringContextBootstrappingInitializer.register(TestAppConfig.class);

		new SpringContextBootstrappingInitializer().init(null, new Properties());

		ConfigurableApplicationContext applicationContext = SpringContextBootstrappingInitializer.getApplicationContext();

		UserDataStoreCacheLoader userDataStoreCacheLoader = applicationContext.getBean(UserDataStoreCacheLoader.class);
		DataSource userDataSource = applicationContext.getBean(DataSource.class);

		Assertions.assertThat(userDataStoreCacheLoader).isSameAs(UserDataStoreCacheLoader.getInstance());
		Assertions.assertThat(userDataSource).isSameAs(userDataStoreCacheLoader.getDataSource());
	}

	@Test
	public void springContextBootstrappingInitializerUsingXmlWithBasePackages() {
		doSpringContextBootstrappingInitializationTest(
			"cache-with-spring-context-bootstrap-initializer-using-base-packages.xml");
	}

	@Test
	public void springContextBootstrappingInitializerUsingXmlWithContextConfigLocations() {
		doSpringContextBootstrappingInitializationTest(
			"cache-with-spring-context-bootstrap-initializer.xml");
	}

	@Configuration
	public static class TestAppConfig {

		@Bean
		public DataSource userDataSource() {
			return new TestDataSource();
		}

		@Bean
		public UserDataStoreCacheLoader userDataStoreCacheLoader() {
			return new UserDataStoreCacheLoader();
		}
	}

	public static final class TestDataSource extends DataSourceAdapter { }

	public static final class UserDataStoreCacheLoader extends LazyWiringDeclarableSupport
			implements CacheLoader<String, User> {

		private static final AtomicReference<UserDataStoreCacheLoader> INSTANCE = new AtomicReference<>();

		private static final Map<String, User> USER_DATA = new ConcurrentHashMap<>(3);

		static {
			USER_DATA.put("jblum", new User("jblum"));
			USER_DATA.put("jdoe", new User("jdoe"));
			USER_DATA.put("jhandy", new User("jhandy"));
		}

		@Autowired
		private DataSource userDataSource;

		static User createUser(String username) {
			return createUser(username, true, Instant.now(), String.format("%1$s@xcompay.com", username));
		}

		static User createUser(String username, Boolean active) {
			return createUser(username, active, Instant.now(), String.format("%1$s@xcompay.com", username));
		}

		static User createUser(String username, Boolean active, Instant since) {
			return createUser(username, active, since, String.format("%1$s@xcompay.com", username));
		}

		static User createUser(String username, Boolean active, Instant since, String email) {

			User user = new User(username);

			user.setActive(active);
			user.setEmail(email);
			user.setSince(since);

			return user;
		}

		public static UserDataStoreCacheLoader getInstance() {
			return INSTANCE.get();
		}

		public UserDataStoreCacheLoader() {
			Assert.state(INSTANCE.compareAndSet(null, this),
				String.format("An instance of %1$s was already created", getClass().getName()));
		}

		@Override
		protected void assertInitialized() {

			super.assertInitialized();

			Assert.state(this.userDataSource != null,
				String.format("The 'User' Data Source was not properly configured and initialized for use in (%s)",
					getClass().getName()));
		}

		DataSource getDataSource() {
			return this.userDataSource;
		}

		@Override
		public void close() {
			this.userDataSource = null;
		}

		@Override
		public void destroy() throws Exception {
			super.destroy();
			INSTANCE.set(null);
		}

		@Override
		public User load(LoaderHelper<String, User> helper) throws CacheLoaderException {

			assertInitialized();

			return USER_DATA.get(helper.getKey());
		}
	}
}
