/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class Person {

  private final String firstName;
  private final String lastName;

  public Person(String firstName, String lastName) {

    Assert.hasText(firstName, "First name is required");
    Assert.hasText(lastName, "Last name is required");

    this.firstName = firstName;
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  @Override
  public boolean equals(final Object obj) {

    if (obj == this) {
      return true;
    }

    if (!(obj instanceof Person)) {
      return false;
    }

    Person that = (Person) obj;

    return ObjectUtils.nullSafeEquals(this.getFirstName(), that.getFirstName())
        && ObjectUtils.nullSafeEquals(this.getLastName(), that.getLastName());
  }

  @Override
  public int hashCode() {

    int hashValue = 17;

    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getFirstName());
    hashValue = 37 * hashValue + ObjectUtils.nullSafeHashCode(getLastName());

    return hashValue;
  }

  @Override
  public String toString() {
    return String.format("%1$s %2$s", getFirstName(), getLastName());
  }
}
