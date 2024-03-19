/*
 * Copyright 2022-2024 Broadcom. All rights reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package org.springframework.data.gemfire.repository;

import org.springframework.data.repository.PagingAndSortingRepository;

/**
 * Apache Geode extension of the Spring Data {@link PagingAndSortingRepository} interface.
 *
 * @author Oliver Gierke
 * @author John Blum
 * @see org.springframework.data.repository.PagingAndSortingRepository
 */
public interface GemfireRepository<T, ID> extends PagingAndSortingRepository<T, ID> {

	/**
	 * Save the entity wrapped by the given {@link Wrapper}.
	 *
	 * @param wrapper {@link Wrapper} object wrapping the entity and the identifier of the entity (i.e. key).
	 * @return the saved entity.
	 * @see org.springframework.data.gemfire.repository.Wrapper
	 */
	T save(Wrapper<T, ID> wrapper);

}
