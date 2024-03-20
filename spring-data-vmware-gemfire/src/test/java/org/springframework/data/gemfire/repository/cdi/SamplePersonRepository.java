/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.cdi;

import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.sample.Person;

/**
 * The SamplePersonRepository class is a {@link GemfireRepository} implementation for performing data access (CRUD)
 * operations on instances of {@link Person}.
 *
 * @author John Blum
 * @see GemfireRepository
 * @see CustomPersonRepository
 * @see CustomPersonRepositoryImpl
 * @see Person
 * @since 1.8.0
 */
@Region("People")
public interface SamplePersonRepository extends GemfireRepository<Person, Long>, CustomPersonRepository {

}
