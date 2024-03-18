/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;
import org.apache.geode.cache.util.ObjectSizer;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.eviction.EvictionActionType;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;

/**
 * The {@link EnableEviction} annotation marks a Spring {@link Configuration @Configuration} annotated class
 * to enable {@link Region} Eviction.
 *
 * @author John Blum
 * @see EvictionAttributes
 * @see Region
 * @see ObjectSizer
 * @see Import
 * @see EvictionConfiguration
 * @see EvictionActionType
 * @see EvictionPolicyType
 * @see Region
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(EvictionConfiguration.class)
@SuppressWarnings({ "unused" })
public @interface EnableEviction {

	/**
	 * Defines individual {@link Region} Eviction policies or customizes the default Eviction policy applied
	 * to all {@link Region Regions}.
	 *
	 * Defaults to empty.
	 */
	EvictionPolicy[] policies() default {};

	/**
	 * Definition for a specific Eviction policy that can be applied to 1 or more {@link Region Regions}.
	 *
	 * An Eviction policy defines the maximum (a.k.a. threshold) along with {@link ObjectSizer} used to size
	 * {@link Region} entry values and the action applied when {@link Region} entries are to be evicted.
	 *
	 * Additionally, the Eviction policy defines the algorithm used (eviction based on entry count, JVM Heap percentage
	 * or system memory size used) to determine when an Eviction should occur.
	 */
	@interface EvictionPolicy {

		/**
		 * Action to take on an {@link Region} entry when evicted.
		 *
		 * Defaults to {@link EvictionActionType#LOCAL_DESTROY}.
		 *
		 * @see EvictionActionType
		 */
		EvictionActionType action() default EvictionActionType.LOCAL_DESTROY;

		/**
		 * Threshold applied for entry count Eviction.
		 *
		 * Defaults to {@link EvictionAttributes#DEFAULT_ENTRIES_MAXIMUM}
		 */
		int maximum() default EvictionAttributes.DEFAULT_ENTRIES_MAXIMUM;

		/**
		 * Name of a Spring bean of type {@link ObjectSizer} defined in the Spring application context
		 * used to size {@link Region} entry values.
		 *
		 * Defaults to empty.
		 *
		 * @see ObjectSizer
		 */
		String objectSizerName() default "";

		/**
		 * Names of all the {@link Region Regions} in which this Eviction policy will be applied.
		 *
		 * Defaults to empty.
		 */
		String[] regionNames() default {};

		/**
		 * Eviction algorithm used during Eviction.
		 *
		 * Defaults to {@link EvictionPolicyType#ENTRY_COUNT}.
		 *
		 * @see EvictionPolicyType
		 */
		EvictionPolicyType type() default EvictionPolicyType.ENTRY_COUNT;

	}
}
