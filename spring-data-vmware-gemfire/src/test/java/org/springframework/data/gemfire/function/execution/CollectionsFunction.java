/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

import java.util.List;

public class CollectionsFunction implements GudFunction {

  @Override
  public String getId() {
    return "collections";
  }

  @Override
  public void execute(GudFunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();

    List<Integer> integers = (List<Integer>) args[0];

    functionContext.getResultSender().lastResult(integers);
  }
}
