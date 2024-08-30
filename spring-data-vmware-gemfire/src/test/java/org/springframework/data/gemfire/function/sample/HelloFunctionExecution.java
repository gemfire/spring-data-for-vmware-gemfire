/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.sample;

import org.springframework.data.gemfire.function.annotation.OnMember;

/**
 * The HelloFunctionExecution interface is a Spring Data GemFire Function Execution interface
 * for the 'hello' GemFire Function and hello greetings.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.function.annotation.OnMember
 * @since 1.7.0
 */
@OnMember(groups = "HelloGroup")
@SuppressWarnings("unused")
public interface HelloFunctionExecution {

	String hello(String addressTo);

}
