/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.sample;

import java.util.List;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnServer;

/**
 * @author Patrick Johnson
 */
@OnServer
public interface SingleServerAdminFunctions {

	@FunctionId("GetAllMetricsFunction")
	List<Metric> getAllMetrics();

}
