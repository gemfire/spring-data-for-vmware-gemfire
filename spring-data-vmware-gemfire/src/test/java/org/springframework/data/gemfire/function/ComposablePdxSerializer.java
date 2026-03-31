/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudPdxReader;
import org.springframework.data.gemfire.gud.api.GudPdxSerializer;
import org.springframework.data.gemfire.gud.api.GudPdxWriter;

public class ComposablePdxSerializer implements GudPdxSerializer {

  private final GudPdxSerializer[] pdxSerializers;

  private ComposablePdxSerializer(GudPdxSerializer[] pdxSerializers) {
    this.pdxSerializers = pdxSerializers;
  }

  public static GudPdxSerializer compose(GudPdxSerializer... pdxSerializers) {

    return pdxSerializers == null
        ? null
        : pdxSerializers.length == 1
        ? pdxSerializers[0]
        : new ComposablePdxSerializer(pdxSerializers);
  }

  @Override
  public boolean toData(Object obj, GudPdxWriter out) {

    for (GudPdxSerializer pdxSerializer : this.pdxSerializers) {
      if (pdxSerializer.toData(obj, out)) {
        return true;
      }
    }

    return false;
  }

  @Override
  public Object fromData(Class<?> type, final GudPdxReader in) {

    for (GudPdxSerializer pdxSerializer : this.pdxSerializers) {

      Object obj = pdxSerializer.fromData(type, in);

      if (obj != null) {
        return obj;
      }
    }

    return null;
  }
}
