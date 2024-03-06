/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import java.util.List;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;
import org.springframework.data.gemfire.repository.query.annotation.Hint;
import org.springframework.data.gemfire.repository.query.annotation.Import;
import org.springframework.data.gemfire.repository.query.annotation.Limit;
import org.springframework.data.gemfire.repository.query.annotation.Trace;

/**
 * The UsingQueryAnnotationExtensionsInUserRepository class...
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.query.annotation.Hint
 * @see org.springframework.data.gemfire.repository.query.annotation.Import
 * @see org.springframework.data.gemfire.repository.query.annotation.Limit
 * @see org.springframework.data.gemfire.repository.query.annotation.Trace
 * @see org.springframework.data.gemfire.repository.sample.User
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public interface UsingQueryAnnotationExtensionsInUserRepository extends GemfireRepository<User, String> {

	@Trace
	@Limit(10)
	@Hint("IdIdx")
	@Import("org.springframework.data.gemfire.repository.sample.User")
	@Query("<HINT 'UsernameIdx'> SELECT DISTINCT * FROM /Users x WHERE x.username LIKE $1 ORDER BY x.username ASC LIMIT 1")
	List<User> findBy(String username);

	@Trace
	@Limit(5)
	@Hint("UsernameIdx")
	List<User> findDistinctByUsernameLikeOrderByUsernameAsc(String username);

}
