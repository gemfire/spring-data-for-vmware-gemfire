/*
 * Copyright 2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.vmware.gemfire.driver.spi;

/**
 * Native-free property names retained for client cache configuration.
 *
 * <p>Use these names with {@link CacheConfiguration}. Names do not guarantee support for every
 * value: validation and runtime behavior belong to the selected driver. In particular, a mock
 * configuration snapshot does not imply TLS, authentication or monitoring is active.
 *
 * <p>This is a client subset, not the complete native configuration surface. Prefix constants
 * identify namespaces, not standalone settings. The security namespace does not make server
 * security configuration part of the client contract.
 */
public final class ClientConfigurationProperties {

	private ClientConfigurationProperties() {
	}

	// Client identity and runtime configuration.
	public static final String AVAILABILITY_ZONE = "availability-zone";
	public static final String CACHE_XML_FILE = "cache-xml-file";
	public static final String CONFLATE_EVENTS = "conflate-events";
	public static final String DELTA_PROPAGATION = "delta-propagation";
	public static final String DISTRIBUTED_SYSTEM_ID = "distributed-system-id";
	public static final String DURABLE_CLIENT_ID = "durable-client-id";
	public static final String DURABLE_CLIENT_TIMEOUT = "durable-client-timeout";
	public static final String GROUPS = "groups";
	/** Must be empty for a client cache; configure discovery through client pool locators. */
	public static final String LOCATORS = "locators";
	public static final String LOCK_MEMORY = "lock-memory";
	public static final String NAME = "name";
	public static final String REDUNDANCY_ZONE = "redundancy-zone";
	public static final String ROLES = "roles";
	public static final String SERIALIZABLE_OBJECT_FILTER = "serializable-object-filter";
	public static final String VALIDATE_SERIALIZABLE_OBJECTS = "validate-serializable-objects";

	// Client logging, statistics and thread monitoring.
	public static final String ARCHIVE_DISK_SPACE_LIMIT = "archive-disk-space-limit";
	public static final String ARCHIVE_FILE_SIZE_LIMIT = "archive-file-size-limit";
	public static final String ENABLE_TIME_STATISTICS = "enable-time-statistics";
	public static final String LOG_DISK_SPACE_LIMIT = "log-disk-space-limit";
	public static final String LOG_FILE = "log-file";
	public static final String LOG_FILE_SIZE_LIMIT = "log-file-size-limit";
	public static final String LOG_LEVEL = "log-level";
	public static final String MEMBER_TIMEOUT = "member-timeout";
	public static final String STATISTIC_ARCHIVE_FILE = "statistic-archive-file";
	public static final String STATISTIC_SAMPLE_RATE = "statistic-sample-rate";
	public static final String STATISTIC_SAMPLING_ENABLED = "statistic-sampling-enabled";
	public static final String THREAD_MONITOR_ENABLED = "thread-monitor-enabled";
	public static final String THREAD_MONITOR_INTERVAL = "thread-monitor-interval-ms";
	public static final String THREAD_MONITOR_TIME_LIMIT = "thread-monitor-time-limit-ms";

	// Client authentication and security diagnostics.
	public static final String SECURITY_CLIENT_AUTH_INIT = "security-client-auth-init";
	public static final String SECURITY_CLIENT_DHALGO = "security-client-dhalgo";
	public static final String SECURITY_LOG_FILE = "security-log-file";
	public static final String SECURITY_LOG_LEVEL = "security-log-level";
	/** Client security property namespace; not a standalone setting. */
	public static final String SECURITY_PREFIX = "security-";

	// Client TLS, including legacy locator and server transport settings.
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_CIPHERS = "cluster-ssl-ciphers";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_ENABLED = "cluster-ssl-enabled";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_KEYSTORE = "cluster-ssl-keystore";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_KEYSTORE_PASSWORD = "cluster-ssl-keystore-password";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_KEYSTORE_TYPE = "cluster-ssl-keystore-type";
	/** Legacy TLS property namespace; not a standalone setting. */
	public static final String CLUSTER_SSL_PREFIX = "cluster-ssl";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_PROTOCOLS = "cluster-ssl-protocols";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_REQUIRE_AUTHENTICATION = "cluster-ssl-require-authentication";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_TRUSTSTORE = "cluster-ssl-truststore";
	/** Legacy client-to-locator TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String CLUSTER_SSL_TRUSTSTORE_PASSWORD = "cluster-ssl-truststore-password";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_CIPHERS = "server-ssl-ciphers";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_ENABLED = "server-ssl-enabled";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_KEYSTORE = "server-ssl-keystore";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_KEYSTORE_PASSWORD = "server-ssl-keystore-password";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_KEYSTORE_TYPE = "server-ssl-keystore-type";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_PROTOCOLS = "server-ssl-protocols";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_REQUIRE_AUTHENTICATION = "server-ssl-require-authentication";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_TRUSTSTORE = "server-ssl-truststore";
	/** Legacy client-to-server TLS setting when SSL_ENABLED_COMPONENTS is empty. */
	public static final String SERVER_SSL_TRUSTSTORE_PASSWORD = "server-ssl-truststore-password";
	public static final String SSL_CIPHERS = "ssl-ciphers";
	public static final String SSL_CLIENT_PROTOCOLS = "ssl-client-protocols";
	public static final String SSL_DEFAULT_ALIAS = "ssl-default-alias";
	public static final String SSL_ENABLED_COMPONENTS = "ssl-enabled-components";
	public static final String SSL_ENDPOINT_IDENTIFICATION_ENABLED = "ssl-endpoint-identification-enabled";
	public static final String SSL_KEYSTORE = "ssl-keystore";
	public static final String SSL_KEYSTORE_PASSWORD = "ssl-keystore-password";
	public static final String SSL_KEYSTORE_TYPE = "ssl-keystore-type";
	public static final String SSL_LOCATOR_ALIAS = "ssl-locator-alias";
	public static final String SSL_PARAMETER_EXTENSION = "ssl-parameter-extension";
	public static final String SSL_PROTOCOLS = "ssl-protocols";
	public static final String SSL_REQUIRE_AUTHENTICATION = "ssl-require-authentication";
	public static final String SSL_SERVER_ALIAS = "ssl-server-alias";
	public static final String SSL_TRUSTSTORE = "ssl-truststore";
	public static final String SSL_TRUSTSTORE_PASSWORD = "ssl-truststore-password";
	public static final String SSL_TRUSTSTORE_TYPE = "ssl-truststore-type";
	public static final String SSL_USE_DEFAULT_CONTEXT = "ssl-use-default-context";
}
