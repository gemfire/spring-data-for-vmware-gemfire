/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.expiration;

import org.apache.geode.cache.CustomExpiry;
import org.apache.geode.cache.ExpirationAttributes;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.FactoryBean;

/**
 * The {@link ExpiringRegionFactoryBean} interface signifies {@link Region} {@link FactoryBean FactoryBeans}
 * that support Expiration configuration.  That is, {@link Region Region's} capable of expiring both entries
 * as well as the {@link Region} itself.
 *
 * Expiration policies may either be expressed as {@link ExpirationAttributes} or using a {@link CustomExpiry}
 * object enable the application developer to specify custom expiration criteria.
 *
 * Apache Geode and Pivotal GemFire supports both Idle Timeout (TTI) as well as Time-to-Live (TTL) expiration policies
 * at both the {@link Region} level as well as for entries.
 *
 * @author John Blum
 * @see CustomExpiry
 * @see ExpirationAttributes
 * @see Region
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public interface ExpiringRegionFactoryBean<K, V> {

	void setCustomEntryIdleTimeout(CustomExpiry<K, V> customEntryIdleTimeout);

	void setCustomEntryTimeToLive(CustomExpiry<K, V> customEntryTimeToLive);

	void setEntryIdleTimeout(ExpirationAttributes entryIdleTimeout);

	void setEntryTimeToLive(ExpirationAttributes entryTimeToLive);

	void setRegionIdleTimeout(ExpirationAttributes regionIdleTimeout);

	void setRegionTimeToLive(ExpirationAttributes regionTimeToLive);

}
