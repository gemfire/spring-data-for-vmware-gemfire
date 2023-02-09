/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.eviction;

import org.apache.geode.cache.EvictionAttributes;
import org.apache.geode.cache.Region;

import org.springframework.beans.factory.FactoryBean;

/**
 * The {@link EvictingRegionFactoryBean} interface specifies {@link Region} {@link FactoryBean FactoryBeans} capable
 * of supporting Eviction configuration, that is, evicting {@link Region} entries.
 *
 * @author John Blum
 * @see org.apache.geode.cache.EvictionAttributes
 * @see org.apache.geode.cache.Region
 * @since 2.1.0
 */
public interface EvictingRegionFactoryBean {

	void setEvictionAttributes(EvictionAttributes evictionAttributes);

}
