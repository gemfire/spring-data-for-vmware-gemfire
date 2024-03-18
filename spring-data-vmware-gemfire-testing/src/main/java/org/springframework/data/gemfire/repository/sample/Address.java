/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.sample;

import org.springframework.data.annotation.Id;
import org.springframework.data.gemfire.mapping.annotation.Region;
import org.springframework.util.ObjectUtils;

/**
 *
 * @author Oliver Gierke
 */
@Region("address")
public class Address {

	public String street;
	public String city;

	@Id
	public String zipCode;

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Address)) {
			return false;
		}

		Address that = (Address) obj;

		return ObjectUtils.nullSafeEquals(this.street, that.street)
			&& ObjectUtils.nullSafeEquals(this.city, that.city)
			&& ObjectUtils.nullSafeEquals(this.zipCode, that.zipCode);
	}

	@Override
	public int hashCode() {

		int hashValue = 17;

		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(this.street);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(this.city);
		hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(this.zipCode);

		return hashValue;
	}

	@Override
	public String toString() {
		return String.format("%1$s %2$s, %3$s", this.street, this.city, this.zipCode);
	}
}
