// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository.sample;

import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.gemfire.repository.GemfireRepository;
import org.springframework.data.gemfire.repository.Query;

/**
 * Sample Repository interface managing {@link Person}s.
 *
 * @author Oliver Gierke
 * @author David Turanski
 * @author John Blum
 */
public interface PersonRepository extends GemfireRepository<Person, Long> {

	@Query("SELECT * FROM /simple p WHERE p.firstname = $1")
	Collection<Person> findByFirstnameAnnotated(String firstname);

	@Query("SELECT * FROM /simple p WHERE p.firstname IN SET $1")
	Collection<Person> findByFirstnamesAnnotated(Collection<String> firstnames);

	Collection<Person> findByFirstname(String firstname);

	Collection<Person> findByFirstnameContaining(String firstName);

	Collection<Person> findByFirstnameIn(Collection<String> firstNames);

	Collection<Person> findByFirstnameIn(String... firstNames);

	Collection<Person> findByFirstnameLike(String firstName);

	Collection<Person> findByFirstnameStartingWith(String firstName);

	Collection<Person> findByFirstnameAndLastname(String firstName, String lastName);

	Collection<Person> findByFirstnameIgnoreCaseAndLastnameIgnoreCase(String firstName, String lastName);

	Collection<Person> findByFirstnameAndLastnameAllIgnoringCase(String firstName, String lastName);

	Collection<Person> findByFirstnameOrLastname(String firstName, String lastName);

	Collection<Person> findDistinctByFirstnameOrLastname(String firstName, String lastName, Sort order);

	Person findByLastname(String lastName);

	Collection<Person> findByLastnameEndingWith(String lastName);

	List<Person> findDistinctByLastname(String lastName, Sort order);

	List<Person> findDistinctPeopleByOrderByLastnameDesc(Sort order);

}
