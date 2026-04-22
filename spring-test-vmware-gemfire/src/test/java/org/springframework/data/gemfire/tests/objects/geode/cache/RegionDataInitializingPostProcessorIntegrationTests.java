/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-04-01: Replaced Apache Geode DataPolicy, ClientRegionShortcut, and Region with GUD equivalents
 */

package org.springframework.data.gemfire.tests.objects.geode.cache;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.gud.api.GudClientRegionShortcut;
import org.springframework.data.gemfire.gud.api.GudDataPolicy;
import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.tests.unit.annotation.GemFireUnitTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration Tests for {@link RegionDataInitializingPostProcessor}.
 *
 * @author John Blum
 * @see Test
 * @see org.springframework.data.gemfire.gud.api.GudRegion
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
	private GudRegion<String, User> users;

	@Test
	public void assertUsersRegionMetadata() {

		assertThat(this.users).isNotNull();
		assertThat(this.users.getName()).isEqualTo("Users");
		assertThat(this.users.getAttributes()).isNotNull();
		assertThat(this.users.getAttributes().getDataPolicy()).isEqualTo(GudDataPolicy.NORMAL);
	}

	@Test
	public void assetUsersRegionData() {

		assertThat(this.users).hasSize(2);
		assertThat(this.users).containsKeys("jonDoe", "janeDoe");
		assertThat(this.users).containsValues(User.with("jonDoe"), User.with("janeDoe"));
	}

	@ClientCacheApplication
	@EnableEntityDefinedRegions(basePackageClasses = User.class, clientRegionShortcut = GudClientRegionShortcut.LOCAL)
	static class TestConfiguration {

		@Bean
		RegionDataInitializingPostProcessor<User> usersRegionDataInitializer() {

			return RegionDataInitializingPostProcessor.<User>withGudRegion("Users")
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
