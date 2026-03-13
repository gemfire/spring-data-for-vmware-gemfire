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

package org.springframework.data.gemfire;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.gemfire.gud.api.GudConfigurationProperties;
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
 * @see GudConfigurationProperties
 * @see <a href="https://geode.apache.org/docs/guide/114/reference/topics/gemfire_properties.html">Apache Geode Properties</a>
 * @since 2.3.0
 */
@SuppressWarnings("unused")
public enum GemFireProperties {

	ACK_SEVERE_ALERT_THRESHOLD(GudConfigurationProperties.ACK_SEVERE_ALERT_THRESHOLD, Long.class, 0),
	ACK_WAIT_THRESHOLD(GudConfigurationProperties.ACK_WAIT_THRESHOLD, Long.class, 15),
	ARCHIVE_DISK_SPACE_LIMIT(GudConfigurationProperties.ARCHIVE_DISK_SPACE_LIMIT, Integer.class, 0),
	ARCHIVE_FILE_SIZE_LIMIT(GudConfigurationProperties.ARCHIVE_FILE_SIZE_LIMIT, Integer.class, 0),
	BIND_ADDRESS(GudConfigurationProperties.BIND_ADDRESS, String.class),
	CACHE_XML_FILE(GudConfigurationProperties.CACHE_XML_FILE, String.class),
	CONFLATE_EVENTS(GudConfigurationProperties.CONFLATE_EVENTS, String.class, "server"),
	CONSERVE_SOCKETS(GudConfigurationProperties.CONSERVE_SOCKETS, Boolean.class, true),
	DELTA_PROPAGATION(GudConfigurationProperties.DELTA_PROPAGATION, Boolean.class, true),
	DEPLOY_WORKING_DIRECTORY(GudConfigurationProperties.DEPLOY_WORKING_DIR, File.class, new File(".")),
	DISABLE_AUTO_RECONNECT(GudConfigurationProperties.DISABLE_AUTO_RECONNECT, Boolean.class, false),
	DISABLE_TCP(GudConfigurationProperties.DISABLE_TCP, Boolean.class, false),
	DISTRIBUTED_SYSTEM_ID(GudConfigurationProperties.DISTRIBUTED_SYSTEM_ID, Integer.class, -1),
	@Deprecated
	DISTRIBUTED_TRANSACTIONS(GudConfigurationProperties.DISTRIBUTED_TRANSACTIONS, Boolean.class, false),
	DURABLE_CLIENT_ID(GudConfigurationProperties.DURABLE_CLIENT_ID, String.class),
	DURABLE_CLIENT_TIMEOUT(GudConfigurationProperties.DURABLE_CLIENT_TIMEOUT, Long.class, 300L),
	ENABLE_CLUSTER_CONFIGURATION(GudConfigurationProperties.ENABLE_CLUSTER_CONFIGURATION, Boolean.class, true),
	ENABLE_NETWORK_PARTITION_DETECTION(GudConfigurationProperties.ENABLE_NETWORK_PARTITION_DETECTION, Boolean.class, true),
	ENABLE_TIME_STATISTICS(GudConfigurationProperties.ENABLE_TIME_STATISTICS, Boolean.class, false),
	ENFORCE_UNIQUE_HOST(GudConfigurationProperties.ENFORCE_UNIQUE_HOST, Boolean.class, false),
	//GEODE_DISALLOW_INTERNAL_MESSAGES_WITHOUT_CREDENTIALS("geode.disallow-internal-messages-without-credentials", Boolean.class, false),
	GROUPS(GudConfigurationProperties.GROUPS, String.class),
	LOAD_CLUSTER_CONFIGURATION_FROM_DIR(GudConfigurationProperties.LOAD_CLUSTER_CONFIGURATION_FROM_DIR, Boolean.class, false),
	LOCATOR_WAIT_TIME(GudConfigurationProperties.LOCATOR_WAIT_TIME, Long.class, 0),
	LOCATORS(GudConfigurationProperties.LOCATORS, String.class),
	LOCK_MEMORY(GudConfigurationProperties.LOCK_MEMORY, Boolean.class, false),
	LOG_DISK_SPACE_LIMIT(GudConfigurationProperties.LOG_DISK_SPACE_LIMIT, Integer.class, 0),
	LOG_FILE(GudConfigurationProperties.LOG_FILE, String.class),
	LOG_FILE_SIZE_LIMIT(GudConfigurationProperties.LOG_FILE_SIZE_LIMIT, Integer.class, 0),
	LOG_LEVEL(GudConfigurationProperties.LOG_LEVEL, String.class, "config"),
	MAX_NUM_RECONNECT_TRIES(GudConfigurationProperties.MAX_NUM_RECONNECT_TRIES, Integer.class, 3),
	MAX_WAIT_TIME_RECONNECT(GudConfigurationProperties.MAX_WAIT_TIME_RECONNECT, Long.class, 60000),
	MEMBER_TIMEOUT(GudConfigurationProperties.MEMBER_TIMEOUT, Long.class, 5000L),
	MEMBERSHIP_PORT_RANGE(GudConfigurationProperties.MEMBERSHIP_PORT_RANGE, String.class, "41000-61000"),
	NAME(GudConfigurationProperties.NAME, String.class),
	REDUNDANCY_ZONE(GudConfigurationProperties.REDUNDANCY_ZONE, String.class),
	REMOTE_LOCATORS(GudConfigurationProperties.REMOTE_LOCATORS, String.class),
	REMOVE_UNRESPONSIVE_CLIENT(GudConfigurationProperties.REMOVE_UNRESPONSIVE_CLIENT, Boolean.class, false),
	REST_JSON_STORAGE_FORMAT(GudConfigurationProperties.REST_JSON_STORAGE_FORMAT, String.class, "BSON"),
	SECURITY_AUTH_TOKEN_ENABLED_COMPONENTS(GudConfigurationProperties.SECURITY_AUTH_TOKEN_ENABLED_COMPONENTS, String[].class),
	SECURITY_CLIENT_AUTH_INIT(GudConfigurationProperties.SECURITY_CLIENT_AUTH_INIT, String.class),
	SECURITY_LOG_FILE(GudConfigurationProperties.SECURITY_LOG_FILE, File.class),
	SECURITY_LOG_LEVEL(GudConfigurationProperties.SECURITY_LOG_LEVEL, String.class, "config"),
	SECURITY_MANAGER(GudConfigurationProperties.SECURITY_MANAGER, String.class),
	SECURITY_PEER_AUTH_INIT(GudConfigurationProperties.SECURITY_PEER_AUTH_INIT, String.class),
	SECURITY_PEER_VERIFY_MEMBER_TIMEOUT(GudConfigurationProperties.SECURITY_PEER_VERIFY_MEMBER_TIMEOUT, Long.class, 1000L),
	SECURITY_POST_PROCESSOR(GudConfigurationProperties.SECURITY_POST_PROCESSOR, String.class),
	@Deprecated
	SECURITY_SHIRO_INIT(GudConfigurationProperties.SECURITY_SHIRO_INIT, String.class),
	SECURITY_UDP_DHALO(GudConfigurationProperties.SECURITY_UDP_DHALGO, String.class),
	SERIALIZABLE_OBJECT_FILTER(GudConfigurationProperties.SERIALIZABLE_OBJECT_FILTER, String.class, "!*"),
	SERVER_BIND_ADDRESS(GudConfigurationProperties.SERVER_BIND_ADDRESS, String.class),
	SOCKET_BUFFER_SIZE(GudConfigurationProperties.SOCKET_BUFFER_SIZE, Integer.class, 32768),
	SOCKET_LEASE_TIME(GudConfigurationProperties.SOCKET_LEASE_TIME, Long.class, 60000L),
	SSL_CIPHERS(GudConfigurationProperties.SSL_CIPHERS, String.class, "any"),
	SSL_CLIENT_PROTOCOLS(GudConfigurationProperties.SSL_CLIENT_PROTOCOLS, String.class, ""),
	SSL_CLUSTER_ALIAS(GudConfigurationProperties.SSL_CLUSTER_ALIAS, String.class),
	SSL_DEFAULT_ALIAS(GudConfigurationProperties.SSL_DEFAULT_ALIAS, String.class),
	SSL_ENABLED_COMPONENTS(GudConfigurationProperties.SSL_ENABLED_COMPONENTS, String.class, "all"),
	SSL_ENDPOINT_IDENTIFICATION_ENABLED(GudConfigurationProperties.SSL_ENDPOINT_IDENTIFICATION_ENABLED, Boolean.class, false),
	SSL_GATEWAY_ALIAS(GudConfigurationProperties.SSL_GATEWAY_ALIAS, String.class),
	SSL_KEYSTORE(GudConfigurationProperties.SSL_KEYSTORE, String.class),
	SSL_KEYSTORE_PASSWORD(GudConfigurationProperties.SSL_KEYSTORE_PASSWORD, String.class),
	SSL_KEYSTORE_TYPE(GudConfigurationProperties.SSL_KEYSTORE_TYPE, String.class, "JKS"),
	SSL_LOCATOR_ALIAS(GudConfigurationProperties.SSL_LOCATOR_ALIAS, String.class),
	SSL_PARAMETER_EXTENSION(GudConfigurationProperties.SSL_PARAMETER_EXTENSION, String.class),
	SSL_PROTOCOLS(GudConfigurationProperties.SSL_PROTOCOLS, String.class, "any"),
	SSL_REQUIRE_AUTHENTICATION(GudConfigurationProperties.SSL_REQUIRE_AUTHENTICATION, Boolean.class, true),
	SSL_SERVER_ALIAS(GudConfigurationProperties.SSL_SERVER_ALIAS, String.class),
	SSL_SERVER_PROTOCOLS(GudConfigurationProperties.SSL_SERVER_PROTOCOLS, String.class, ""),
	SSL_TRUSTSTORE(GudConfigurationProperties.SSL_TRUSTSTORE, String.class),
	SSL_TRUSTSTORE_PASSWORD(GudConfigurationProperties.SSL_TRUSTSTORE_PASSWORD, String.class),
	SSL_TRUSTSTORE_TYPE(GudConfigurationProperties.SSL_TRUSTSTORE_TYPE, String.class, "JKS"),
	SSL_USE_DEFAULT_CONTEXT(GudConfigurationProperties.SSL_USE_DEFAULT_CONTEXT, Boolean.class, false),
	SSL_WEB_ALIAS(GudConfigurationProperties.SSL_WEB_ALIAS, String.class),
	SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION(GudConfigurationProperties.SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION, Boolean.class, false),
	START_LOCATOR(GudConfigurationProperties.START_LOCATOR, Boolean.class),
	STATISTIC_ARCHIVE_FILE(GudConfigurationProperties.STATISTIC_ARCHIVE_FILE, File.class),
	STATISTIC_SAMPLE_RATE(GudConfigurationProperties.STATISTIC_SAMPLE_RATE, Long.class, 1000),
	@Deprecated
	STATISTIC_SAMPLING_ENABLED(GudConfigurationProperties.STATISTIC_SAMPLING_ENABLED, Boolean.class, false),
	TCP_PORT(GudConfigurationProperties.TCP_PORT, Integer.class, 0),
	THREAD_MONITOR_ENABLED(GudConfigurationProperties.THREAD_MONITOR_ENABLED, Boolean.class, true),
	THREAD_MONITOR_INTERVAL_MS(GudConfigurationProperties.THREAD_MONITOR_INTERVAL, Long.class, 0),
	THREAD_MONITOR_TIME_LIMIT(GudConfigurationProperties.THREAD_MONITOR_TIME_LIMIT, Long.class, 30000),
	//TOMBSTONE_GC_THRESHOLD("tombstone-gc-threshold", Integer.class, 100000),
	UDP_FRAGMENT_SIZE(GudConfigurationProperties.UDP_FRAGMENT_SIZE, Integer.class, 60000),
	UDP_RECV_BUFFER_SIZE(GudConfigurationProperties.UDP_RECV_BUFFER_SIZE, Integer.class, 1048576),
	UPD_SEND_BUFFER_SIZE(GudConfigurationProperties.UDP_SEND_BUFFER_SIZE, Integer.class, 65535),

	USE_CLUSTER_CONFIGURATION(GudConfigurationProperties.USE_CLUSTER_CONFIGURATION, Boolean.class, true),
	USER_COMMAND_PACKAGES(GudConfigurationProperties.USER_COMMAND_PACKAGES, String.class),
	VALIDATE_SERIALIZABLE_OBJECTS(GudConfigurationProperties.VALIDATE_SERIALIZABLE_OBJECTS, Boolean.class, false);

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
