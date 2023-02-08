/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.sample;

import java.util.List;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnServers;

/**
 * @author Patrick Johnson
 */
@OnServers
public interface AllServersAdminFunctions {

	@FunctionId("GetAllMetricsFunction")
	List<List<Metric>> getAllMetrics();

}
