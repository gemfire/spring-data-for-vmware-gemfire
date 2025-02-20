/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

public class AddressPdxSerializer implements PdxSerializer {

  @Override
  public boolean toData(Object obj, PdxWriter out) {

    if (obj instanceof Address) {

      Address address = (Address) obj;

      out.writeString("street", address.getStreet());
      out.writeString("city", address.getCity());
      out.writeString("state", address.getState());
      out.writeString("zipCode", address.getZipCode());

      return true;
    }

    return false;
  }

  @Override
  public Object fromData(Class<?> type, PdxReader in) {

    if (Address.class.isAssignableFrom(type)) {
      return new Address(in.readString("street"), in.readString("city"), in.readString("state"),
          in.readString("zipCode"));
    }

    return null;
  }
}
