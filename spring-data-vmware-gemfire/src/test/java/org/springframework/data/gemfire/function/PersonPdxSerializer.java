/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudDeclarable;
import org.springframework.data.gemfire.gud.api.GudPdxReader;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudPdxWriter;

public class PersonPdxSerializer implements GudPdxSerializer, GudDeclarable {

  @Override
  public boolean toData(Object obj, GudPdxWriter out) {

    if (obj instanceof Person) {

      Person person = (Person) obj;

      out.writeString("firstName", person.getFirstName());
      out.writeString("lastName", person.getLastName());

      return true;
    }

    return false;
  }

  @Override
  public Object fromData(Class<?> type, GudPdxReader in) {

    if (Person.class.isAssignableFrom(type)) {
      return new Person(in.readString("firstName"), in.readString("lastName"));
    }

    return null;
  }
}
