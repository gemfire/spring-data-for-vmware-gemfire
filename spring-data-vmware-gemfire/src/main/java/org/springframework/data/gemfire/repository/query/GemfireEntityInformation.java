/*
 * Copyright (c) VMware, Inc. 2022. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package org.springframework.data.gemfire.repository.query;

import org.apache.geode.cache.Region;

import org.springframework.data.repository.core.EntityInformation;

/**
 * {@link EntityInformation} capturing GemFire specific information.
 *
 * @author Oliver Gierke
 * @see EntityInformation
 */
// TODO: Move to org.springframework.data.gemfire.repository.core
public interface GemfireEntityInformation<T, ID> extends EntityInformation<T, ID> {

	/**
	 * Returns the name of the {@link Region} the entity is held in.
	 *
	 * @return the name of the {@link Region} the entity is held in.
	 */
	String getRegionName();

}
