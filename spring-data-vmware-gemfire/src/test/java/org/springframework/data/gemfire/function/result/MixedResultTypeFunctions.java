/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.result;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.geode.cache.execute.Function;

import org.springframework.data.gemfire.function.annotation.GemfireFunction;
import org.springframework.stereotype.Component;

/**
 * The {@link MixedResultTypeFunctions} class defines (implements) various Apache Geode {@link Function Functions}
 * using SDG's {@link Function} implementation annotation support.
 *
 * @author John Blum
 * @since 2.6.0
 * @see Function
 * @see GemfireFunction
 * @see Component
 */
@Component
@SuppressWarnings("unused")
public class MixedResultTypeFunctions {

	@GemfireFunction(id = "returnSingleObject", hasResult = true)
	public BigDecimal returnSingleObject() {
		return new BigDecimal(5);
	}

	@GemfireFunction(id = "returnList", hasResult = true)
	public List<BigDecimal> returnList() {
		return Collections.singletonList(new BigDecimal(10));
	}

	@GemfireFunction(id = "returnPrimitive", hasResult = true)
	public int returnPrimitive() {
		return 7;
	}
}
