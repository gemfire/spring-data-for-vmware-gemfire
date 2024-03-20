/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.sample;

import org.apache.geode.cache.execute.Function;

import org.springframework.data.gemfire.function.annotation.OnServer;

/**
 * The {@link ExceptionThrowingFunctionExecution} interface defines a GemFire {@link Function}
 * that throws a {@link RuntimeException}.
 *
 * @author John Blum
 * @see org.apache.geode.cache.execute.Function
 * @see org.springframework.data.gemfire.function.annotation.OnServer
 * @since 1.7.0
 */
@OnServer
@SuppressWarnings("unused")
public interface ExceptionThrowingFunctionExecution {

	Integer exceptionThrowingFunction();

}
