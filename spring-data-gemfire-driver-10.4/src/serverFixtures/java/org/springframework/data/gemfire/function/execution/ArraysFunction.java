/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class ArraysFunction implements Function<Object[]> {

  @Override
  public String getId() {
    return "arrays";
  }

  @Override
  public void execute(FunctionContext functionContext) {
    Object[] args = (Object[]) functionContext.getArguments();

    functionContext.getResultSender().lastResult(args[0]);
  }

}
