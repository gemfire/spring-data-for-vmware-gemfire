/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
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
 * @see Function
 * @see OnServer
 * @since 1.7.0
 */
@OnServer
@SuppressWarnings("unused")
public interface ExceptionThrowingFunctionExecution {

	Integer exceptionThrowingFunction();

}
