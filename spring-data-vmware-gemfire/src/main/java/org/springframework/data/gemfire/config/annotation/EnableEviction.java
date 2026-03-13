/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-13: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.eviction.EvictionActionType;
import org.springframework.data.gemfire.eviction.EvictionPolicyType;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudObjectSizer;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link EnableEviction} annotation marks a Spring {@link Configuration @Configuration} annotated class
 * to enable {@link GudRegion} Eviction.
 *
 * @author John Blum
 * @see GudEvictionAttributes
 * @see GudRegion
 * @see GudObjectSizer
 * @see Import
 * @see EvictionConfiguration
 * @see EvictionActionType
 * @see EvictionPolicyType
 * @see GudRegion
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
	 * Defines individual {@link GudRegion} Eviction policies or customizes the default Eviction policy applied
	 * to all {@link GudRegion Regions}.
	 *
	 * Defaults to empty.
	 */
	EvictionPolicy[] policies() default {};

	/**
	 * Definition for a specific Eviction policy that can be applied to 1 or more {@link GudRegion Regions}.
	 *
	 * An Eviction policy defines the maximum (a.k.a. threshold) along with {@link GudObjectSizer} used to size
	 * {@link GudRegion} entry values and the action applied when {@link GudRegion} entries are to be evicted.
	 *
	 * Additionally, the Eviction policy defines the algorithm used (eviction based on entry count, JVM Heap percentage
	 * or system memory size used) to determine when an Eviction should occur.
	 */
	@interface EvictionPolicy {

		/**
		 * Action to take on an {@link GudRegion} entry when evicted.
		 *
		 * Defaults to {@link EvictionActionType#LOCAL_DESTROY}.
		 *
		 * @see EvictionActionType
		 */
		EvictionActionType action() default EvictionActionType.LOCAL_DESTROY;

		/**
		 * Threshold applied for entry count Eviction.
		 *
		 * Defaults to {@link GudEvictionAttributes#DEFAULT_ENTRIES_MAXIMUM}
		 */
		int maximum() default GudEvictionAttributes.DEFAULT_ENTRIES_MAXIMUM;

		/**
		 * Name of a Spring bean of type {@link GudObjectSizer} defined in the Spring application context
		 * used to size {@link GudRegion} entry values.
		 *
		 * Defaults to empty.
		 *
		 * @see GudObjectSizer
		 */
		String objectSizerName() default "";

		/**
		 * Names of all the {@link GudRegion Regions} in which this Eviction policy will be applied.
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
