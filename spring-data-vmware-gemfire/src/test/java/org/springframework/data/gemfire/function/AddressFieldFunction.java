/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.pdx.PdxInstance;
import org.springframework.util.Assert;

public class AddressFieldFunction implements Function<Object> {

  @Override
  public String getId() {
    return "getAddressField";
  }

  @Override
  public void execute(FunctionContext<Object> context) {
    Object[] args = (Object[]) context.getArguments();

    PdxInstance address = (PdxInstance) args[0];
    String fieldName = (String) args[1];

    Assert.isTrue(Address.class.getName().equals(address.getClassName()),
        "Address is not the correct type");

    context.getResultSender().lastResult(String.valueOf(address.getField(fieldName)));
  }
}
