/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import java.util.List;

public class CollectionsFunction implements Function<List<Integer>> {

  @Override
  public String getId() {
    return "collections";
  }

  @Override
  public void execute(FunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();

    List<Integer> integers = (List<Integer>) args[0];

    functionContext.getResultSender().lastResult(integers);
  }
}
