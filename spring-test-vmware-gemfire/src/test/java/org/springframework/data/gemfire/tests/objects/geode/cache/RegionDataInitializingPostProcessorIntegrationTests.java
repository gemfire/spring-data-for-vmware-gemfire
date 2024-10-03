/*
 * Copyright 2017-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.tests.objects.geode.cache;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.DataPolicy;
import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Integration Tests for {@link RegionDataInitializingPostProcessor}.
 *
 * @author John Blum
 * @see Test
 * @see org.apache.geode.cache.Region
 * @see ClientCacheApplication
 * @see IntegrationTestsSupport
 * @see GemFireUnitTest
 * @see org.springframework.test.context.ContextConfiguration
 * @see SpringRunner
 * @since 0.0.26
 */
@RunWith(SpringRunner.class)
@GemFireUnitTest
@SuppressWarnings("unused")
public class RegionDataInitializingPostProcessorIntegrationTests extends IntegrationTestsSupport {

	@Autowired
	@Qualifier("Users")
	private org.apache.geode.cache.Region<String, User> users;

	@Test
	public void assertUsersRegionMetadata() {

		assertThat(this.users).isNotNull();
		assertThat(this.users.getName()).isEqualTo("Users");
		assertThat(this.users.getAttributes()).isNotNull();
		assertThat(this.users.getAttributes().getDataPolicy()).isEqualTo(DataPolicy.NORMAL);
	}

	@Test
	public void assetUsersRegionData() {

		assertThat(this.users).hasSize(2);
		assertThat(this.users).containsKeys("jonDoe", "janeDoe");
		assertThat(this.users).containsValues(User.with("jonDoe"), User.with("janeDoe"));
	}

	@ClientCacheApplication
	@EnableEntityDefinedRegions(basePackageClasses = User.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	static class TestConfiguration {

		@Bean
		RegionDataInitializingPostProcessor<User> usersRegionDataInitializer() {

			return RegionDataInitializingPostProcessor.<User>withRegion("Users")
				.useAsEntityIdentifier(User::getName)
				.store(User.with("jonDoe"))
				.store(User.with("janeDoe"));
		}
	}

	@Getter
	@Region("Users")
	@ToString(of = "name")
	@EqualsAndHashCode(of = "name")
	@RequiredArgsConstructor(staticName = "with")
	static class User {
		@lombok.NonNull
		private final String name;
	}
}
