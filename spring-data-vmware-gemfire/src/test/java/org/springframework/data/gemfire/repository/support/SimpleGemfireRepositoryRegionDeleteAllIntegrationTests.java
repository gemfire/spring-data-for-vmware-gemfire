/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.gemfire.LocalRegionFactoryBean;
import org.springframework.data.gemfire.PartitionedRegionFactoryBean;
import org.springframework.data.gemfire.ReplicatedRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.CacheServerApplication;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.config.annotation.EnablePdx;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.tests.integration.ForkingClientServerIntegrationTestsSupport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.model.User;
import example.app.repo.UserRepository;

/**
 * Integration Tests asserting the correct function of the {@link CrudRepository#deleteAll()} method
 * as implemented by the {@link SimpleGemfireRepository} class in an Apache Geode client/server topology.
 *
 * @author John Blum
 * @see Test
 * @see GemFireCache
 * @see Region
 * @see CacheServerApplication
 * @see ClientCacheApplication
 * @see EnableGemfireRepositories
 * @see SimpleGemfireRepository
 * @see ForkingClientServerIntegrationTestsSupport
 * @see CrudRepository
 * @see ContextConfiguration
 * @see SpringRunner
 * @link <a herf="https://github.com/spring-projects/spring-data-geode/issues/512">CrudRepository.deleteAll not working</a>
 * @since 2.6.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SimpleGemfireRepositoryRegionDeleteAllIntegrationTests.GeodeClientTestConfiguration.class)
@SuppressWarnings("unused")
public class SimpleGemfireRepositoryRegionDeleteAllIntegrationTests extends ForkingClientServerIntegrationTestsSupport {

	@BeforeClass
	public static void startGeodeServer() throws Exception {
		startGemFireServer(GeodeServerTestConfiguration.class, "-Dspring.profiles.active=partition");
	}

	@Autowired
	@Qualifier("Users")
	private Region<Integer, User> users;

	@Autowired
	private UserRepository userRepository;

	@Before
	public void assertRegionConfiguration() {

		assertThat(this.users).isNotNull();
		assertThat(this.users.getName()).isEqualTo("Users");
		assertThat(this.users.getAttributes()).isNotNull();
		assertThat(this.users.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.EMPTY);
		assertThat(this.users.getAttributes().getPoolName()).isNotEmpty();
		assertThat(this.users.keySet()).isEmpty();
		assertThat(this.userRepository).isNotNull();
	}

	@Before
	public void storeUsers() {

		User jonDoe = User.as("jonDoe").identifiedBy(1);
		User janeDoe = User.as("janeDoe").identifiedBy(2);

		this.userRepository.saveAll(Arrays.asList(jonDoe, janeDoe));

		assertThat(this.users.keySet()).isEmpty();
		assertThat(this.users.keySetOnServer()).containsExactlyInAnyOrder(jonDoe.getId(), janeDoe.getId());
	}

	@Test
	public void deleteAllIsSuccessful() {

		assertThat(this.userRepository.count()).isEqualTo(2);

		this.userRepository.deleteAll();

		assertThat(this.userRepository.count()).isZero();
		assertThat(this.users.keySet()).isEmpty();
		assertThat(this.users.keySetOnServer()).isEmpty();
	}

	@ClientCacheApplication
	@EnablePdx
	@EnableEntityDefinedRegions(basePackageClasses = User.class)
	@EnableGemfireRepositories(basePackageClasses = UserRepository.class)
	static class GeodeClientTestConfiguration { }

	@CacheServerApplication
	static class GeodeServerTestConfiguration {

		public static void main(String[] args) {
			runSpringApplication(GeodeServerTestConfiguration.class, args);
		}

		@Bean("Users")
		@Profile("local")
		public LocalRegionFactoryBean<Object, Object> localRegion(GemFireCache gemfireCache) {

			LocalRegionFactoryBean<Object, Object> users = new LocalRegionFactoryBean<>();

			users.setCache(gemfireCache);
			users.setPersistent(false);

			return users;
		}

		@Bean("Users")
		@Profile("partition")
		public PartitionedRegionFactoryBean<Object, Object> partitionRegion(GemFireCache gemfireCache) {

			PartitionedRegionFactoryBean<Object, Object> users = new PartitionedRegionFactoryBean<>();

			users.setCache(gemfireCache);
			users.setPersistent(false);

			return users;
		}

		@Bean("Users")
		@Profile("replicate")
		public ReplicatedRegionFactoryBean<Object, Object> replicateRegion(GemFireCache gemfireCache) {

			ReplicatedRegionFactoryBean<Object, Object> users = new ReplicatedRegionFactoryBean<>();

			users.setCache(gemfireCache);
			users.setClose(false);
			users.setPersistent(false);

			return users;
		}
	}
}
