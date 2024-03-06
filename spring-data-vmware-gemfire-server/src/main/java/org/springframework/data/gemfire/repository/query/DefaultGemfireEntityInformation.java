/*
 * Copyright (c) VMware, Inc. 2022-2024. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository.query;

import org.springframework.data.gemfire.mapping.GemfirePersistentEntity;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.support.PersistentEntityInformation;
import org.springframework.lang.NonNull;

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
	 * Constructs a new instance of {@link DefaultGemfireEntityInformation}
	 * for the given {@link GemfirePersistentEntity}.
	 *
	 * @param entity {@link GemfirePersistentEntity} to wrap; must not be {@literal null}.
	 * @see GemfirePersistentEntity
	 */
	public DefaultGemfireEntityInformation(@NonNull GemfirePersistentEntity<T> entity) {
		super(entity);
		this.entity = entity;
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public String getRegionName() {
		return entity.getRegionName();
	}
}
