/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class ExceptionThrowingFunction implements Function<Object> {

  @Override
  public String getId() {
    return "exceptionThrowingFunction";
  }

  @Override
  public void execute(FunctionContext context) {
    context.getResultSender().sendException(new IllegalArgumentException("TEST"));
  }
}
