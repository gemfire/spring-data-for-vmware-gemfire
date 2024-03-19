/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import java.util.List;

import org.springframework.data.gemfire.repository.GemfireRepository;

/**
 * The GuestUserRepository class is a DAO for accessing and persisting GuestUsers.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.sample.GuestUser
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public interface GuestUserRepository extends GemfireRepository<GuestUser, String> {

	public List<GuestUser> findDistinctByUsername(String username);

}
