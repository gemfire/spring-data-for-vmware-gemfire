/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class Address {

  private final String street;
  private final String city;
  private final String state; // Refactor; use Enum!
  private final String zipCode;

  public Address(String street, String city, String state, String zipCode) {

    Assert.hasText("Street is required", street);
    Assert.hasText("City is required", city);
    Assert.hasText("State is required", state);
    Assert.hasText("ZipCode is required", zipCode);

    this.street = street;
    this.city = city;
    this.state = state;
    this.zipCode = zipCode;
  }

  public String getStreet() {
    return street;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

  public String getZipCode() {
    return zipCode;
  }

  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Address)) {
      return false;
    }

    Address that = (Address) obj;

    return ObjectUtils.nullSafeEquals(this.getStreet(), that.getStreet())
        && ObjectUtils.nullSafeEquals(this.getCity(), that.getCity())
        && ObjectUtils.nullSafeEquals(this.getState(), that.getState())
        && ObjectUtils.nullSafeEquals(this.getZipCode(), that.getZipCode());
  }

  @Override
  public int hashCode() {

    int hashValue = 17;

    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getStreet());
    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getCity());
    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getState());
    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getZipCode());

    return hashValue;
  }

  @Override
  public String toString() {
    return String.format("%1$s %2$s, %3$s %4$s", getStreet(), getCity(), getState(), getZipCode());
  }
}
