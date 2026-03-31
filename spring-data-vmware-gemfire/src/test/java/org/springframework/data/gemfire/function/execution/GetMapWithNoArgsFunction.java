/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

import java.util.HashMap;
import java.util.Map;

public class GetMapWithNoArgsFunction implements GudFunction {
  @Override
  public String getId() {
    return "getMapWithNoArgs";
  }

  @Override
  public void execute(GudFunctionContext functionContext) {
    Map<String, Integer> region = new HashMap<>();
    for (Map.Entry<Object, Object> entry : functionContext.getCache().getRegion("TestRegion").entrySet()) {
      region.put((String)entry.getKey(), (Integer)entry.getValue());
    }

    functionContext.getResultSender().lastResult(region);
  }
}
