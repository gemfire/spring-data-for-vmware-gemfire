/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.test.repo;

import org.springframework.data.gemfire.test.model.Person;
import org.springframework.data.repository.CrudRepository;

/**
 * The {@link PersonRepository} interface defines a Spring Data {@link CrudRepository} used by applications
 * to perform basic CRUD and querying data access operations on {@link Person people}.
 *
 * @author John Blum
 * @see java.lang.Long
 * @see org.springframework.data.gemfire.test.model.Person
 * @see org.springframework.data.repository.CrudRepository
 * @since 2.0.0
 */
public interface PersonRepository extends CrudRepository<Person, Long> {

}
