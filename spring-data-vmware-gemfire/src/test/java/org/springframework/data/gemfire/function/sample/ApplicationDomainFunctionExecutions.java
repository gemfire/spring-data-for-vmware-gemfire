/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.function.sample;

import org.apache.geode.pdx.PdxInstance;

import org.springframework.data.gemfire.function.ClientCacheFunctionExecutionWithPdxIntegrationTest;
import org.springframework.data.gemfire.function.annotation.OnServer;

/**
 * The ApplicationDomainFunctionExecutions class defines a GemFire Client Cache Function execution targeted at a
 * GemFire Server.
 *
 * @author John Blum
 * @see org.springframework.data.gemfire.function.ClientCacheFunctionExecutionWithPdxIntegrationTest
 * @see org.springframework.data.gemfire.function.annotation.OnServer
 * @see org.apache.geode.pdx.PdxInstance
 * @since 1.0.0
 */
@OnServer
@SuppressWarnings("unused")
public interface ApplicationDomainFunctionExecutions {

	Class<?>[] captureConvertedArgumentTypes(String stringValue, Integer integerValue, Boolean booleanValue,
		ClientCacheFunctionExecutionWithPdxIntegrationTest.Person person,
			ClientCacheFunctionExecutionWithPdxIntegrationTest.Gender gender);

	Class<?>[] captureUnconvertedArgumentTypes(String stringValue, Integer integerValue, Boolean booleanValue,
		Object person, Object gender);

	String getAddressField(ClientCacheFunctionExecutionWithPdxIntegrationTest.Address address, String fieldName);

	Integer getDataField(PdxInstance data, String fieldName);

}
