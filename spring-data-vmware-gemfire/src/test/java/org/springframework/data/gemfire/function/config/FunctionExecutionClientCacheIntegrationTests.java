/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.TimeUnit;

import org.springframework.data.gemfire.gud.api.GudFunctionException;
import org.springframework.data.gemfire.gud.api.GudResultCollector;
import org.springframework.data.gemfire.gud.api.GudDistributedMember;


@SuppressWarnings("rawtypes")
class MyResultCollector implements GudResultCollector {

	@Override
	public void addResult(GudDistributedMember arg0, Object arg1) { }

	@Override
	public void clearResults() { }

	@Override
	public void endResults() { }

	@Override
	public Object getResult() throws GudFunctionException {
		return null;
	}

	@Override
	public Object getResult(long arg0, TimeUnit arg1) throws GudFunctionException {
		return null;
	}
}
