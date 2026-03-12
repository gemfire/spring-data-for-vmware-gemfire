/*
 * Copyright $originalComment.match(" (\d+)", 1, "-", $today.year)2026 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

/*
 * @AI-Generated
 * Generated in whole or in part by Cursor
 * Description:
 * 2026-03-11: Migrated from org.apache.geode imports to GUD API types
 */

package org.springframework.data.gemfire.repository.query;

import org.springframework.data.gemfire.gud.api.GudRegion;

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
	 * Returns the name of the {@link GudRegion} the entity is held in.
	 *
	 * @return the name of the {@link GudRegion} the entity is held in.
	 */
	String getRegionName();

}
