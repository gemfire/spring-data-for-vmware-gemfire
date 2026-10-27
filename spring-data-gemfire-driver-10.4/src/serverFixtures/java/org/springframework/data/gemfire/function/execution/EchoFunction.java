/*
 * Copyright 2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.execution;

import org.apache.geode.cache.execute.Function;
import org.apache.geode.cache.execute.FunctionContext;

public class EchoFunction implements Function<Object> {


		@Override
		public String getId() {
			return FunctionFixtureIds.ECHO;
		}

		@Override
		@SuppressWarnings("unchecked")
		public void execute(FunctionContext functionContext) {

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