/*
 * Copyright 2022-2025 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.apache.geode.cache.execute.FunctionException;
import org.apache.geode.cache.execute.ResultCollector;
import org.apache.geode.distributed.DistributedMember;


@SuppressWarnings("rawtypes")
class MyResultCollector implements ResultCollector {

	@Override
	public void addResult(DistributedMember arg0, Object arg1) { }

	@Override
	public void clearResults() { }

	@Override
	public void endResults() { }

	@Override
	public Object getResult() throws FunctionException {
		return null;
	}

	@Override
	public Object getResult(long arg0, TimeUnit arg1) throws FunctionException {
		return null;
	}
}
