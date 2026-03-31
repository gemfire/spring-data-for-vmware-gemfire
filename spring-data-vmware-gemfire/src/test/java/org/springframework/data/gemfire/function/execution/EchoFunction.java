/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.springframework.data.gemfire.gud.api.GudFunction;
import org.springframework.data.gemfire.gud.api.GudFunctionContext;

public class EchoFunction implements GudFunction {

	public static String FUNCTION_ID = "echoFunction";

		@Override
		public String getId() {
			return FUNCTION_ID;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void execute(GudFunctionContext functionContext) {

			Object[] arguments = (Object[]) functionContext.getArguments();

			for (int index = 0; index < arguments.length; index++) {
				if ((index + 1) == arguments.length){
					functionContext.getResultSender().lastResult(arguments[index]);
				}
				else {
					functionContext.getResultSender().sendResult(arguments[index]);
				}
			}
		}
	}
