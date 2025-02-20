/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.pdx.PdxReader;
import org.apache.geode.pdx.PdxSerializer;
import org.apache.geode.pdx.PdxWriter;

public class ComposablePdxSerializer implements PdxSerializer {

  private final PdxSerializer[] pdxSerializers;

  private ComposablePdxSerializer(PdxSerializer[] pdxSerializers) {
    this.pdxSerializers = pdxSerializers;
  }

  public static PdxSerializer compose(PdxSerializer... pdxSerializers) {

    return pdxSerializers == null
        ? null
        : pdxSerializers.length == 1
        ? pdxSerializers[0]
        : new ComposablePdxSerializer(pdxSerializers);
  }

  @Override
  public boolean toData(Object obj, PdxWriter out) {

    for (PdxSerializer pdxSerializer : this.pdxSerializers) {
      if (pdxSerializer.toData(obj, out)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Object fromData(Class<?> type, final PdxReader in) {

    for (PdxSerializer pdxSerializer : this.pdxSerializers) {

      Object obj = pdxSerializer.fromData(type, in);

      if (obj != null) {
        return obj;
      }
    }

    return null;
  }
}
