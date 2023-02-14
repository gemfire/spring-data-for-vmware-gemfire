/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import java.util.List;

import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

/**
 * The CustomerRepository class is a Data Access Object (DAO) for accessing and performing persistent operations on
 * Customer objects.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.repository.sample.Customer
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @since 1.0.0
 */
@SuppressWarnings("unused")
public interface CustomerRepository extends GemfireRepository<Customer, Long> {

	@Query("SELECT DISTINCT c FROM /Customers c, /Accounts a WHERE c.id = a.customerId")
	List<Customer> findCustomersWithAccounts();

}
