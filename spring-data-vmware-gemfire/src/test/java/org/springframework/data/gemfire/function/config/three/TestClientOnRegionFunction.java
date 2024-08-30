/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config.three;

import java.util.Set;

import org.springframework.data.gemfire.function.annotation.Filter;
import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnRegion;

/**
 * @author David Turanski
 *
 */
@OnRegion(id="testClientOnRegionFunction", region="r1")
public interface TestClientOnRegionFunction {
	   @FunctionId("f1")
	   public String getString(Object arg1, @Filter Set<Object> keys) ;

	   @FunctionId("f2")
	   public String getString(Object arg1) ;
}



