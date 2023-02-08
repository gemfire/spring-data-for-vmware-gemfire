/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.gemfire.repository.GemfireRepository;

/**
 * The AccountsRepository class is a Data Access Object (DAO) for accessing and performing persistent operations on
 * Account objects.
 *
 * @author John Blum
 * @see Account
 * @see GemfireRepository
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface AccountRepository extends GemfireRepository<Account, Long> {

}
