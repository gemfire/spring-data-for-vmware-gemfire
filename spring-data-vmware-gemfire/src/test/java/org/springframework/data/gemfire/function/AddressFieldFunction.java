/*
 * Copyright (c) 2026 Broadcom. All rights reserved.
 */

/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;
import org.springframework.data.gemfire.gud.api.GudPdxInstance;
import org.springframework.util.Assert;

public class AddressFieldFunction implements GudFunction {

  @Override
  public String getId() {
    return "getAddressField";
  }

  @Override
  public void execute(GudFunctionContext context) {
    Object[] args = (Object[]) context.getArguments();

    GudPdxInstance address = (GudPdxInstance) args[0];
    String fieldName = (String) args[1];

    Assert.isTrue(Address.class.getName().equals(address.getClassName()),
        "Address is not the correct type");

    context.getResultSender().lastResult(String.valueOf(address.getField(fieldName)));
  }
}
