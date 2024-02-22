/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.config.two;

import java.util.Set;

import org.springframework.data.gemfire.function.annotation.Filter;
import org.springframework.data.gemfire.function.annotation.FunctionId;
import org.springframework.data.gemfire.function.annotation.OnRegion;

/**
 * @author David Turanski
 *
 */
@OnRegion(id="testFunction", region="r1")
public interface TestOnRegionFunction {
	   @FunctionId("f1")
	   public String getString(Object arg1, @Filter Set<Object> keys) ;

	   @FunctionId("f2")
	   public String getString(Object arg1) ;
}



