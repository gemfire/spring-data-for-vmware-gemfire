/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.sample;

import java.util.List;

import org.springframework.data.gemfire.repository.GemfireRepository;

/**
 * The ProgrammerRepository class is a Data Access Object (DAO) for Programmer domain objects supporting basic CRUD
 * and Query operations.
 *
 * @author John Blum
 * @see GemfireRepository
 * @see Programmer
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public interface ProgrammerRepository extends GemfireRepository<Programmer, String> {

	public List<Programmer> findDistinctByProgrammingLanguageOrderByUsernameAsc(String programmingLanguage);

	public List<Programmer> findDistinctByProgrammingLanguageLikeOrderByUsernameAsc(String programmingLanguage);

}
