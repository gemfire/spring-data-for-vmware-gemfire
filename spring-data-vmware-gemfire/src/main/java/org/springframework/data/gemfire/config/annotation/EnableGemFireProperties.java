/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.context.annotation.Import;

/**
 * The {@link EnableGemFireProperties} annotation marks a Spring {@link org.springframework.context.annotation.Configuration @Configuration}
 * annotated class to configure GemFire/Geode System properties at runtime during [Spring Boot] application startup.
 *
 * @author John Blum
 * @see GemFirePropertiesConfiguration
 * @see <a href="https://geode.apache.org/docs/guide/113/reference/topics/gemfire_properties.html">Geode Properties</a>
 * @since 1.9.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(GemFirePropertiesConfiguration.class)
@UsesGemFireProperties
@SuppressWarnings("unused")
public @interface EnableGemFireProperties {

	/**
	 * Declarative initialization file for the client cache.
	 *
	 * Defaults to unset.
	 */
	String cacheXmlFile() default GemFirePropertiesConfiguration.DEFAULT_CACHE_XML_FILE;

	/**
	 * Used only by clients in a client/server installation. This is a client-side property
	 * that is passed to the server. Affects subscription queue conflation in this client’s
	 * servers. Specifies whether to conflate (true setting), not conflate (false), or to
	 * use the server’s conflation setting (server).
	 *
	 * Defaults to {@literal server}.
	 */
	String conflateEvents() default GemFirePropertiesConfiguration.DEFAULT_CONFLATE_EVENTS;

	/**
	 * Enables delta extraction for client entry updates. Sending a delta instead of the full value
	 * also depends on the server connection's delta capability.
	 *
	 * Default to {@literal true}.
	 */
	boolean deltaPropagation() default GemFirePropertiesConfiguration.DEFAULT_DELTA_PROPAGATION;

	/**
	 * Identifier supplied to a configured SSL parameter extension's initialization context on the client.
	 * Client PDX serialization does not require this setting.
	 *
	 * -1 means no setting. Valid values are integers in the range -1…255.
	 *
	 * Defaults to {@literal -1}.
	 */
	int distributedSystemId() default GemFirePropertiesConfiguration.DEFAULT_DISTRIBUTED_SYSTEM_ID;

	/**
	 * Defines group names included in the client's distributed-member identity.
	 * Configure the pool's server group separately to select servers for client operations.
	 *
	 * Default to unset.
	 */
	String[] groups() default {};

	/**
	 * Requests memory locking for the client JVM to prevent paging to disk. The native implementation
	 * requires Linux memory-locking support and sufficient available memory and operating-system privileges.
	 *
	 * Defaults to {@literal false}.
	 */
	boolean lockMemory() default GemFirePropertiesConfiguration.DEFAULT_LOCK_MEMORY;

	/**
	 * Configures {@literal member-timeout} in milliseconds. On a client, the native thread monitor uses
	 * half this value as its default execution-time threshold for backing off monitoring cycles.
	 * This applies when thread monitoring is enabled and its execution threshold has not been overridden.
	 * Client connection, read and server failure detection settings are configured separately on the pool.
	 *
	 * Valid values are in the range 10..600000 for the supported GemFire 10.4 driver.
	 *
	 * Defaults to {@literal 5000} milliseconds. The default is omitted from the generated properties,
	 * allowing the native driver to supply its matching default.
	 */
	long memberTimeout() default GemFirePropertiesConfiguration.DEFAULT_MEMBER_TIMEOUT;

	/**
	 * Configures the name included in the client's distributed-member identity.
	 *
	 * Defaults to unset.
	 */
	String name() default GemFirePropertiesConfiguration.DEFAULT_NAME;

	/**
	 * Class-name patterns allowed during client deserialization when
	 * {@link #validateSerializableObjects()} is set to {@literal true}.
	 *
	 * Patterns follow {@link java.io.ObjectInputFilter.Config#createFilter(String)} syntax.
	 *
	 * Defaults to unset.
	 */
	String[] serializableObjectFilter() default {};

	/**
	 * Enables class validation during client deserialization. The native implementation permits its
	 * sanctioned classes and application classes allowed by {@link #serializableObjectFilter()}.
	 *
	 * Defaults to {@literal false}.
	 */
	boolean validateSerializableObjects() default GemFirePropertiesConfiguration.DEFAULT_VALIDATE_SERIALIZABLE_OBJECTS;

}
