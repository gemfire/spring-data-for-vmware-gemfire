/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudPdxReader;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudPdxWriter;

public class AddressPdxSerializer implements GudPdxSerializer {

  @Override
  public boolean toData(Object obj, GudPdxWriter out) {

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
  public Object fromData(Class<?> type, GudPdxReader in) {

    if (Address.class.isAssignableFrom(type)) {
      return new Address(in.readString("street"), in.readString("city"), in.readString("state"),
          in.readString("zipCode"));
    }

    return null;
  }
}
