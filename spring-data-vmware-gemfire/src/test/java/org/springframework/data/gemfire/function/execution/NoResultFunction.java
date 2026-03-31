/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

public class NoResultFunction implements GudFunction {

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
  public void execute(GudFunctionContext functionContext) {}
}
