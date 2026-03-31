/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.sample;

import org.springframework.data.gemfire.gud.api.GudFunction;

import org.springframework.data.gemfire.function.annotation.OnServer;

/**
 * The {@link ExceptionThrowingFunctionExecution} interface defines a GemFire {@link GudFunction}
 * that throws a {@link RuntimeException}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.execute.GudFunction
 * @see org.springframework.data.gemfire.function.annotation.OnServer
 * @since 1.7.0
 */
@OnServer
@SuppressWarnings("unused")
public interface ExceptionThrowingFunctionExecution {

	Integer exceptionThrowingFunction();

}
