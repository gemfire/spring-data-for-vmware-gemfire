/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

public class OneArgFunction implements GudFunction {

  @Override
  public String getId() {
    return "oneArg";
  }

  @Override
  public void execute(GudFunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();
    String key = (String) args[0];
    GudRegion<String, Integer> region = functionContext.getCache().getRegion("TestRegion");
    functionContext.getResultSender().lastResult(region.get(key));
  }
}
