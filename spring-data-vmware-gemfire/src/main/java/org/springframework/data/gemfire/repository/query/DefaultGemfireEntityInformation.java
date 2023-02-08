/*
 * Copyright (c) VMware, Inc. 2022-2023. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query;

import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.support.PersistentEntityInformation;

/**
 * Implementation of {@link GemfireEntityInformation} and Spring Data's {@link PersistentEntityInformation}
 * that returns the Region name associated with the {@link PersistentEntity}.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see GemfirePersistentEntity
 * @see GemfireEntityInformation
 * @see PersistentEntityInformation
 */
// TODO: Move to org.springframework.data.gemfire.repository.core.support
public class DefaultGemfireEntityInformation<T, ID> extends PersistentEntityInformation<T, ID>
		implements GemfireEntityInformation<T, ID> {

	private final GemfirePersistentEntity<T> entity;

	/**
	 * Creates a new {@link DefaultGemfireEntityInformation}.
	 *
	 * @param entity must not be {@literal null}.
	 */
	public DefaultGemfireEntityInformation(GemfirePersistentEntity<T> entity) {
		super(entity);
		this.entity = entity;
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.gemfire.repository.query.GemfireEntityInformation#getRegionName()
	 */
	@Override
	public String getRegionName() {
		return entity.getRegionName();
	}
}
