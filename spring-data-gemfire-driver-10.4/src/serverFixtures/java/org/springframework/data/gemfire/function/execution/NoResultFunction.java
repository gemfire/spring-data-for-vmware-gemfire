/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class NoResultFunction implements Function {

  @Override
  public boolean hasResult() {
    return false;
  }

  @Override
  public boolean isHA() {
    return false;
  }

  @Override
  public String getId() {
    return "noResult";
  }

  @Override
  public void execute(FunctionContext functionContext) {}
}
