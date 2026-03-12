/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Created GudConfigurationProperties constants for GUD API
 */

package org.springframework.data.gemfire.gud.api;

/**
 * GUD API abstraction for GemFire ConfigurationProperties.
 * Contains property name constants for GemFire configuration.
 */
public interface GudConfigurationProperties {

    String NAME = "name";
    String LOG_LEVEL = "log-level";
    String LOG_FILE = "log-file";
    String LOG_FILE_SIZE_LIMIT = "log-file-size-limit";
    String LOG_DISK_SPACE_LIMIT = "log-disk-space-limit";
    String LOCATORS = "locators";
    String START_LOCATOR = "start-locator";
    String BIND_ADDRESS = "bind-address";
    String SERVER_BIND_ADDRESS = "server-bind-address";
    String TCP_PORT = "tcp-port";
    String MCAST_PORT = "mcast-port";
    String MCAST_ADDRESS = "mcast-address";
    String CACHE_XML_FILE = "cache-xml-file";
    String ACK_WAIT_THRESHOLD = "ack-wait-threshold";
    String ACK_SEVERE_ALERT_THRESHOLD = "ack-severe-alert-threshold";
    String ARCHIVE_FILE_SIZE_LIMIT = "archive-file-size-limit";
    String ARCHIVE_DISK_SPACE_LIMIT = "archive-disk-space-limit";
    String STATISTIC_ARCHIVE_FILE = "statistic-archive-file";
    String STATISTIC_SAMPLING_ENABLED = "statistic-sampling-enabled";
    String STATISTIC_SAMPLE_RATE = "statistic-sample-rate";
    String ENABLE_TIME_STATISTICS = "enable-time-statistics";
    String DURABLE_CLIENT_ID = "durable-client-id";
    String DURABLE_CLIENT_TIMEOUT = "durable-client-timeout";
    String CONSERVE_SOCKETS = "conserve-sockets";
    String SOCKET_LEASE_TIME = "socket-lease-time";
    String SOCKET_BUFFER_SIZE = "socket-buffer-size";
    String CONFLATE_EVENTS = "conflate-events";
    String DISTRIBUTED_SYSTEM_ID = "distributed-system-id";
    String REMOTE_LOCATORS = "remote-locators";
    String GROUPS = "groups";
    String REDUNDANCY_ZONE = "redundancy-zone";
    String ENFORCE_UNIQUE_HOST = "enforce-unique-host";
    String MEMBER_TIMEOUT = "member-timeout";
    String MEMBERSHIP_PORT_RANGE = "membership-port-range";
    String LOCATOR_WAIT_TIME = "locator-wait-time";
    String MAX_WAIT_TIME_RECONNECT = "max-wait-time-reconnect";
    String MAX_NUM_RECONNECT_TRIES = "max-num-reconnect-tries";
    String DISABLE_AUTO_RECONNECT = "disable-auto-reconnect";
    String DISABLE_TCP = "disable-tcp";
    String DEPLOY_WORKING_DIR = "deploy-working-dir";
    String USER_COMMAND_PACKAGES = "user-command-packages";
    String ENABLE_CLUSTER_CONFIGURATION = "enable-cluster-configuration";
    String USE_CLUSTER_CONFIGURATION = "use-cluster-configuration";
    String LOAD_CLUSTER_CONFIGURATION_FROM_DIR = "load-cluster-configuration-from-dir";
    String CLUSTER_CONFIGURATION_DIR = "cluster-configuration-dir";
    String LOCK_MEMORY = "lock-memory";
    String DELTA_PROPAGATION = "delta-propagation";
    String REMOVE_UNRESPONSIVE_CLIENT = "remove-unresponsive-client";
    String DISTRIBUTED_TRANSACTIONS = "distributed-transactions";
    String SERIALIZABLE_OBJECT_FILTER = "serializable-object-filter";
    String VALIDATE_SERIALIZABLE_OBJECTS = "validate-serializable-objects";
    String THREAD_MONITOR_ENABLED = "thread-monitor-enabled";
    String THREAD_MONITOR_INTERVAL = "thread-monitor-interval-ms";
    String THREAD_MONITOR_TIME_LIMIT = "thread-monitor-time-limit-ms";
    String ENABLE_NETWORK_PARTITION_DETECTION = "enable-network-partition-detection";
    
    // SSL Properties
    String SSL_ENABLED_COMPONENTS = "ssl-enabled-components";
    String SSL_PROTOCOLS = "ssl-protocols";
    String SSL_CIPHERS = "ssl-ciphers";
    String SSL_REQUIRE_AUTHENTICATION = "ssl-require-authentication";
    String SSL_KEYSTORE = "ssl-keystore";
    String SSL_KEYSTORE_PASSWORD = "ssl-keystore-password";
    String SSL_KEYSTORE_TYPE = "ssl-keystore-type";
    String SSL_TRUSTSTORE = "ssl-truststore";
    String SSL_TRUSTSTORE_PASSWORD = "ssl-truststore-password";
    String SSL_TRUSTSTORE_TYPE = "ssl-truststore-type";
    String SSL_DEFAULT_ALIAS = "ssl-default-alias";
    String SSL_CLUSTER_ALIAS = "ssl-cluster-alias";
    String SSL_GATEWAY_ALIAS = "ssl-gateway-alias";
    String SSL_SERVER_ALIAS = "ssl-server-alias";
    String SSL_LOCATOR_ALIAS = "ssl-locator-alias";
    String SSL_WEB_ALIAS = "ssl-web-alias";
    String SSL_ENDPOINT_IDENTIFICATION_ENABLED = "ssl-endpoint-identification-enabled";
    String SSL_USE_DEFAULT_CONTEXT = "ssl-use-default-context";
    String SSL_PARAMETER_EXTENSION = "ssl-parameter-extension";
    String SSL_CLIENT_PROTOCOLS = "ssl-client-protocols";
    String SSL_SERVER_PROTOCOLS = "ssl-server-protocols";
    String SSL_WEB_SERVICE_REQUIRE_AUTHENTICATION = "ssl-web-service-require-authentication";
    
    // Security Properties
    String SECURITY_MANAGER = "security-manager";
    String SECURITY_CLIENT_AUTH_INIT = "security-client-auth-init";
    String SECURITY_PEER_AUTH_INIT = "security-peer-auth-init";
    String SECURITY_POST_PROCESSOR = "security-post-processor";
    String SECURITY_LOG_FILE = "security-log-file";
    String SECURITY_LOG_LEVEL = "security-log-level";
    String SECURITY_PEER_VERIFY_MEMBER_TIMEOUT = "security-peer-verify-member-timeout";
    String SECURITY_SHIRO_INIT = "security-shiro-init";
    String SECURITY_UDP_DHALGO = "security-udp-dhalgo";
    String SECURITY_AUTH_TOKEN_ENABLED_COMPONENTS = "security-auth-token-enabled-components";
    
    // UDP Properties
    String UDP_SEND_BUFFER_SIZE = "udp-send-buffer-size";
    String UDP_RECV_BUFFER_SIZE = "udp-recv-buffer-size";
    String UDP_FRAGMENT_SIZE = "udp-fragment-size";

    // Rest/HTTP Properties
    String HTTP_SERVICE_PORT = "http-service-port";
    String HTTP_SERVICE_BIND_ADDRESS = "http-service-bind-address";
    String START_DEV_REST_API = "start-dev-rest-api";
    String REST_JSON_STORAGE_FORMAT = "rest-json-storage-format";
}
