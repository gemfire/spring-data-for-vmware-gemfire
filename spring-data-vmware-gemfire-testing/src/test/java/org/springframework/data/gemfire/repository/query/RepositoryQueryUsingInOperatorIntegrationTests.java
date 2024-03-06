/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.apache.geode.cache.client.ClientRegionShortcut;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.gemfire.config.annotation.ClientCacheApplication;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport;
import org.springframework.data.gemfire.util.ArrayUtils;
import org.springframework.data.gemfire.util.CollectionUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import example.app.model.User;
import example.app.repo.UserRepository;

/**
 * Integration Tests testing and asserting the use of the {@literal IN} operator in an OQL query
 *
 * @author John Blum
 * @see org.junit.Test
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.config.EnableGemfireRepositories
 * @see org.springframework.data.gemfire.tests.integration.IntegrationTestsSupport
 * @see org.springframework.test.context.ContextConfiguration
 * @see org.springframework.test.context.junit4.SpringRunner
 * @see <a href="https://github.com/spring-projects/spring-data-geode/issues/483">Fix bug with SDG Repository derived queries using the IN operator with numeric values.</a>
 * @since 2.5.0
 */
@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("unused")
public class RepositoryQueryUsingInOperatorIntegrationTests extends IntegrationTestsSupport {

	private static final AtomicBoolean initialized = new AtomicBoolean(false);

	private final List<User> users = Arrays.asList(
		User.as("jonDoe").identifiedBy(1),
		User.as("janeDoe").identifiedBy(2),
		User.as("cookieDoe").identifiedBy(3),
		User.as("pieDoe").identifiedBy(4),
		User.as("sourDoe").identifiedBy(5)
	);

	@Autowired
	private UserRepository userRepository;

	private static boolean doOnce() {
		return initialized.compareAndSet(false, true);
	}

	@Before
	public void setup() {

		assertThat(this.userRepository).isNotNull();

		if (doOnce()) {
			this.userRepository.saveAll(this.users);
		}

		assertThat(this.userRepository.count()).isEqualTo(this.users.size());
	}

	private User[] findUsersByIds(Integer... ids) {

		return this.users.stream()
			.filter(user -> Arrays.asList(ArrayUtils.nullSafeArray(ids, Integer.class)).contains(user.getId()))
			.sorted(Comparator.comparing(User::getId))
			.toArray(User[]::new);
	}

	private User[] findUsersByNames(String... names) {

		return this.users.stream()
			.filter(user -> Arrays.asList(ArrayUtils.nullSafeArray(names, String.class)).contains(user.getName()))
			.sorted(Comparator.comparing(User::getName))
			.toArray(User[]::new);
	}

	@Test
	public void findByUserIdsInArrayIsCorrect() {

		List<User> usersById = this.userRepository.findByIdInOrderById(2, 4);

		assertThat(usersById).isNotNull();
		assertThat(usersById).hasSize(2);
		assertThat(usersById).containsExactly(findUsersByIds(2, 4));
	}

	@Test
	public void findByUserIdsInSetIsCorrect() {

		List<User> usersById = this.userRepository.findByIdInOrderById(CollectionUtils.asSet(2, 4, 3));

		assertThat(usersById).isNotNull();
		assertThat(usersById).hasSize(3);
		assertThat(usersById).containsExactlyInAnyOrder(findUsersByIds(2, 3, 4));
	}

	@Test
	public void findByUserNamesInArrayIsCorrect() {

		List<User> usersByName =
			this.userRepository.findByNameInOrderByName("pieDoe", "cookieDoe", "sourDoe");

		assertThat(usersByName).isNotNull();
		assertThat(usersByName).hasSize(3);
		assertThat(usersByName).containsExactly(findUsersByNames("cookieDoe", "pieDoe", "sourDoe"));
	}

	@Test
	public void findByUserNamesInSetIsCorrect() {

		List<User> usersByName =
			this.userRepository.findByNameInOrderByName(CollectionUtils.asSet("sourDoe", "jonDoe"));

		assertThat(usersByName).isNotNull();
		assertThat(usersByName).hasSize(2);
		assertThat(usersByName).containsExactly(findUsersByNames("jonDoe", "sourDoe"));
	}

	@ClientCacheApplication
	@EnableEntityDefinedRegions(basePackageClasses = User.class, clientRegionShortcut = ClientRegionShortcut.LOCAL)
	@EnableGemfireRepositories(basePackageClasses = UserRepository.class)
	static class TestConfiguration { }

}
