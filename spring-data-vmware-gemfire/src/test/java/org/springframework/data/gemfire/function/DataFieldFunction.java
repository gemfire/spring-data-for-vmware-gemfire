/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.apache.geode.pdx.PdxInstance;

public class DataFieldFunction implements Function<Object> {

  @Override
  public String getId() {
    return "getDataField";
  }

  @Override
  public void execute(FunctionContext<Object> context) {
    Object[] args = (Object[]) context.getArguments();

    PdxInstance data = (PdxInstance) args[0];
    String fieldName = (String) args[1];

    context.getResultSender().lastResult(data.getField(fieldName));
  }
}
