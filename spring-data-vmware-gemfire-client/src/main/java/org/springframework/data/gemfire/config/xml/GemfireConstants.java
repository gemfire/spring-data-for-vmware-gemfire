/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.config.xml;

/**
 * The GemfireConstants class define constants for Spring GemFire component bean names.
 *
 * @author David Turanski
 * @author John Blum
 */
@SuppressWarnings("unused")
public interface GemfireConstants {

	String DEFAULT_GEMFIRE_CACHE_NAME = "gemfireCache";
	String DEFAULT_GEMFIRE_INDEX_DEFINITION_QUERY_SERVICE = "gemfireIndexDefinitionQueryService";
	String DEFAULT_GEMFIRE_FUNCTION_SERVICE_NAME = "gemfireFunctionService";
	String DEFAULT_GEMFIRE_POOL_NAME = "gemfirePool";
	String DEFAULT_GEMFIRE_TRANSACTION_MANAGER_NAME = "gemfireTransactionManager";

}
