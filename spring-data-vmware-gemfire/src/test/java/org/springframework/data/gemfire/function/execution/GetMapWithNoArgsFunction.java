/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import java.util.HashMap;
import java.util.Map;

public class GetMapWithNoArgsFunction implements Function<Map<String, Integer>> {
  @Override
  public String getId() {
    return "getMapWithNoArgs";
  }

  @Override
  public void execute(FunctionContext functionContext) {
    Map<String, Integer> region = new HashMap<>();
    for (Map.Entry<Object, Object> entry : functionContext.getCache().getRegion("TestRegion").entrySet()) {
      region.put((String)entry.getKey(), (Integer)entry.getValue());
    }

    functionContext.getResultSender().lastResult(region);
  }
}
