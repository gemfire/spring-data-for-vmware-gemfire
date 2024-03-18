/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.support;

import example.app.model.User;
import example.app.repo.UserRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.Region;
import org.assertj.core.api.Assertions;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheConfigurer;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.support.ConnectionEndpoint;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.vmware.gemfire.testcontainers.GemFireCluster;

/**
 * Integration Tests asserting the correct function of the {@link CrudRepository#deleteAll()} method
 * as implemented by the {@link SimpleGemfireRepository} class in an Apache Geode client/server topology.
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.config.annotation.CacheServerApplication
 * @see org.springframework.data.gemfire.config.annotation.ClientCacheApplication
 * @see org.springframework.data.gemfire.repository.config.EnableGemfireRepositories
 * @see org.springframework.data.gemfire.repository.support.SimpleGemfireRepository
 * @see org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport
 * @see org.springframework.data.repository.CrudRepository
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @link <a herf="https://github.com/spring-projects/spring-data-geode/issues/512">CrudRepository.deleteAll not working</a>
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SimpleGemfireRepositoryRegionDeleteAllIntegrationTests.GeodeClientTestConfiguration.class)
@SuppressWarnings("unused")
public class SimpleGemfireRepositoryRegionDeleteAllIntegrationTests {

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
	@Qualifier("Users")
	private Region<Integer, User> users;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void assertRegionConfiguration() {

		Assertions.assertThat(this.users).isNotNull();
		Assertions.assertThat(this.users.getName()).isEqualTo("Users");
		Assertions.assertThat(this.users.getAttributes()).isNotNull();
		Assertions.assertThat(this.users.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
		Assertions.assertThat(this.users.getAttributes().getPoolName()).isNotEmpty();
		Assertions.assertThat(this.users.keySet()).isEmpty();
		Assertions.assertThat(this.userRepository).isNotNull();
	}

	@Before
	public void storeUsers() {

		User jonDoe = User.as("jonDoe").identifiedBy(1);
		User janeDoe = User.as("janeDoe").identifiedBy(2);

		this.userRepository.saveAll(Arrays.asList(jonDoe, janeDoe));

		Assertions.assertThat(this.users.keySet()).isEmpty();
		Assertions.assertThat(this.users.keySetOnServer()).containsExactlyInAnyOrder(jonDoe.getId(), janeDoe.getId());
	}

	@Test
	public void deleteAllIsSuccessful() {

		Assertions.assertThat(this.userRepository.count()).isEqualTo(2);

		this.userRepository.deleteAll();

		Assertions.assertThat(this.userRepository.count()).isZero();
		Assertions.assertThat(this.users.keySet()).isEmpty();
		Assertions.assertThat(this.users.keySetOnServer()).isEmpty();
	}

	@ClientCacheApplication
	@EnablePdx
	@EnableEntityDefinedRegions(basePackageClasses = User.class)
	@EnableGemfireRepositories(basePackageClasses = UserRepository.class)
	static class GeodeClientTestConfiguration {
		@Bean
		ClientCacheConfigurer clientCacheConfigurer() {
			return (bean, clientCacheFactoryBean) -> clientCacheFactoryBean.setLocators(
					Collections.singletonList(
							new ConnectionEndpoint("localhost", gemFireCluster.getLocatorPort())));
		}
	}
}
