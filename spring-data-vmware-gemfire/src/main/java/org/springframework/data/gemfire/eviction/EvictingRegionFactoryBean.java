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

package org.springframework.data.gemfire.eviction;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.gemfire.gud.api.GudEvictionAttributes;
import org.springframework.data.gemfire.gud.api.GudRegion;

/**
 * The {@link EvictingRegionFactoryBean} interface specifies {@link GudRegion} {@link FactoryBean FactoryBeans} capable
 * of supporting Eviction configuration, that is, evicting {@link GudRegion} entries.
 *
 * @author John Blum
 * @see GudEvictionAttributes
 * @see GudRegion
 * @since 2.1.0
 */
public interface EvictingRegionFactoryBean {

	void setEvictionAttributes(GudEvictionAttributes evictionAttributes);

}
