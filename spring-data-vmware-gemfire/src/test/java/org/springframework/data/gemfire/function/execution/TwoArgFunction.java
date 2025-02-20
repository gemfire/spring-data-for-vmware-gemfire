/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class TwoArgFunction implements Function<Integer> {

  @Override
  public String getId() {
    return "twoArg";
  }

  @Override
  public void execute(FunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();

    Region<String, Integer> region = functionContext.getCache().getRegion("TestRegion");
    if (region.get(args[0]) != null && region.get(args[1]) != null) {

      functionContext.getResultSender().lastResult(region.get(args[0]) + region.get(args[1]));
    }

    functionContext.getResultSender().lastResult(null);
  }
}
