/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.function.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Used to inject Region data into a function execution. The annotated parameter must be of type
 * {@link java.util.Map}. The contents depends on the region configuration (for a partitioned region, this will
 * contain only entries for the local partition)
 * and any filters configured for the function context.
 *
 * @author David Turanski
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RegionData {
}
