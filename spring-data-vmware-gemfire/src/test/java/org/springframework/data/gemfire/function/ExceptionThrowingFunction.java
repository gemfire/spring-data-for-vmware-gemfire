/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

public class ExceptionThrowingFunction implements GudFunction {

  @Override
  public String getId() {
    return "exceptionThrowingFunction";
  }

  @Override
  public void execute(GudFunctionContext context) {
    context.getResultSender().sendException(new IllegalArgumentException("TEST"));
  }
}
