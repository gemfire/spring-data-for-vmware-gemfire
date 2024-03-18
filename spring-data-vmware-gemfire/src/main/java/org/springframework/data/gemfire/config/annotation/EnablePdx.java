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

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.gemfire.mapping.MappingPdxSerializer;

/**
 * The {@link EnablePdx} annotation marks a Spring {@link Configuration @Configuration} annotated {@link Class}
 * to enable the Apache Geode PDX features and functionality in this peer cache, cluster member or cache client
 * application.
 *
 * @author John Blum
 * @see java.lang.annotation.Annotation
 * @see Import
 * @see PdxConfiguration
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(PdxConfiguration.class)
@SuppressWarnings("unused")
public @interface EnablePdx {

	/**
	 * Configures the disk store that is used for PDX meta data.
	 *
	 * Use the {@literal spring.data.gemfire.pdx.disk-store-name} property in {@literal application.properties}.
	 */
	String diskStoreName() default PdxConfiguration.DEFAULT_PDX_DISK_STORE_NAME;

	/**
	 * Configures whether pdx ignores fields that were unread during deserialization.
	 *
	 * Default is {@literal false}.
	 *
	 * Use the {@literal spring.data.gemfire.pdx.ignore-unread-fields} property in {@literal application.properties}.
	 */
	boolean ignoreUnreadFields() default PdxConfiguration.DEFAULT_IGNORE_UNREAD_FIELDS;

	/**
	 * When using the Spring Data Geode's (SDG) {@link MappingPdxSerializer} most application domain {@link Class types}
	 * are included for Apache Geode PDX serialization, by default. However, certain {@link Class types} are excluded
	 * by SDG's {@link MappingPdxSerializer}, such as {@literal java.*}, {@literal javax.*}, {@literal com.gemstone.*},
	 * {@literal org.apache.geode.*} and {@literal org.springframework.*} {@link Class types}. This allows the default
	 * behavior to be overridden when and where necessary.
	 *
	 * @return an array of {@link Class types} to be handled by the {@link MappingPdxSerializer}, possibly overriding
	 * the excluded {@link Class types} by default.
	 */
	Class<?>[] includeDomainTypes() default {};

	/**
	 * Configures whether the type metadata for PDX objects is persisted to disk.
	 *
	 * Default is {@literal false}.
	 *
	 * Use the {@literal spring.data.gemfire.pdx.persistent} property in {@literal application.properties}.
	 */
	boolean persistent() default PdxConfiguration.DEFAULT_PERSISTENT;

	/**
	 * Configures the object preference to {@link org.apache.geode.pdx.PdxInstance} type or {@link Object}.
	 *
	 * Default is {@literal false}.
	 *
	 * Use the {@literal spring.data.gemfire.pdx.read-serialized} property in {@literal application.properties}.
	 */
	boolean readSerialized() default PdxConfiguration.DEFAULT_READ_SERIALIZED;

	/**
	 * Configures the PDX serializer to be used by the cache to serialize object data.
	 *
	 * Use the {@literal spring.data.gemfire.pdx.serializer-bean-name} property in {@literal application.properties}.
	 */
	String serializerBeanName() default PdxConfiguration.DEFAULT_PDX_SERIALIZER_BEAN_NAME;

}
