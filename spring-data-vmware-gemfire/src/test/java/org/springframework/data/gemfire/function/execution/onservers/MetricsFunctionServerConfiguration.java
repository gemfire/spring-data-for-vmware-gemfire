/*
 * Copyright 2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution.onservers;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;
import org.springframework.data.gemfire.function.sample.Metric;

import java.util.ArrayList;
import java.util.List;

public class MetricsFunctionServerConfiguration implements Function<List<Metric>> {

  private static final int NUMBER_OF_METRICS = 10;

  @Override
  public void execute(FunctionContext functionContext) {
    List<Metric> allMetrics = new ArrayList<>();

    for (int i = 0; i < NUMBER_OF_METRICS; i++) {
      Metric metric = new Metric("statName" + i, i, "statCat" + i, "statType" + i);
      allMetrics.add(metric);
    }

    functionContext.getResultSender().lastResult(allMetrics);
  }

  @Override
  public String getId() {
    return "GetAllMetricsFunction";
  }
}
