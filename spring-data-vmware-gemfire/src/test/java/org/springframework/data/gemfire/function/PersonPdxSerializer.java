/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.cache.Declarable;
import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

public class PersonPdxSerializer implements PdxSerializer, Declarable {

  @Override
  public boolean toData(Object obj, PdxWriter out) {

    if (obj instanceof Person) {

      Person person = (Person) obj;

      out.writeString("firstName", person.getFirstName());
      out.writeString("lastName", person.getLastName());

      return true;
    }

    return false;
  }

  @Override
  public Object fromData(Class<?> type, PdxReader in) {

    if (Person.class.isAssignableFrom(type)) {
      return new Person(in.readString("firstName"), in.readString("lastName"));
    }

    return null;
  }
}
