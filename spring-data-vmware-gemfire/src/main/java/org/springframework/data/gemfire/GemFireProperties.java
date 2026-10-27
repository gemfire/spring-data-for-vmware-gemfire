/*
 * Copyright 2022-2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.distributed.ConfigurationProperties;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;

import static org.springframework.data.gemfire.util.RuntimeExceptionFactory.newIllegalArgumentException;

/**
 * An Enum (enumeration) of Apache Geode {@literal gemfire.properties}.
 *
 * @author John Blum
 * @see ConfigurationProperties
 * @see <a href="https://geode.apache.org/docs/guide/114/reference/topics/gemfire_properties.html">Apache Geode Properties</a>
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public enum GemFireProperties {

	ARCHIVE_DISK_SPACE_LIMIT(ConfigurationProperties.ARCHIVE_DISK_SPACE_LIMIT, Integer.class, 0),
	ARCHIVE_FILE_SIZE_LIMIT(ConfigurationProperties.ARCHIVE_FILE_SIZE_LIMIT, Integer.class, 0),
	AVAILABILITY_ZONE(ConfigurationProperties.AVAILABILITY_ZONE, String.class),
	CACHE_XML_FILE(ConfigurationProperties.CACHE_XML_FILE, String.class),
	CONFLATE_EVENTS(ConfigurationProperties.CONFLATE_EVENTS, String.class, "server"),
	DELTA_PROPAGATION(ConfigurationProperties.DELTA_PROPAGATION, Boolean.class, true),
	DISTRIBUTED_SYSTEM_ID(ConfigurationProperties.DISTRIBUTED_SYSTEM_ID, Integer.class, -1),
	DURABLE_CLIENT_ID(ConfigurationProperties.DURABLE_CLIENT_ID, String.class),
	DURABLE_CLIENT_TIMEOUT(ConfigurationProperties.DURABLE_CLIENT_TIMEOUT, Long.class, 300L),
	ENABLE_TIME_STATISTICS(ConfigurationProperties.ENABLE_TIME_STATISTICS, Boolean.class, false),
	//GEODE_DISALLOW_INTERNAL_MESSAGES_WITHOUT_CREDENTIALS("geode.disallow-internal-messages-without-credentials", Boolean.class, false),
	GROUPS(ConfigurationProperties.GROUPS, String.class),
	LOCK_MEMORY(ConfigurationProperties.LOCK_MEMORY, Boolean.class, false),
	LOG_DISK_SPACE_LIMIT(ConfigurationProperties.LOG_DISK_SPACE_LIMIT, Integer.class, 0),
	LOG_FILE(ConfigurationProperties.LOG_FILE, String.class),
	LOG_FILE_SIZE_LIMIT(ConfigurationProperties.LOG_FILE_SIZE_LIMIT, Integer.class, 0),
	LOG_LEVEL(ConfigurationProperties.LOG_LEVEL, String.class, "config"),
	MEMBER_TIMEOUT(ConfigurationProperties.MEMBER_TIMEOUT, Long.class, 5000L),
	NAME(ConfigurationProperties.NAME, String.class),
	REDUNDANCY_ZONE(ConfigurationProperties.REDUNDANCY_ZONE, String.class),
	SECURITY_CLIENT_AUTH_INIT(ConfigurationProperties.SECURITY_CLIENT_AUTH_INIT, String.class),
	SECURITY_LOG_FILE(ConfigurationProperties.SECURITY_LOG_FILE, File.class),
	SECURITY_LOG_LEVEL(ConfigurationProperties.SECURITY_LOG_LEVEL, String.class, "config"),
	SERIALIZABLE_OBJECT_FILTER(ConfigurationProperties.SERIALIZABLE_OBJECT_FILTER, String.class, "!*"),
	SSL_CIPHERS(ConfigurationProperties.SSL_CIPHERS, String.class, "any"),
	SSL_CLIENT_PROTOCOLS(ConfigurationProperties.SSL_CLIENT_PROTOCOLS, String.class, ""),
	SSL_DEFAULT_ALIAS(ConfigurationProperties.SSL_DEFAULT_ALIAS, String.class),
	SSL_ENABLED_COMPONENTS(ConfigurationProperties.SSL_ENABLED_COMPONENTS, String.class, "locator,server"),
	SSL_ENDPOINT_IDENTIFICATION_ENABLED(ConfigurationProperties.SSL_ENDPOINT_IDENTIFICATION_ENABLED, Boolean.class, false),
	SSL_KEYSTORE(ConfigurationProperties.SSL_KEYSTORE, String.class),
	SSL_KEYSTORE_PASSWORD(ConfigurationProperties.SSL_KEYSTORE_PASSWORD, String.class),
	SSL_KEYSTORE_TYPE(ConfigurationProperties.SSL_KEYSTORE_TYPE, String.class, "JKS"),
	SSL_LOCATOR_ALIAS(ConfigurationProperties.SSL_LOCATOR_ALIAS, String.class),
	SSL_PARAMETER_EXTENSION(ConfigurationProperties.SSL_PARAMETER_EXTENSION, String.class),
	SSL_PROTOCOLS(ConfigurationProperties.SSL_PROTOCOLS, String.class, "any"),
	SSL_REQUIRE_AUTHENTICATION(ConfigurationProperties.SSL_REQUIRE_AUTHENTICATION, Boolean.class, true),
	SSL_SERVER_ALIAS(ConfigurationProperties.SSL_SERVER_ALIAS, String.class),
	SSL_TRUSTSTORE(ConfigurationProperties.SSL_TRUSTSTORE, String.class),
	SSL_TRUSTSTORE_PASSWORD(ConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, String.class),
	SSL_TRUSTSTORE_TYPE(ConfigurationProperties.SSL_TRUSTSTORE_TYPE, String.class, "JKS"),
	SSL_USE_DEFAULT_CONTEXT(ConfigurationProperties.SSL_USE_DEFAULT_CONTEXT, Boolean.class, false),
	STATISTIC_ARCHIVE_FILE(ConfigurationProperties.STATISTIC_ARCHIVE_FILE, File.class),
	STATISTIC_SAMPLE_RATE(ConfigurationProperties.STATISTIC_SAMPLE_RATE, Long.class, 1000),
	@Deprecated
	STATISTIC_SAMPLING_ENABLED(ConfigurationProperties.STATISTIC_SAMPLING_ENABLED, Boolean.class, false),
	THREAD_MONITOR_ENABLED(ConfigurationProperties.THREAD_MONITOR_ENABLED, Boolean.class, true),
	THREAD_MONITOR_INTERVAL_MS(ConfigurationProperties.THREAD_MONITOR_INTERVAL, Long.class, 0),
	THREAD_MONITOR_TIME_LIMIT(ConfigurationProperties.THREAD_MONITOR_TIME_LIMIT, Long.class, 30000),

	VALIDATE_SERIALIZABLE_OBJECTS(ConfigurationProperties.VALIDATE_SERIALIZABLE_OBJECTS, Boolean.class, false);

	/**
	 * Factory method used to get a {@link GemFireProperties} enumerated value for the given {@link String property name}.
	 *
	 * @param propertyName {@link String name} of the {@link GemFireProperties} enumerated value to return.
	 * @return a {@link GemFireProperties} enumerated value for the given {@link String property name}.
	 * @throws IllegalArgumentException if a {@link GemFireProperties} enumerated value cannot be found
	 * for the given {@link String property name}.
	 * @see #values()
	 */
	public static @NonNull GemFireProperties from(@Nullable String propertyName) {

		return Arrays.stream(values())
			.filter(it -> equals(it, propertyName))
			.findFirst()
			.orElseThrow(() -> newIllegalArgumentException("[%s] is not a valid Apache Geode property", propertyName));
	}

	private static boolean equals(@NonNull GemFireProperties property, @Nullable String propertyName) {
		return property != null && property.getName().equals(normalizePropertyName(propertyName));
	}

	/**
	 * Normalizes the given {@link String property name} by stripping off the {@literal gemfire.} prefix.
	 *
	 * @param propertyName {@link String name} of the property to normalize.
	 * @return a normalized {@link String name} for the given property.
	 */
	public static @Nullable String normalizePropertyName(@Nullable String propertyName) {

		String nullSafePropertyName = String.valueOf(propertyName).trim();

		boolean gemfireDotPrefixed = nullSafePropertyName.startsWith(GEMFIRE_PROPERTY_NAME_PREFIX);

		int index = nullSafePropertyName.lastIndexOf(".");

		return gemfireDotPrefixed && index > -1
			? nullSafePropertyName.substring(index + 1)
			: propertyName;
	}

	private static final Class<?> DEFAULT_PROPERTY_TYPE = Object.class;

	private static final Object DEFAULT_PROPERTY_VALUE = null;

	public static final String GEMFIRE_PROPERTY_NAME_PREFIX = "gemfire.";

	private final Class<?> propertyType;

	private final ConversionService conversionService;

	/** NOTE: A {@literal null} value represents an unset value */
	private final Object defaultValue;

	private final String propertyName;

	GemFireProperties(@NonNull String propertyName, @Nullable Class<?> propertyType) {
		this(propertyName, propertyType, null);
	}

	GemFireProperties(@NonNull String propertyName, @Nullable Class<?> propertyType, @Nullable Object defaultValue) {

		Assert.hasText(propertyName, "Property name is required");

		this.conversionService = DefaultConversionService.getSharedInstance();
		this.propertyName = propertyName;
		this.propertyType = propertyType;
		this.defaultValue = defaultValue;
	}

	/**
	 * Gets the {@link Object default value} for this Apache Geode property.
	 *
	 * @return the {@link Object default value} for this Apache Geode property.
	 * @see Object
	 */
	public @Nullable Object getDefaultValue() {
		return this.defaultValue != null ? this.defaultValue : DEFAULT_PROPERTY_VALUE;
	}

	/**
	 * Gets the {@link Object default value} for this Apache Geode property as a {@link String}.
	 *
	 * @return the {@link Object default value} for this Apache Geode property as a {@link String}.
	 * If this property's {@link Object default value} is {@literal null}, then this method returns
	 * the {@literal "null"} {@link String}.
	 * @see #getDefaultValue()
	 * @see String
	 */
	public @NonNull String getDefaultValueAsString() {
		return String.valueOf(getDefaultValue());
	}

	/**
	 * Gets the {@link Object default value} for this Apache Geode property converted to
	 * the given, required {@link Class type}.
	 *
	 * @param <T> Desired {@link Class type} for this Apache Geode property's {@link Object default value}.
	 * @param type {@link Class type} to convert the {@link Object default value} to.
	 * @return the {@link Object default value} for this Apache Geode property converted into an instance of
	 * the given, required {@link Class type}.
	 * @throws IllegalArgumentException if this Apache Geode property's {@link Object default value}
	 * cannot be converted to an instance of the given, required {@link Class type}
	 * or the given {@link Class type} is {@literal null}.
	 * @see #getDefaultValue()
	 * @see #getType()
	 */
	public @NonNull <T> T getDefaultValueAsType(@NonNull Class<T> type) {

		Assert.notNull(type, "Target type must not be null");

		Object defaultValue = getDefaultValue();
		Class<?> defaultValueType = resolveDefaultValueType();

		if (type.isInstance(defaultValue)) {
			return type.cast(defaultValue);
		}
		else if (this.conversionService.canConvert(defaultValueType, type)) {
			return this.conversionService.convert(defaultValue, type);
		}

		throw newIllegalArgumentException("Cannot convert value [%s] from type [%s] to type [%s]",
			defaultValue, defaultValueType, type);
	}

	private @NonNull Class<?> resolveDefaultValueType() {

		Class<?> propertyType = getType();
		Object defaultValue = getDefaultValue();

		return defaultValue != null ? defaultValue.getClass()
			: propertyType != null ? propertyType
			: Object.class;
	}

	/**
	 * Gets the {@link String name} of this Apache Geode property.
	 *
	 * @return the {@link String name} of this Apache Geode property.
	 * @see String
	 */
	public @NonNull String getName() {
		return this.propertyName;
	}

	/**
	 * Gets the declared {@link Class type} of this Apache Geode property.
	 *
	 * @return the declared {@link Class type} of this Apache Geode property.
	 * @see Class
	 */
	public @NonNull Class<?> getType() {
		return this.propertyType != null ? this.propertyType : DEFAULT_PROPERTY_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public @NonNull String toString() {
		return getName();
	}
}
