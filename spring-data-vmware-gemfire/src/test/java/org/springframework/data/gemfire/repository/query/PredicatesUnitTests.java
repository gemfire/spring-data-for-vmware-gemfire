/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;

import org.junit.Test;

import org.springframework.data.gemfire.repository.query.Predicates.AtomicPredicate;
import org.springframework.data.repository.query.parser.Part;

/**
 * Unit Tests for {@link Predicates}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.junit.Test
 * @see Predicates
 */
@SuppressWarnings("unused")
public class PredicatesUnitTests {

	@Test
	public void atomicPredicateDefaultsAlias() {

		Part part = new Part("firstname", Person.class);

		Iterator<Integer> indexes = Collections.singletonList(1).iterator();

		Predicate predicate = new AtomicPredicate(part, indexes);

		assertThat(predicate.toString(null)).isEqualTo("x.firstname = $1");
	}

	@Test
	public void concatenatesAndPredicateCorrectly() {

		Part left = new Part("firstname", Person.class);
		Part right = new Part("lastname", Person.class);

		Iterator<Integer> indexes = Arrays.asList(1, 2).iterator();

		Predicate predicate = Predicates.create(left, indexes).and(Predicates.create(right, indexes));

		assertThat(predicate).isNotNull();
		assertThat(predicate.toString(null)).isEqualTo("x.firstname = $1 AND x.lastname = $2");
	}

	@Test
	public void concatenatesOrPredicateCorrectly() {

		Part left = new Part("firstname", Person.class);
		Part right = new Part("lastname", Person.class);

		Iterator<Integer> indexes = Arrays.asList(1, 2).iterator();

		Predicate predicate = Predicates.create(left, indexes).or(Predicates.create(right, indexes));

		assertThat(predicate).isNotNull();
		assertThat(predicate.toString(null)).isEqualTo("x.firstname = $1 OR x.lastname = $2");
	}

	@Test
	public void handlesBooleanBasedPredicateCorrectly() {

		Part part = new Part("activeTrue", User.class);

		Iterator<Integer> indexes = Collections.singletonList(1).iterator();

		Predicates predicate = Predicates.create(part, indexes);

		assertThat(predicate).isNotNull();
		assertThat(predicate.toString("user")).isEqualTo("user.active = true");
	}

	/**
	 * @link https://jira.spring.io/browse/SGF-507
	 */
	@Test
	public void handlesIgnoreCasePredicateCorrectly() {

		Part left = new Part("firstnameIgnoreCase", Person.class);
		Part right = new Part("lastnameIgnoreCase", Person.class);

		Iterator<Integer> indexes = Arrays.asList(1, 2).iterator();

		Predicate predicate = Predicates.create(left, indexes).and(Predicates.create(right, indexes));

		assertThat(predicate).isNotNull();
		assertThat(predicate.toString("person"))
			.isEqualTo("person.firstname.equalsIgnoreCase($1) AND person.lastname.equalsIgnoreCase($2)");
	}

	static class Person {
		String firstname;
		String lastname;
	}

	// TODO refactor Person to include boolean state; remove User
	static class User {
		Boolean active;
		String username;
	}
}
