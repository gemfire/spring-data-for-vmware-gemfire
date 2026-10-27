/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.Region;
import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class OneArgFunction implements Function<Integer> {

  @Override
  public String getId() {
    return "oneArg";
  }

  @Override
  public void execute(FunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();
    String key = (String) args[0];
    Region<String, Integer> region = functionContext.getCache().getRegion("TestRegion");
    functionContext.getResultSender().lastResult(region.get(key));
  }
}
