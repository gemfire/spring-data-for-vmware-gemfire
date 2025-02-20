/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

import java.util.ArrayList;
import java.util.List;

public class CaptureUnconvertedArgumentTypesFunction implements Function<Class<?>[]> {

  @Override
  public void execute(FunctionContext<Class<?>[]> context) {
    Object[] args = context.getArguments();

    String stringValue = (String) args[0];
    Integer integerValue = (Integer) args[1];
    Boolean booleanValue = (Boolean) args[2];
    Object domainObject = args[3];
    Object enumValue = args[4];


    context.getResultSender().lastResult(getArgumentTypes(stringValue, integerValue, booleanValue, domainObject, enumValue));
  }

  @Override
  public String getId() {
    return "captureUnconvertedArgumentTypes";
  }

  private static Class<?>[] getArgumentTypes(Object... arguments) {

    List<Class<?>> argumentTypes = new ArrayList<>();

    for (Object argument : arguments) {
      argumentTypes.add(argument.getClass());
    }

    return argumentTypes.toArray(new Class[0]);
  }
}
