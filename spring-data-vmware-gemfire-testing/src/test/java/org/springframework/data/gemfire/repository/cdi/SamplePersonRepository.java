/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
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
 * @see org.springframework.data.gemfire.repository.GemfireRepository
 * @see org.springframework.data.gemfire.repository.cdi.CustomPersonRepository
 * @see org.springframework.data.gemfire.repository.cdi.CustomPersonRepositoryImpl
 * @see org.springframework.data.gemfire.repository.sample.Person
 * @since 1.8.0
 */
@Region("People")
public interface SamplePersonRepository extends GemfireRepository<Person, Long>, CustomPersonRepository {

}
