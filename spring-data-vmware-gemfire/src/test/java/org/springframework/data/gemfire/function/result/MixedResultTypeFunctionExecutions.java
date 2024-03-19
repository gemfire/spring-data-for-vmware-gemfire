/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.result;

import java.math.BigDecimal;
import java.util.List;

import org.apache.geode.cache.execute.Function;

import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnRegion;

/**
 * The MixedResultTypeFunctionExecutions class declares various Apache Geode {@link Function Functions}
 *  * using SDG's {@link Function} implementation annotation support.
 *
 * @author John Blum
 * @see FunctionId
 * @see OnRegion
 * @since 2.6.0
 */
@OnRegion(region = "Numbers")
interface MixedResultTypeFunctionExecutions {

	@FunctionId("returnSingleObject")
	BigDecimal returnFive();

	@FunctionId("returnList")
	List<BigDecimal> returnList();

	@FunctionId("returnPrimitive")
	int returnPrimitive();

}
