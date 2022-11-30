// Copyright (c) VMware, Inc. 2022. All rights reserved.
// SPDX-License-Identifier: Apache-2.0

package org.springframework.data.gemfire.repository.sample;

import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.data.gemfire.mapping.annotation.ReplicateRegion;
import org.springframework.util.ObjectUtils;

import lombok.Data;

/**
 * The Customer class is a class abstraction modeling a Customer.
 *
 * @author John Blum
 * @see org.springframework.data.annotation.Id
 * @see Region
 * @since 1.0.0
 */
@Data
@ReplicateRegion("Customers")
@SuppressWarnings("unused")
public class Customer {

	@Id
	private Long id;

	private String firstName;
	private String lastName;

	public Customer() {
	}

	public Customer(Long id) {
		this.id = id;
	}

	public Customer(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public String getName() {
		return String.format("%1$s %2$s", getFirstName(), getLastName());
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	protected static boolean equalsIgnoreNull(final Object obj1, final Object obj2) {
		return (Objects.equals(obj1, obj2));
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Customer)) {
			return false;
		}

		Customer that = (Customer) obj;

		return equalsIgnoreNull(this.getId(), that.getId())
			&& ObjectUtils.nullSafeEquals(this.getFirstName(), that.getFirstName())
			&& ObjectUtils.nullSafeEquals(this.getLastName(), that.getLastName());
	}

	protected static int hashCodeIgnoreNull(Object obj) {
		return (obj != null ? obj.hashCode() : 0);
	}

	@Override
	public int hashCode() {
		int hashValue = 17;
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getId());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getFirstName());
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getLastName());
		return hashValue;
	}

	@Override
	public String toString() {
		return getName();
	}
}
