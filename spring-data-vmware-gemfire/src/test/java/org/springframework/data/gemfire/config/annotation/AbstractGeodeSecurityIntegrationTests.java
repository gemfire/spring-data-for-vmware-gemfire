/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.Serializable;
import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import org.apache.geode.cache.CacheLoader;
import org.apache.geode.cache.CacheLoaderException;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.LoaderHelper;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.apache.geode.cache.client.ServerOperationException;
import org.apache.geode.security.NotAuthorizedException;
import org.apache.geode.security.ResourcePermission;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.support.AbstractAuthInitialize;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.data.gemfire.util.PropertiesBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Abstract base test class for implementing Apache Geode Integrated Security Integration Tests.
 *
 * @author John Blum
 * @see java.security.Principal
 * @see java.util.Properties
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.support.AbstractAuthInitialize
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @since 1.0.0
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@SuppressWarnings("unused")
public abstract class AbstractGeodeSecurityIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractGeodeSecurityIntegrationTests.class);

	protected static final String CACHE_SERVER_HOST = "localhost";
	protected static final String GEODE_SECURITY_PROFILE_PROPERTY = "geode.security.profile";
	protected static final String SECURITY_PASSWORD_PROPERTY = "security-password";
	protected static final String SECURITY_USERNAME_PROPERTY = "security-username";

	private static final AtomicInteger RUN_COUNT = new AtomicInteger(0);

	@BeforeClass
	public static void runGeodeServer() throws IOException {

		String geodeSecurityProfile = System.getProperty(GEODE_SECURITY_PROFILE_PROPERTY);

		if (StringUtils.hasText(geodeSecurityProfile)) {
			runGeodeServer(geodeSecurityProfile);
		}
	}

	protected static void runGeodeServer(String geodeSecurityProfile) throws IOException {

		Assert.hasText(geodeSecurityProfile, String.format("[%s] System property is required",
			GEODE_SECURITY_PROFILE_PROPERTY));

		String debugEndpoint = Boolean.getBoolean(DEBUGGING_ENABLED_PROPERTY) ? DEBUG_ENDPOINT : null;

		startGemFireServer(GeodeServerConfiguration.class,
			String.format("-Dgemfire.log-file=%s", logFile()),
			String.format("-Dgemfire.log-level=%s", logLevel(TEST_GEMFIRE_LOG_LEVEL)),
			String.format("-Dspring.profiles.active=apache-geode-server,%s", geodeSecurityProfile),
			debugEndpoint);
	}

	@AfterClass
	public static void stopGeodeServer() {
		System.clearProperty(GEODE_SECURITY_PROFILE_PROPERTY);
	}

	@Autowired
	@Qualifier("Echo")
	private Region<String, String> echo;

	@Test
	@DirtiesContext
	public void authorizedUser() {

		assertThat(echo.get("one")).isEqualTo("one");
		assertThat(echo.put("two", "four")).isNull();
		assertThat(echo.get("two")).isEqualTo("four");
	}

	@Test(expected = NotAuthorizedException.class)
	public void unauthorizedUser() {

		try {
			assertThat(echo.get("one")).isEqualTo("one");
			echo.put("two", "four");
		}
		catch (ServerOperationException expected) {

			assertThat(expected).hasMessageContaining("analyst not authorized for DATA:WRITE:Echo:two");
			assertThat(expected).hasCauseInstanceOf(NotAuthorizedException.class);

			throw (NotAuthorizedException) expected.getCause();
		}
		finally {
			assertThat(echo).doesNotContainKey("two");
		}
	}

	public static class GeodeClientAuthInitialize extends AbstractAuthInitialize {

		protected static final User ANALYST = User.newUser("analyst").with("p@55w0rd");
		protected static final User SCIENTIST = User.newUser("scientist").with("w0rk!ng4u");

		private final User user;

		public static GeodeClientAuthInitialize create() {
			return new GeodeClientAuthInitialize(RUN_COUNT.incrementAndGet() < 2 ? SCIENTIST : ANALYST);
		}

		public GeodeClientAuthInitialize(User user) {
			Assert.notNull(user, "User cannot be null");
			this.user = user;
		}

		@Override
		protected Properties doGetCredentials(Properties securityProperties) {

			User user = getUser();

			return PropertiesBuilder.create()
				.setProperty(SECURITY_USERNAME_PROPERTY, user.getName())
				.setProperty(SECURITY_PASSWORD_PROPERTY, user.getCredentials())
				.build();
		}

		protected User getUser() {
			return this.user;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString() {

			User user = getUser();

			return String.format("%1$s:%2$s", user.getName(), user.getCredentials());
		}
	}

	@ClientCacheApplication(name = "GeodeSecurityIntegrationTestsClient")
	@EnableAuth(clientAuthenticationInitializer =
		"org.springframework.data.gemfire.config.annotation.AbstractGeodeSecurityIntegrationTests$GeodeClientAuthInitialize.create")
	@Profile("apache-geode-client")
	static class GeodeClientConfiguration {

		@Bean("Echo")
		ClientRegionFactoryBean<String, String> echoRegion(GemFireCache gemfireCache) {

			ClientRegionFactoryBean<String, String> echoRegion = new ClientRegionFactoryBean<>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setShortcut(ClientRegionShortcut.PROXY);

			return echoRegion;
		}
	}

	@CacheServerApplication(name = "GeodeSecurityIntegrationTestsServer")
	@Import({
		ApacheGeodeSecurityManagerSecurityIntegrationTests.ApacheGeodeSecurityManagerConfiguration.class,
		ApacheShiroRealmSecurityIntegrationTests.ApacheShiroRealmConfiguration.class,
		ApacheShiroIniSecurityIntegrationTests.ApacheShiroIniConfiguration.class
	})
	@Profile("apache-geode-server")
	public static class GeodeServerConfiguration {

		public static void main(String[] args) {
			runSpringApplication(GeodeServerConfiguration.class, args);
		}

		@Autowired
		private GemFireCache gemfireCache;

		@Bean("Echo")
		LocalRegionFactoryBean<String, String> echoRegion(GemFireCache gemfireCache) {

			LocalRegionFactoryBean<String, String> echoRegion = new LocalRegionFactoryBean<>();

			echoRegion.setCache(gemfireCache);
			echoRegion.setCacheLoader(echoCacheLoader());
			echoRegion.setPersistent(false);

			return echoRegion;
		}

		CacheLoader<String, String> echoCacheLoader() {

			return new CacheLoader<String, String>() {

				@Override
				public String load(LoaderHelper<String, String> helper) throws CacheLoaderException {
					return helper.getKey();
				}

				@Override
				public void close() { }

			};
		}

		@PostConstruct
		public void postProcess() {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Geode Distributed System Properties [{}]",
					CollectionUtils.toString(gemfireCache.getDistributedSystem().getProperties()));
			}
		}
	}

	@Getter
	@EqualsAndHashCode(of = { "name", "credentials" })
	@RequiredArgsConstructor(staticName = "newUser")
	public static class User implements Iterable<Role>, Principal, Serializable {

		private final Set<Role> roles = new HashSet<>();

		@lombok.NonNull
		private final String name;

		@Setter(AccessLevel.PROTECTED)
		private String credentials;

		public boolean hasPermission(ResourcePermission permission) {

			for (Role role : this) {
				if (role.hasPermission(permission)) {
					return true;
				}
			}

			return false;
		}

		public boolean hasRole(Role role) {
			return this.roles.contains(role);
		}

		@Override
		public Iterator<Role> iterator() {
			return Collections.unmodifiableSet(getRoles()).iterator();
		}

		@Override
		public String toString() {
			return getName();
		}

		public User with(String credentials) {
			setCredentials(credentials);
			return this;
		}

		public User with(Role... roles) {
			Collections.addAll(getRoles(), roles);
			return this;
		}
	}

	@Getter
	@EqualsAndHashCode(of = "name")
	@RequiredArgsConstructor(staticName = "newRole")
	@SuppressWarnings("unsed")
	public static class Role implements Iterable<ResourcePermission>, Serializable {

		@NonNull
		private final String name;

		private final Set<ResourcePermission> permissions = new HashSet<>();

		public boolean hasPermission(ResourcePermission permission) {

			for (ResourcePermission thisPermission : this) {
				if (thisPermission.implies(permission)) {
					return true;
				}
			}

			return false;
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public Iterator<ResourcePermission> iterator() {
			return Collections.unmodifiableSet(this.permissions).iterator();
		}

		/**
		 * @inheritDoc
		 */
		@Override
		public String toString() {
			return getName();
		}

		public Role with(ResourcePermission... permissions) {

			Collections.addAll(this.permissions, permissions);

			return this;
		}
	}
}
