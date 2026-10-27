/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.support;

/**
 * Features exposed by Spring Data for VMware GemFire client applications.
 *
 * @author John Blum
 * @since 1.7.0
 */
@SuppressWarnings("unused")
public enum GemfireFeature {

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
	PERSISTENCE,
	QUERY,
	REGISTER_INTEREST,
	SECURITY,
	SERIALIZATION,
	STATISTICS,
	TRANSACTIONS,
	TUNING

}
