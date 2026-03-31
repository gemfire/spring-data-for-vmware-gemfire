/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution.onservers;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;
import org.springframework.data.gemfire.function.sample.Metric;

import java.util.ArrayList;
import java.util.List;

public class MetricsFunctionServerConfiguration implements GudFunction {

  private static final int NUMBER_OF_METRICS = 10;

  @Override
  public void execute(GudFunctionContext functionContext) {
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
