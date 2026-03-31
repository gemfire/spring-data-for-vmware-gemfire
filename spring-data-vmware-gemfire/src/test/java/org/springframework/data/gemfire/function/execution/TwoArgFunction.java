/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudRegion;
import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

public class TwoArgFunction implements GudFunction {

  @Override
  public String getId() {
    return "twoArg";
  }

  @Override
  public void execute(GudFunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();

    GudRegion<String, Integer> region = functionContext.getCache().getRegion("TestRegion");
    if (region.get(args[0]) != null && region.get(args[1]) != null) {

      functionContext.getResultSender().lastResult(region.get(args[0]) + region.get(args[1]));
    }

    functionContext.getResultSender().lastResult(null);
  }
}
