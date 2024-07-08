/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.support;

/**
 * The GemfireFeature enum is an enumeration of features available in Apache Geode and Pivotal GemFire combined.
 *
 * @author John Blum
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public enum GemfireFeature {

	BACKUP,
	CACHING,
	CLIENT_SERVER,
	COMPRESSION,
	CONFIGURATION,
	CONSISTENCY,
	CONTINUOUS_QUERY,
	DELTA_PROPAGATION,
	EVENT_HANDLING,
	FUNCTIONS,
	HTTP_SESSION_MANAGEMENT,
	JSON,
	LOGGING,
	MANAGEMENT_MONITORING,
	NETWORK_PARTITIONING,
	OFF_HEAP,
	PARTITIONING,
	PEER_TO_PEER,
	PERSISTENCE,
	QUERY,
	REGISTER_INTEREST,
	REPLICATION,
	SECURITY,
	SERIALIZATION,
	STATISTICS,
	TRANSACTIONS,
	TUNING

}
