/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.util.ObjectUtils;

/**
 * Abstract Data Type (ADT) modeling a Person.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see Serializable
 * @see Id
 * @see Region
 */
@Region("simple")
@JsonIgnoreProperties("name")
public class Person implements Serializable {

	private static final long serialVersionUID = 508843183613325255L;

	public Address address;

	@Id
	public Long id;

	public String firstname;
	public String lastname;

	@PersistenceConstructor
	public Person() { }

	public Person(Long id) {
		this.id = id;
	}

	public Person(String firstName, String lastName) {
		this.firstname = firstName;
		this.lastname = lastName;
	}

	public Person(Long id, String firstname, String lastname) {
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
	}

	/**
	 * Gets the Person's address.
	 *
	 * @return the Address of the Person.
	 */
	public Address getAddress() {
		return address;
	}

	/**
	 * Returns the identifier (ID) of this Person.
	 *
	 * @return a Long value with the ID of this Person.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Gets this Person's first name.
	 *
	 * @return the first name of this Person.
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * Gets this Person's last name.
	 *
	 * @return the last name of this Person.
	 */
	public String getLastname() {
		return lastname;
	}

	/**
	 * Returns the Person's full name.
	 *
	 * @return the first and last name of the Person.
	 * @see #getFirstname()
	 * @see #getLastname()
	 */
	@Transient
	public String getName() {
		return String.format("%1$s %2$s", getFirstname(), getLastname());
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Person)) {
			return false;
		}

		Person that = (Person) obj;

		return (this.id != null && this.id.equals(that.id));
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.id);
	}

	@Override
	public String toString() {
		return String.format("{ @type = %1$s, id = %2$d, name = %3$s }", getClass().getName(), id, getName());
	}
}
