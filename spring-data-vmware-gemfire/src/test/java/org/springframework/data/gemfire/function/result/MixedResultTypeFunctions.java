/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.result;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.apache.geode.cache.execute.Function;

import org.apache.geode.cache.execute.FunctionContext;
import org.springframework.stereotype.Component;

/**
 * The {@link MixedResultTypeFunctions} class defines (implements) various Apache Geode {@link Function Functions}
 * using SDG's {@link Function} implementation annotation support.
 *
 * @author John Blum
 * @since 2.6.0
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.stereotype.Component
 */
@Component
@SuppressWarnings("unused")
public class MixedResultTypeFunctions {

	public static class SingleObjectFunction implements Function<BigDecimal> {

		@Override
		public void execute(FunctionContext<BigDecimal> functionContext) {
			functionContext.getResultSender().lastResult(new BigDecimal(5));
		}

		@Override
		public String getId() {
			return "returnSingleObject";
		}
	}

	public static class ListObjectFunction implements Function<List<BigDecimal>> {

		@Override
		public void execute(FunctionContext<List<BigDecimal>> functionContext) {
			functionContext.getResultSender().lastResult(Collections.singletonList(new BigDecimal(10)));
		}

		@Override
		public String getId() {
			return "returnList";
		}
	}

	public static class SinglePrimitiveFunction implements Function<Integer> {

		@Override
		public void execute(FunctionContext<Integer> functionContext) {
			functionContext.getResultSender().lastResult(7);
		}

		@Override
		public String getId() {
			return "returnPrimitive";
		}
	}
}
