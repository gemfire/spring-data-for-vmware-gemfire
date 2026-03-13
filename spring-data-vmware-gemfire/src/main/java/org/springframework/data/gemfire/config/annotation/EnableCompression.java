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

import static org.springframework.data.gemfire.config.annotation.CompressionConfiguration.SNAPPY_COMPRESSOR_BEAN_NAME;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.gud.api.GudCompressor;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link EnableCompression} annotation marks a Spring {@link Configuration @Configuration} annotated application
 * {@link Class} to configure and enable Pivotal GemFire/Apache Geode {@link GudRegion} data compression.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see Import
 * @see CompressionConfiguration
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(CompressionConfiguration.class)
public @interface EnableCompression {

	/**
	 * Reference to the {@link String name} of a bean having type {@link GudCompressor}
	 * registered in the Spring container to handle {@link GudRegion} compression.
	 *
	 * Defaults to {@literal snappyCompressor}.
	 *
	 * Set the {@literal spring.data.gemfire.cache.compression.compressor-bean-name}
	 * in {@literal application.properties}.
	 */
	String compressorBeanName() default SNAPPY_COMPRESSOR_BEAN_NAME;

	/**
	 * Identifies all the {@link GudRegion Regions} by name in which the data compression will be enabled.
	 *
	 * Defaults to all {@link GudRegion Regions}.
	 *
	 * Set the {@literal spring.data.gemfire.cache.compression.region-names} property
	 * in {@literal application.properties}.
	 */
	String[] regionNames() default {};

}
