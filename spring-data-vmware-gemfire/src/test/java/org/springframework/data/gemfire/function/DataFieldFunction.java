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

public class DataFieldFunction implements GudFunction {

  @Override
  public String getId() {
    return "getDataField";
  }

  @Override
  public void execute(GudFunctionContext context) {
    Object[] args = (Object[]) context.getArguments();

    GudPdxInstance data = (GudPdxInstance) args[0];
    String fieldName = (String) args[1];

    context.getResultSender().lastResult(data.getField(fieldName));
  }
}
