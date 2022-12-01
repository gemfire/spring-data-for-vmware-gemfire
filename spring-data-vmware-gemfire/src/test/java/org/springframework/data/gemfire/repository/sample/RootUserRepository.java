/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import java.util.List;

import org.springframework.data.gemfire.repository.GemfireRepository;

/**
 * The RootUserRepository class is a DAO for accessing and persisting RootUsers.
 *
 * @author John Blum
 * @see GemfireRepository
 * @see RootUser
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public interface RootUserRepository extends GemfireRepository<RootUser, String> {

	public List<RootUser> findDistinctByUsername(String username);

}
