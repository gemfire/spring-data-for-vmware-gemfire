/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package example.app.repo;

import java.util.List;
import java.util.Set;

import org.springframework.data.gemfire.repository.GemfireRepository;

import example.app.model.User;

/**
 * Spring Data [Geode] {@link GemfireRepository} used to perform basic CRUD and simple OQL query data access operations
 * on {@link User} objects to and from an Apache Geode {@link org.apache.geode.cache.GemFireCache}
 * {@link org.apache.geode.cache.Region}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.GemFireCache
 * @see org.apache.geode.cache.Region
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see example.app.model.User
 * @since 2.5.0
 */
@SuppressWarnings("unused")
public interface UserRepository extends GemfireRepository<User, Integer> {

	List<User> findByIdInOrderById(Integer... identifiers);

	//@Query("SELECT u FROM /Users u WHERE u.id IN ($1) ORDER BY u.id")
	List<User> findByIdInOrderById(Set<Integer> identifiers);

	List<User> findByNameInOrderByName(String... usersnames);

	List<User> findByNameInOrderByName(Set<String> usernames);

}
