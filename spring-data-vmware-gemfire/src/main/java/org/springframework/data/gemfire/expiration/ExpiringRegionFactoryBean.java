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

package org.springframework.data.gemfire.expiration;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.gemfire.gud.api.GudCustomExpiry;
import org.springframework.data.gemfire.gud.api.GudExpirationAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link ExpiringRegionFactoryBean} interface signifies {@link GudRegion} {@link FactoryBean FactoryBeans}
 * that support Expiration configuration.  That is, {@link GudRegion Region's} capable of expiring both entries
 * as well as the {@link GudRegion} itself.
 *
 * Expiration policies may either be expressed as {@link GudExpirationAttributes} or using a {@link GudCustomExpiry}
 * object enable the application developer to specify custom expiration criteria.
 *
 * Apache Geode and Pivotal GemFire supports both Idle Timeout (TTI) as well as Time-to-Live (TTL) expiration policies
 * at both the {@link GudRegion} level as well as for entries.
 *
 * @author John Blum
 * @see GudCustomExpiry
 * @see GudExpirationAttributes
 * @see GudRegion
 * @since 2.1.0
 */
@SuppressWarnings("unused")
public interface ExpiringRegionFactoryBean<K, V> {

	void setCustomEntryIdleTimeout(GudCustomExpiry<K, V> customEntryIdleTimeout);

	void setCustomEntryTimeToLive(GudCustomExpiry<K, V> customEntryTimeToLive);

	void setEntryIdleTimeout(GudExpirationAttributes entryIdleTimeout);

	void setEntryTimeToLive(GudExpirationAttributes entryTimeToLive);

	void setRegionIdleTimeout(GudExpirationAttributes regionIdleTimeout);

	void setRegionTimeToLive(GudExpirationAttributes regionTimeToLive);

}
