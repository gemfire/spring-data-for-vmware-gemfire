/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire;

import org.apache.geode.cache.AttributesFactory;
import org.apache.geode.cache.RegionFactory;

/**
 * The {@link IndexMaintenancePolicyType} enum is a enumerated type of GemFire Index maintenance update options.
 *
 * @author John Blum
 * @see AttributesFactory#setIndexMaintenanceSynchronous(boolean)
 * @see org.apache.geode.cache.RegionAttributes#getIndexMaintenanceSynchronous()
 * @see RegionFactory#setIndexMaintenanceSynchronous(boolean)
 * @since 1.6.0
 */
@SuppressWarnings("unused")
public enum IndexMaintenancePolicyType {

	SYNCHRONOUS,
	ASYNCHRONOUS;

	public static final IndexMaintenancePolicyType DEFAULT = IndexMaintenancePolicyType.SYNCHRONOUS;

	/**
	 * Return an IndexMaintenanceType enumerated value given a case-insensitive, named String value
	 * describing the type of Index maintenance.
	 *
	 * @param name the String value indicating the type of Index maintenance (update).
	 * @return an IndexMaintenanceType enumerated value given a case-insensitive, named String value describing
	 * the type of Index maintenance, or null if no match was found.
	 * @see String#equalsIgnoreCase(String)
	 * @see #name()
	 */
	public static IndexMaintenancePolicyType valueOfIgnoreCase(String name) {

		for (IndexMaintenancePolicyType indexMaintenancePolicyType : values()) {
			if (indexMaintenancePolicyType.name().equalsIgnoreCase(name)) {
				return indexMaintenancePolicyType;
			}
		}

		return null;
	}

	/**
	 * Sets the GemFire AttributesFactory's 'indexMaintenanceSynchronous' property appropriately based on
	 * this IndexMaintenancePolicyType.
	 *
	 * @param attributesFactory the AttributesFactory instance on which to set the indexMaintenanceProperty.
	 * @throws NullPointerException if the AttributesFactory reference is null.
	 * @see #setIndexMaintenance(RegionFactory)
	 */
	@SuppressWarnings("deprecation")
	public void setIndexMaintenance(AttributesFactory<?, ?> attributesFactory) {
		attributesFactory.setIndexMaintenanceSynchronous(equals(SYNCHRONOUS));
	}

	/**
	 * Sets the GemFire RegionFactory's 'indexMaintenanceSynchronous' property appropriately based on
	 * this IndexMaintenancePolicyType.
	 *
	 * @param regionFactory the RegionFactory instance on which to set the indexMaintenanceProperty.
	 * @throws NullPointerException if the RegionFactory reference is null.
	 * @see #setIndexMaintenance(AttributesFactory)
	 */
	public void setIndexMaintenance(RegionFactory<?, ?> regionFactory) {
		regionFactory.setIndexMaintenanceSynchronous(equals(SYNCHRONOUS));
	}
}
