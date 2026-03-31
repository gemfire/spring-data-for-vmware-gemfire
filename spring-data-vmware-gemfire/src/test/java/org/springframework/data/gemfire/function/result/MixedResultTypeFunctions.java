/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.result;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.springframework.data.gemfire.gud.api.GudFunction;

import org.springframework.data.gemfire.gud.api.GudFunctionContext;
import org.springframework.stereotype.Component;

/**
 * The {@link MixedResultTypeFunctions} class defines (implements) various Apache Geode {@link GudFunction Functions}
 * using SDG's {@link GudFunction} implementation annotation support.
 *
 * @author John Blum
 * @since 2.6.0
 * @see org.apache.geode.cache.execute.GudFunction
 * @see org.springframework.stereotype.Component
 */
@Component
@SuppressWarnings("unused")
public class MixedResultTypeFunctions {

	public static class SingleObjectFunction implements GudFunction {

		@Override
		public void execute(GudFunctionContext<BigDecimal> functionContext) {
			functionContext.getResultSender().lastResult(new BigDecimal(5));
		}

		@Override
		public String getId() {
			return "returnSingleObject";
		}
	}

	public static class ListObjectFunction implements GudFunction {

		@Override
		public void execute(GudFunctionContext<List<BigDecimal>> functionContext) {
			functionContext.getResultSender().lastResult(Collections.singletonList(new BigDecimal(10)));
		}

		@Override
		public String getId() {
			return "returnList";
		}
	}

	public static class SinglePrimitiveFunction implements GudFunction {

		@Override
		public void execute(GudFunctionContext<Integer> functionContext) {
			functionContext.getResultSender().lastResult(7);
		}

		@Override
		public String getId() {
			return "returnPrimitive";
		}
	}
}
