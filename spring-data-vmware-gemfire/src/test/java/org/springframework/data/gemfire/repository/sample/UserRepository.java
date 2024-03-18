/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import java.util.List;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

/**
 * The UserRepository class is a DAO for accessing and persisting Users.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.Query
 * @see org.springframework.data.gemfire.repository.sample.User
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public interface UserRepository extends GemfireRepository<User, String> {

	@Query("SELECT count(*) FROM /Users u WHERE u.username LIKE $1")
	Integer countUsersByUsernameLike(String username);

	//@Query("SELECT DISTINCT * FROM /Users u WHERE u.active = true")
	List<User> findDistinctByActiveTrue();

	//@Query("SELECT DISTINCT * FROM /Users u WHERE u.active = false")
	List<User> findDistinctByActiveFalse();

	List<User> findDistinctByUsernameLike(String username);

/*
	//NOTE unfortunately, the 'NOT LIKE' operator is unsupported in GemFire's Query/OQL syntax
	List<User> findDistinctByUsernameNotLike(String username);
*/

}
